package numberSet;

import numberSet.messages.SetAckMessage;
import numberSet.messages.SetRecoveryMessage;
import numberSet.messages.SetRecoveryRequest;
import numberSet.messages.SetUpdateMessage;
import numberSet.timers.ConnectionRetryTimer;
import numberSet.timers.SendTimer;
import numberSet.update.NullUpdate;
import numberSet.update.ValueUpdate;
import numberSet.utils.GenericUtils;
import numberSet.utils.LogUtils;
import numberSet.session.PeerSession;
import numberSet.session.SessionController;
import numberSet.update.SetUpdate;
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


/**
 * Class {@code NumberSetProtocol} handles the distribution of <i> unique </i> numbers to different peers in a small
 *  network.
 * <p>
 *     To make sure unique numbers are sent, a list is created to store all numbers up to N. Then, when a number is sent,
 *      it is removed from that list, instead of constantly retrying until a new unique number is found
*  </p>
 * <p> NOTE: Does not support leader crash </p>
 * <p>
 *     Supports lost ACKs and lost connections, via the usage of PeerSessions, to control which messages were sent and still need to be ACKed
 * </p>
 */
public class NumberSetProtocol extends GenericProtocol {
    private static final short PROTO_ID = 100;
    private static final String PROTO_NAME = "NumberSetProtocol";
    private static final Logger logger = LogManager.getLogger(NumberSetProtocol.class);

    // Networking
    private int connections;
    private Host myself;
    private Host leader;
    private List<Host> peers;
    private SessionController sessionController;

    // Number set
    private Set<Integer> numberSet;
    private List<Integer> uniqueNumbers;

    public NumberSetProtocol() {
        super(PROTO_NAME, PROTO_ID);
    }

    @Override
    public void init(Properties properties) throws UnknownHostException, HandlerRegistrationException {
        InetAddress address = InetAddress.getByName(properties.getProperty("address"));
        String port = properties.getProperty("port");
        String peers = properties.getProperty("peers");
        String leaderPort = properties.getProperty("leader_port");
        String setSize = properties.getProperty("set_size");

        // Setup networking
        int channelId = createTcpChannel(address.getHostAddress(), port);
        this.peers = GenericUtils.setupPeersList(peers);
        this.myself = new Host(address, Integer.parseInt(port));
        this.leader = new Host(address, Integer.parseInt(leaderPort));

        // Setup state
        this.numberSet = new TreeSet<>();
        if (myself.equals(leader)) {
            this.uniqueNumbers = GenericUtils.createPossibleNumberSet(Integer.parseInt(setSize));
            this.sessionController = new SessionController(this.peers);
        }

        // register channel event handlers
        registerChannelEventHandler(channelId, OutConnectionFailed.EVENT_ID, this::uponOutConnectionFailed);
        registerChannelEventHandler(channelId, OutConnectionUp.EVENT_ID, this::uponOutConnectionUp);

        // Register message handlers and serializer
        registerMessageHandler(channelId, SetUpdateMessage.UID, this::uponSetUpdateMessage);
        registerMessageHandler(channelId, SetAckMessage.UID, this::uponSetAckMessage);
        registerMessageHandler(channelId, SetRecoveryRequest.UID, this::uponSetRecoveryRequest);
        registerMessageHandler(channelId, SetRecoveryMessage.UID, this::uponSetRecoveryMessage);
        registerMessageSerializer(channelId, SetUpdateMessage.UID, SetUpdateMessage.serializer);
        registerMessageSerializer(channelId, SetAckMessage.UID, SetAckMessage.serializer);
        registerMessageSerializer(channelId, SetRecoveryRequest.UID, SetRecoveryRequest.serializer);
        registerMessageSerializer(channelId, SetRecoveryMessage.UID, SetRecoveryMessage.serializer);

        // Register timers
        registerTimerHandler(SendTimer.ID, this::uponSendTimer);
        registerTimerHandler(ConnectionRetryTimer.ID, this::uponConnectionRetryTimer);

        // Finally openConnection and wait a bit
        for(Host peer : this.peers) {
            if (peer.equals(myself)) continue;
            openConnection(peer);
        }
    }


    /* CONNECTION EVENT HANDLERS */

    private void uponOutConnectionUp(OutConnectionUp event, int channel) {
        connections++;

        // Only the leader sends updates
        if (connections == (peers.size() - 1) && myself.equals(leader)) {
            setupPeriodicTimer(new SendTimer(), 0, 1000);
        }

        // Request recovery
        if (numberSet.isEmpty() && event.getNode().equals(leader)) {
            sendMessage(new SetRecoveryRequest(), leader);
        }
    }
    private void uponOutConnectionFailed(OutConnectionFailed<ProtoMessage> event, int channel) {
        logger.info("Connection failed. Retrying...");

        setupTimer(new ConnectionRetryTimer(event.getNode(), channel), 2000);
    }


    /* MESSAGE HANDLERS */

    private void uponSetUpdateMessage(SetUpdateMessage message, Host sender, short sourceProto, int channelId) {
        LogUtils.ReceivedMessage(logger, myself, sender, message);

        Iterator<Integer> receivedIterator = message.getSetIterator();
        while(receivedIterator.hasNext()) {
            numberSet.add(receivedIterator.next());
        }

        logger.info("({}) Current set: {}", myself, numberSet);
        sendMessage(new SetAckMessage(message.getNumbers()), sender);
    }
    private void uponSetAckMessage(SetAckMessage message, Host sender, short sourceProto, int channelId) {
        LogUtils.ReceivedMessage(logger, myself, sender, message);

        PeerSession session = sessionController.get(sender);
        session.ack(message);
    }
    private void uponSetRecoveryRequest(SetRecoveryRequest message, Host sender, short sourceProto, int channelId) {
        LogUtils.ReceivedMessage(logger, myself, sender, message);

        PeerSession session = sessionController.get(sender);
        sendMessage(new SetRecoveryMessage(new LinkedList<>(numberSet)), sender);
    }
    private void uponSetRecoveryMessage(SetRecoveryMessage message, Host sender, short sourceProto, int channelId) {
        LogUtils.ReceivedMessage(logger, myself, sender, message);

        numberSet.addAll(message.getNumbers());
        logger.info("({}) Received recovery. Current set: {}", myself, numberSet);
    }


    /* TIMER HANDLERS */

    private void uponSendTimer(SendTimer timer, long timerId) {
        if (sessionController.isComplete() && uniqueNumbers.isEmpty()) {
            cancelTimer(timerId);
            logger.info("Finished sending all numbers!");
            return;
        }

        Random rand = new Random();
        SetUpdate update;

        // Retrieve next update from the "to send" list
        if (uniqueNumbers.isEmpty()) {
            update = new NullUpdate();
        } else {
            int idx = rand.nextInt(uniqueNumbers.size());
            int val = uniqueNumbers.remove(idx);
            numberSet.add(val);
            update = new ValueUpdate(val);
        }

        logger.info("({}) Leader Set: {}", myself, numberSet);
        broadcastSetUpdate(update);
    }
    private void uponConnectionRetryTimer(ConnectionRetryTimer timer, long timerId) {
        openConnection(timer.getHost(), timer.getChannelId());
    }

    /* AUX METHODS */

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


    private void broadcastSetUpdate(SetUpdate update) {
        for (Host peer : peers) {
            if (peer.equals(myself)) continue;

            PeerSession session = sessionController.get(peer);
            if (update instanceof ValueUpdate) {
                session.add(((ValueUpdate) update).getValue());
            }

            sendMessage(new SetUpdateMessage(session.getToAck()), peer);
        }
    }
}