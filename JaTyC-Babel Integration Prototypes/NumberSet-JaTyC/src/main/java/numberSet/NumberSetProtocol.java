package numberSet;

import jatyc.lib.Requires;
import numberSet.common.LoopbackHost;
import numberSet.common.NetworkEndpoint;
import numberSet.common.PropertiesReader;
import numberSet.common.PublicGenericProtocol;
import numberSet.common.utils.GenericUtils;
import numberSet.exceptions.BadNumberSetUpdate;
import numberSet.handlers.messages.SetAckMessageHandler;
import numberSet.handlers.messages.SetRecoveryMessageHandler;
import numberSet.handlers.messages.SetRecoveryRequestHandler;
import numberSet.handlers.messages.SetUpdateMessageHandler;
import numberSet.handlers.timers.ConnectionRetryHandler;
import numberSet.handlers.OutConnectionFailedHandler;
import numberSet.handlers.OutConnectionUpHandler;
import numberSet.handlers.timers.SendTimerHandler;
import numberSet.messages.SetAckMessage;
import numberSet.messages.SetRecoveryMessage;
import numberSet.messages.SetRecoveryRequest;
import numberSet.messages.SetUpdateMessage;
import numberSet.serializers.SetAckMessageSerializer;
import numberSet.serializers.SetRecoveryMessageSerializer;
import numberSet.serializers.SetRecoveryRequestSerializer;
import numberSet.serializers.SetUpdateMessageSerializer;
import numberSet.session.PeerSession;
import numberSet.session.SessionController;
import numberSet.timers.ConnectionRetryTimer;
import numberSet.timers.SendTimer;
import numberSet.update.SetUpdate;
import numberSet.update.ValueUpdate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.babel.exceptions.HandlerRegistrationException;
import pt.unl.fct.di.novasys.channel.tcp.events.OutConnectionFailed;
import pt.unl.fct.di.novasys.channel.tcp.events.OutConnectionUp;
import pt.unl.fct.di.novasys.network.data.Host;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static jatyc.lib.Utils.nonNull;


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
public class NumberSetProtocol extends PublicGenericProtocol {
    private static final short PROTO_ID = 100;
    private static final String PROTO_NAME = "NumberSetProtocol";
    private static final Logger logger = nonNull(LogManager.getLogger(NumberSetProtocol.class));

    private final Random rand;

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

