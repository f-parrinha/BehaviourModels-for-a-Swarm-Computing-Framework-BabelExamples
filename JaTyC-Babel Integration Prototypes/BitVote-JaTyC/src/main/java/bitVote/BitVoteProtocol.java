package bitVote;

import bitVote.common.*;
import bitVote.common.utils.LogUtils;
import bitVote.common.utils.NetworkUtils;
import bitVote.handlers.messages.VoteAckHandler;
import bitVote.handlers.messages.VoteRequestHandler;
import bitVote.handlers.messages.VoteWriteBackHandler;
import bitVote.handlers.timers.ConnectionRetryHandler;
import bitVote.handlers.OutConnectionFailedHandler;
import bitVote.handlers.OutConnectionUpHandler;
import bitVote.handlers.timers.VoteTimerHandler;
import bitVote.messages.VoteAck;
import bitVote.messages.VoteRequest;
import bitVote.messages.VoteWriteBack;
import bitVote.serializers.VoteAckSerializer;
import bitVote.serializers.VoteRequestSerializer;
import bitVote.serializers.VoteWriteBackSerializer;
import bitVote.timers.ConnectionRetryTimer;
import bitVote.timers.VoteTimer;
import bitVote.voting.VoteRecord;
import bitVote.voting.VoteRound;
import jatyc.lib.Requires;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.babel.core.GenericProtocol;
import pt.unl.fct.di.novasys.babel.exceptions.HandlerRegistrationException;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.channel.tcp.events.*;
import pt.unl.fct.di.novasys.network.data.Host;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static jatyc.lib.Utils.nonNull;

public class BitVoteProtocol extends GenericProtocol {
    private static final short PROTO_ID = 100;
    private static final String PROTO_NAME = "BitVoteProtocol";
    private static final Logger logger = nonNull(LogManager.getLogger(BitVoteProtocol.class));

    private final Random rand;

    // State
    private Map<String, VoteRound> voteRounds;
    private Map<String, VoteRecord> voteHistory;
    private String currentRoundID;
    private List<Host> votesToReceive;

    // Networking
    private final List<Host> peers;
    private Host myself;
    private Host leader;
    private int connections;

    public BitVoteProtocol() {
        super(PROTO_NAME, PROTO_ID);

        rand = new Random();

        voteHistory = new LinkedHashMap<>();
        voteRounds = new HashMap<>(0);
        votesToReceive = new LinkedList<>();
        currentRoundID = VoteRound.EMPTY_ID;


        peers = new ArrayList<>();
        myself = new LoopbackHost();
        leader = new LoopbackHost();
        connections = 0;
    }

    @Override
    public void init(Properties properties) throws UnknownHostException, HandlerRegistrationException {
        PropertiesReader propertiesReader = PropertiesReader.create(properties);
        String address = propertiesReader.read("address");
        String port = propertiesReader.read("port");
        String peers = propertiesReader.read("peers");
        String leaderPort = propertiesReader.read("leader_port");

        // Networking
        NetworkEndpoint myNetEndpoint = NetworkEndpoint.create(address, port);
        InetAddress inetAddress = myNetEndpoint.getInetAddress();
        int channelId = createTcpChannel(myNetEndpoint);
        this.peers.addAll(NetworkUtils.setupPeersList(peers));
        this.myself = new Host(inetAddress, Integer.parseInt(port));
        this.leader = new Host(inetAddress, Integer.parseInt(leaderPort));

        // State
        if (myself.equals(leader)) {
            this.voteRounds = new HashMap<>();
            this.votesToReceive = new ArrayList<>(this.peers.size());
        }

        // register channel event handlers
        registerChannelEventHandler(channelId, OutConnectionFailed.EVENT_ID, new OutConnectionFailedHandler<>(this));
        registerChannelEventHandler(channelId, OutConnectionUp.EVENT_ID, new OutConnectionUpHandler<>(this));

        // Register message handlers and serializer
        registerMessageHandler(channelId, VoteRequest.ID, new VoteRequestHandler<>(this));
        registerMessageHandler(channelId, VoteAck.ID, new VoteAckHandler<>(this));
        registerMessageHandler(channelId, VoteWriteBack.ID, new VoteWriteBackHandler<>(this));
        registerMessageSerializer(channelId, VoteRequest.ID, new VoteRequestSerializer<>());
        registerMessageSerializer(channelId, VoteAck.ID, new VoteAckSerializer<>());
        registerMessageSerializer(channelId, VoteWriteBack.ID, new VoteWriteBackSerializer<>());

        // Register timers
        registerTimerHandler(ConnectionRetryTimer.ID, new ConnectionRetryHandler<>(this));
        registerTimerHandler(VoteTimer.ID, new VoteTimerHandler<>(this));

        // Finally... setup and join network
        joinNetwork();
    }


