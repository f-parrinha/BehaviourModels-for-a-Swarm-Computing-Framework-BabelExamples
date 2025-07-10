package abp;

import abp.messages.BitMessage;
import abp.timers.BitTimer;
import abp.timers.ConnectionRetryTimer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import abp.messages.BitAck;
import abp.messages.BitSend;
import abp.utils.LogUtils;
import pt.unl.fct.di.novasys.babel.core.GenericProtocol;
import pt.unl.fct.di.novasys.babel.exceptions.HandlerRegistrationException;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.channel.tcp.TCPChannel;
import pt.unl.fct.di.novasys.channel.tcp.events.*;
import pt.unl.fct.di.novasys.network.data.Host;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class AlternatingBitProtocol extends GenericProtocol {
    private static final short PROTO_ID = 100;
    private static final String PROTO_NAME = "AlternatingBitProtocol";
    private static final Logger logger = LogManager.getLogger(AlternatingBitProtocol.class);

    // Networking
    private Host sender;
    private Host receiver;
    private Host myself;

    // State
    private BitSend toSendMessage;
    private int lastAckBit;

    public AlternatingBitProtocol() {
        super(PROTO_NAME, PROTO_ID);
    }

    @Override
    public void init(Properties properties) throws UnknownHostException, HandlerRegistrationException {
        InetAddress address = InetAddress.getByName(properties.getProperty("address"));
        String myPort = properties.getProperty("port");
        String senderPort = properties.getProperty("sender_port");
        String receiverPort = properties.getProperty("receiver_port");
        int channelId = createTcpChannel(address.getHostAddress(), myPort);

        // Networking
        this.myself = new Host(address, Integer.parseInt(myPort));
        this.receiver = new Host(address, Integer.parseInt(receiverPort));
        this.sender = new Host(address, Integer.parseInt(senderPort));

        // State
        this.lastAckBit = BitMessage.STARTING_RECEIVER_BIT;
        this.toSendMessage = new BitSend(BitMessage.STARING_SENDING_BIT);


        // register channel event handlers
        registerChannelEventHandler(channelId, InConnectionDown.EVENT_ID, (InConnectionDown event, int channel) -> logger.info(event));
        registerChannelEventHandler(channelId, InConnectionUp.EVENT_ID, (InConnectionUp event, int channel) -> logger.info(event));
        registerChannelEventHandler(channelId, OutConnectionDown.EVENT_ID, (OutConnectionDown event, int channel) -> logger.info(event));
        registerChannelEventHandler(channelId, OutConnectionFailed.EVENT_ID, this::uponOutConnectionFailed);
        registerChannelEventHandler(channelId, OutConnectionUp.EVENT_ID, this::uponOutConnectionUp);

        // Register message handlers and serializer
        registerMessageHandler(channelId, BitSend.ID, this::uponBitSend);
        registerMessageHandler(channelId, BitAck.ID, this::uponBitAck);
        registerMessageSerializer(channelId, BitSend.ID, BitSend.serializer);
        registerMessageSerializer(channelId, BitAck.ID, BitAck.serializer);

        // Register timers
        registerTimerHandler(BitTimer.ID, this::uponBitTimer);
        registerTimerHandler(ConnectionRetryTimer.ID, this::uponConnectionRetryTimer);

        // Finally connect
        connectTo(myself.equals(receiver) ? sender : this.receiver);
    }

    /* TYPESTATE METHODS */

    public void connectTo(Host host) {
        openConnection(host);
    }

    public void startSend() {
        if (myself.equals(sender)) {
            setupPeriodicTimer(new BitTimer(), 0, 1000);
        }
    }



    /* CONNECTION EVENT HANDLERS */

    private void uponOutConnectionUp(OutConnectionUp event, int channel) {
        logger.info(event);

        startSend();
    }
    private void uponOutConnectionFailed(OutConnectionFailed<ProtoMessage> event, int channel) {
        logger.info("Connection failed. Retrying...");

        setupTimer(new ConnectionRetryTimer(), 2000);
    }

    /* MESSAGE HANDLERS */

    private void uponBitSend(BitSend message, Host sender, short sourceProto, int channelId) {
        LogUtils.ReceivedMessage(logger, sender, message);

        int receivedBit = message.getBit();
        if (lastAckBit != receivedBit) {
            lastAckBit = receivedBit;
            sendMessage(new BitAck(receivedBit), sender);
        } else {
            sendMessage(new BitAck(lastAckBit), sender);
            logger.info("Received a repeated bit");
        }
    }
    private void uponBitAck(BitAck message, Host sender, short sourceProto, int channelId) {
        LogUtils.ReceivedMessage(logger, sender, message);

        if (message.getBit() == toSendMessage.getBit()) {
            toSendMessage.flip();
        }
    }

    /* TIMER HANDLERS */

    private void uponBitTimer(BitTimer timer, long timerId) {
        sendMessage(toSendMessage, receiver);
    }
    private void uponConnectionRetryTimer(ConnectionRetryTimer timer, long timerId) {
        openConnection(this.receiver);
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
}