        // Init all variables as they are not Nullable
        connections = 0;
        myself = new LoopbackHost();
        leader = new LoopbackHost();
        peers = new LinkedList<>();
        sessionController = new SessionController(0);
        numberSet = new TreeSet<>();
        uniqueNumbers = new LinkedList<>();
        rand = new Random();
    }

    @Override
    public void init(Properties properties) throws UnknownHostException, HandlerRegistrationException {
        PropertiesReader propertiesReader = PropertiesReader.create(properties);
        String address = propertiesReader.read("address");
        String port = propertiesReader.read("port");
        String peers = propertiesReader.read("peers");
        String leaderPort = propertiesReader.read("leader_port");
        String setSize = propertiesReader.read("set_size");

        // Setup networking
        NetworkEndpoint myEndpoint = NetworkEndpoint.create(address, port);
        InetAddress inetAddress = myEndpoint.getInetAddress();
        int channelId = createTcpChannel(myEndpoint);

        this.peers = GenericUtils.setupPeersList(peers);
        this.myself = new Host(inetAddress, Integer.parseInt(port));
        this.leader = new Host(inetAddress, Integer.parseInt(leaderPort));

        // Setup state
        this.numberSet = new TreeSet<>();
        if (myself.equals(leader)) {
            this.uniqueNumbers = GenericUtils.createPossibleNumberSet(Integer.parseInt(setSize));
            this.sessionController = SessionController.create(this.peers);
        }


        // register channel event handlers
        registerChannelEventHandler(channelId, OutConnectionFailed.EVENT_ID, new OutConnectionFailedHandler<>(this));
        registerChannelEventHandler(channelId, OutConnectionUp.EVENT_ID, new OutConnectionUpHandler<>(this));

        // Register message handlers and serializer
        registerMessageHandler(channelId, SetUpdateMessage.UID, new SetUpdateMessageHandler<>(this));
        registerMessageHandler(channelId, SetAckMessage.UID, new SetAckMessageHandler<>(this));
        registerMessageHandler(channelId, SetRecoveryRequest.UID, new SetRecoveryRequestHandler<>(this));
        registerMessageHandler(channelId, SetRecoveryMessage.UID, new SetRecoveryMessageHandler<>(this));
        registerMessageSerializer(channelId, SetUpdateMessage.UID, new SetUpdateMessageSerializer<>());
        registerMessageSerializer(channelId, SetAckMessage.UID, new SetAckMessageSerializer<>());
        registerMessageSerializer(channelId, SetRecoveryRequest.UID, new SetRecoveryRequestSerializer<>());
        registerMessageSerializer(channelId, SetRecoveryMessage.UID, new SetRecoveryMessageSerializer<>());

        // Register timers
        registerTimerHandler(SendTimer.ID, new SendTimerHandler<>(this));
        registerTimerHandler(ConnectionRetryTimer.ID, new ConnectionRetryHandler<>(this));

        // Finally connect
        connect();
    }

    /**
     * Connects itself to the P2P network
     */
    public void connect() {
        for(Host peer : this.peers) {
            if (peer == null) {
                logger.warn("Found a Null peer. This should never happen");
                continue;
            }
            if (peer.equals(myself)){
                continue;
            }

            logger.info("({}) Opened new connection to {}", myself, peer);
            openConnection(peer);
        }
    }

    /**
     * Sends a SetUpdate message to all peers, excluding the sender
     * @param update SetUpdate type
     */
    public void broadcastSetUpdate(SetUpdate update) {
        for (Host peer : peers) {
            if (peer == null || peer.equals(myself)) continue;

            PeerSession session = sessionController.get(peer);
            if (update instanceof ValueUpdate) {
                var valueUpdate = (ValueUpdate) update;
                session.add(valueUpdate.getValue());
            }

            sendMessage(new SetUpdateMessage(session.getToAck()), peer);
        }
    }

    /* SESSION METHODS */

    /**
     * Checks whether all sessions have received their awaited ACKs from SetUpdates
     * @return true if all sessions received all required ACKs, false if not
     */
    public boolean isSessionComplete() {
        return sessionController.isComplete();
    }

    public void sessionAck(Host sender, SetAckMessage message) {
        PeerSession session = sessionController.get(sender);
        session.ack(message);
    }

    /* PEER METHODS */

    public void increaseConnectionsCount() {
        connections++;
    }

    public void decreaseConnectionCount() {
        connections--;
    }

    public int getConnections() {
        return connections;
    }

    public int peersSize() {
        return peers.size();
    }

    /* HOST METHODS*/

    public boolean isLeader() {
        return myself.equals(leader);
    }

    public Host getMyself() {
        return myself;
    }

    public Host getLeader() {
        return leader;
    }

    /* SET METHODS */

    public boolean isNumberSetEmpty() {
        return numberSet.isEmpty();
    }

    public boolean isUniqueNumbersEmpty() {
        return uniqueNumbers.isEmpty();
    }

    public List<Integer> numberSetToList() {
        return new LinkedList<>(numberSet);
    }

    /**
     * Removes one unique number from a random index position, and adds it to the NumberSet
     * @return value added to the number set
     * @throws BadNumberSetUpdate
     */
    public Integer updateNumberSet() throws BadNumberSetUpdate {
        int idx = rand.nextInt(uniqueNumbers.size());
        Integer val = uniqueNumbers.remove(idx);
        if (val == null) {
            throw new BadNumberSetUpdate();
        }

        numberSet.add(val);
        return val;
    }

    /**
     * Updates the number set using a received iterator for a list/set of numbers to update
     * @param iterator to update list's iterator
     */
    public Set<Integer> updateNumberSet(Iterator<Integer> iterator) {
        while(iterator.hasNext()) {
            var next = iterator.next();
            if (next == null) continue;
            numberSet.add(next);
        }

        logger.info("({}) Current set: {}", myself, numberSet);
        return numberSet;
    }

    /**
     * Updates the number set, adding all number from a given list
     * @param toAdd list to with numbers to add
     */
    public Set<Integer> updateNumberSet(List<Integer> toAdd) {
        numberSet.addAll(toAdd);
        logger.info("({}) Current set: {}", myself, numberSet);
        return numberSet;
    }


    /* AUX METHODS */

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