    /* CONNECTION EVENT HANDLERS */

    public void uponOutConnectionUp(OutConnectionUp event, int channel) {
        logger.info(event);
        connections++;

        if (connections == peers.size() && myself.equals(leader)) {
            setupPeriodicTimer(new VoteTimer(), 0,1000);
        }
    }
    public void uponOutConnectionFailed(OutConnectionFailed<ProtoMessage> event, int channel) {
        logger.info("Connection failed. Retrying...");

        setupTimer(new ConnectionRetryTimer(nonNull(event.getNode()), channel), 2000);
    }


    /* MESSAGE HANDLERS */

    public void uponVoteRequest(VoteRequest message, Host sender, short sourceProto, int channelId) {
        LogUtils.ReceivedMessage(logger, myself, sender, message);

        String roundID = message.getRoundID();
        VoteRecord voteRecord = voteHistory.get(roundID);
        if(voteRecord == null) {
            voteRecord = new VoteRecord(roundID, rand.nextInt(2));
            voteHistory.put(roundID, voteRecord);
            logger.info("({}) Starting a new Vote Round!", myself);
        }

        int votedBit = voteRecord.getVotedBit();
        logger.info("({}) I vote '{}'", myself, nonNull(votedBit));
        sendMessage(new VoteAck(roundID, votedBit), sender);
    }
    public void uponVoteAck(VoteAck message, Host sender, short sourceProto, int channelId) {
        LogUtils.ReceivedMessage(logger, myself, sender, message);

        String roundID = message.getRoundID();
        if (!voteRounds.containsKey(roundID)) {
            logger.warn("({}) Received VoteAck for a round that does not exist from ({})" , myself, sender);
            return;
        }

        // Ack and finish round with SUCCESS, if that is the case
        VoteRound voteRound = voteRounds.get(roundID);
        if (voteRound == null) {
            logger.warn("Found NULL VoteRound");
            return;
        }

        voteRound.ack(sender, message);
    }
    public void uponVoteWriteBack(VoteWriteBack message, Host sender, short sourceProto, int channelId) {
        LogUtils.ReceivedMessage(logger, myself, sender, message);

        String roundID = message.getRoundID();
        VoteRecord voteRecord = voteHistory.get(roundID);
        if (voteRecord == null) {
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

    public void uponVoteTimer(VoteTimer timer, long timerId) {
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
    public void uponConnectionRetryTimer(ConnectionRetryTimer timer, long timerId) {
        openConnection(timer.getHost(), timer.getChannelId());
    }


    /* AUX METHODS */

    private void broadcast(ProtoMessage message) {
        for(Host peer : peers) {
            if (peer == null) continue;
            sendMessage(message, peer);
        }
    }
    private void resetVotesToReceive() {
        votesToReceive.clear();
        votesToReceive.addAll(peers);
    }

    private void joinNetwork() {
        for(Host peer : this.peers) {
            if (peer == null) {
                throw new NullPointerException("Found a peer that is null");
            }
            openConnection(peer);
        }
    }

    private int createTcpChannel(@Requires("Exec") NetworkEndpoint netProps) {
        try {
            var channelProps = new Properties();
            channelProps.setProperty("address", netProps.getAddress());
            channelProps.setProperty("port", netProps.getPort());
            int channelId = createChannel("TCPChannel", channelProps);

            logger.info("TCP Channel on {}", netProps.getName());
            return channelId;
        } catch (IOException e) {
            logger.error("Error while creating TCP channel");
            e.fillInStackTrace();
            throw new RuntimeException(e);
        }
    }
}