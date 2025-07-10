package bitVote;

import bitVote.messages.VoteAck;
import bitVote.messages.VoteRequest;
import bitVote.messages.VoteWriteBack;
import bitVote.timers.ConnectionRetryTimer;
import bitVote.timers.VoteTimer;
import bitVote.utills.LogUtils;
import bitVote.utills.NetworkUtils;
import bitVote.voting.VoteRecord;
import bitVote.voting.VoteRound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.babel.core.GenericProtocol;
import pt.unl.fct.di.novasys.babel.exceptions.HandlerRegistrationException;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.channel.tcp.TCPChannel;
import pt.unl.fct.di.novasys.channel.tcp.events.*;
import pt.unl.fct.di.novasys.network.data.Host;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class BitVoteProtocol extends GenericProtocol {
    private static final short PROTO_ID = 100;
    private static final String PROTO_NAME = "BitVoteProtocol";
    private static final Logger logger = LogManager.getLogger(BitVoteProtocol.class);

    // Networking
    private List<Host> peers;
    private Host myself;
    private Host leader;
    private int connections;

    // State
    private Map<String, VoteRound> voteRounds;
    private Map<String, VoteRecord> voteHistory;
    private String currentRoundID;
    private List<Host> votesToReceive;

    // Common
    private Random rand;

    public BitVoteProtocol() {
        super(PROTO_NAME, PROTO_ID);
    }

    @Override
    public void init(Properties properties) throws UnknownHostException, HandlerRegistrationException {
        InetAddress address = InetAddress.getByName(properties.getProperty("address"));
        String port = properties.getProperty("port");
        String peers = properties.getProperty("peers");
        String leaderPort = properties.getProperty("leader_port");

        int channelId = createTcpChannel(address.getHostAddress(), port);

        // Networking
        this.peers = NetworkUtils.setupPeersList(peers);
        this.myself = new Host(address, Integer.parseInt(port));
        this.leader = new Host(address, Integer.parseInt(leaderPort));

        // State
        this.voteHistory = new LinkedHashMap<>();

        if (myself.equals(leader)) {
            this.voteRounds = new HashMap<>();
            this.currentRoundID = VoteRound.EMPTY_ID;
            this.votesToReceive = new ArrayList<>(this.peers.size());
        }

        // Common
        this.rand = new Random();

        // register channel event handlers
        registerChannelEventHandler(channelId, InConnectionDown.EVENT_ID, (InConnectionDown event, int channel) -> logger.debug(event));
        registerChannelEventHandler(channelId, InConnectionUp.EVENT_ID, (InConnectionUp event, int channel) -> logger.debug(event));
        registerChannelEventHandler(channelId, OutConnectionDown.EVENT_ID, (OutConnectionDown event, int channel) -> logger.debug(event));
        registerChannelEventHandler(channelId, OutConnectionFailed.EVENT_ID, this::uponOutConnectionFailed);
        registerChannelEventHandler(channelId, OutConnectionUp.EVENT_ID, this::uponOutConnectionUp);

        // Register message handlers and serializer
        registerMessageHandler(channelId, VoteRequest.ID, this::uponVoteRequest);
        registerMessageHandler(channelId, VoteAck.ID, this::uponVoteAck);
        registerMessageHandler(channelId, VoteWriteBack.ID, this::uponVoteWriteBack);
        registerMessageSerializer(channelId, VoteRequest.ID, VoteRequest.serializer);
        registerMessageSerializer(channelId, VoteAck.ID, VoteAck.serializer);
        registerMessageSerializer(channelId, VoteWriteBack.ID, VoteWriteBack.serializer);

        // Register timers
        registerTimerHandler(VoteTimer.ID, this::uponVoteTimer);
        registerTimerHandler(ConnectionRetryTimer.ID, this::uponConnectionRetryTimer);

        // Finally openConnection and wait a bit
        for(Host peer : this.peers) {
            openConnection(peer);
        }
    }


    /* CONNECTION EVENT HANDLERS */

    private void uponOutConnectionUp(OutConnectionUp event, int channel) {
        logger.info(event);
        connections++;

        if (connections == peers.size() && myself.equals(leader)) {
            setupPeriodicTimer(new VoteTimer(), 0,1000);
        }
    }
    private void uponOutConnectionFailed(OutConnectionFailed<ProtoMessage> event, int channel) {
        logger.info("Connection failed. Retrying...");

        setupTimer(new ConnectionRetryTimer(event.getNode(), channel), 2000);
    }

    /* MESSAGE HANDLERS */

    private void uponVoteRequest(VoteRequest message, Host sender, short sourceProto, int channelId) {
        LogUtils.ReceivedMessage(logger, myself, sender, message);

        VoteRecord voteRecord;
        String roundID = message.getRoundID();
        if(voteHistory.containsKey(roundID)) {
            voteRecord = voteHistory.get(roundID);
        } else {
            voteRecord = new VoteRecord(roundID, rand.nextInt(2));
            voteHistory.put(roundID, voteRecord);
        }

        logger.info("({}) I vote '{}'", myself, voteRecord.getVotedBit());
        sendMessage(new VoteAck(roundID, voteRecord.getVotedBit()), sender);
    }
    private void uponVoteAck(VoteAck message, Host sender, short sourceProto, int channelId) {
        LogUtils.ReceivedMessage(logger, myself, sender, message);

        String roundID = message.getRoundID();
        if (!voteRounds.containsKey(roundID)) {
            logger.warn("({}) Received VoteAck for a round that does not exist from ({})" , myself, sender);
            return;
        }

        // Ack and finish round with SUCCESS, if that is the case
        VoteRound voteRound = voteRounds.get(roundID);
        voteRound.ack(sender, message);
    }
    private void uponVoteWriteBack(VoteWriteBack message, Host sender, short sourceProto, int channelId) {
        LogUtils.ReceivedMessage(logger, myself, sender, message);

        VoteRecord voteRecord;
        String roundID = message.getRoundID();
        if (voteHistory.containsKey(roundID)) {
            voteRecord = voteHistory.get(message.getRoundID());
        } else {
            logger.info("({}) Looks like i never participated in this vote round...", myself);
            voteRecord = new VoteRecord(roundID, VoteRound.UNDECIDED_BIT_VALUE);
            voteHistory.put(roundID, voteRecord);
        }

        // Update state of record
        voteRecord.setDecidedBit(message.getDecidedBit());
        if (message.getStatus().equals(VoteRound.Status.FAIL)) {
            voteRecord.fail();
        }
    }


    /* TIMER HANDLERS */

    private void uponVoteTimer(VoteTimer timer, long timerId) {
        VoteRound voteRound = voteRounds.get(currentRoundID);
        if (voteRound == null || !voteRound.isPending()) {
            voteRound = new VoteRound(leader, peers);
        }

        // Evaluate vote round status
        voteRound.evaluate();
        voteRound.retry();
        currentRoundID = voteRound.getRoundID();
        voteRounds.put(currentRoundID, voteRound);

        // Finally send correct message
        if (voteRound.isPending()) {
            broadcast(new VoteRequest(currentRoundID));
            logger.info("\nVote Round Retry - {}", voteRound);
        } else {
            broadcast(new VoteWriteBack(currentRoundID, voteRound.decide(), voteRound.getStatus()));
            logger.info("\nVote Round WriteBack - {}", voteRound);
        }
    }
    private void uponConnectionRetryTimer(ConnectionRetryTimer timer, long timerId) {
        openConnection(timer.getHost(), timer.getChannelId());
    }


    /* AUX METHODS */

    private void broadcast(ProtoMessage message) {
        for(Host peer : peers) {
            sendMessage(message, peer);
        }
    }
    private void resetVotesToReceive() {
        votesToReceive.clear();
        votesToReceive.addAll(peers);
    }
    private int createTcpChannel(String myAddress, String port) {
        try {
            var channelProps = new Properties();
            channelProps.setProperty(TCPChannel.ADDRESS_KEY, myAddress);
            channelProps.setProperty(TCPChannel.PORT_KEY, port);
            channelProps.setProperty(TCPChannel.HEARTBEAT_INTERVAL_KEY, "1000");
            channelProps.setProperty(TCPChannel.HEARTBEAT_TOLERANCE_KEY, "3000");
            channelProps.setProperty(TCPChannel.CONNECT_TIMEOUT_KEY, "1000");
            int channelId = createChannel(TCPChannel.NAME, channelProps);

            logger.info("TCP Channel on {}:{}", myAddress, port);
            return channelId;
        } catch (IOException e) {
            logger.error("Error while creating Server channel");
            e.fillInStackTrace();
            throw new RuntimeException(e);
        }
    }
}