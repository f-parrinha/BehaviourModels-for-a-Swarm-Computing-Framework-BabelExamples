package abp;

import abp.common.LoopbackHost;
import abp.common.PublicGenericProtocol;
import abp.common.PropertiesReader;
import abp.common.NetworkEndpoint;
import abp.handlers.*;
import abp.messages.BitAck;
import abp.messages.BitMessage;
import abp.serializers.BitAckSerializer;
import abp.serializers.BitSendSerializer;
import abp.timers.BitTimer;
import abp.timers.ConnectionRetryTimer;
import jatyc.lib.Requires;
import abp.messages.BitSend;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.babel.exceptions.HandlerRegistrationException;
import pt.unl.fct.di.novasys.channel.tcp.events.OutConnectionFailed;
import pt.unl.fct.di.novasys.channel.tcp.events.OutConnectionUp;
import pt.unl.fct.di.novasys.network.data.Host;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import static jatyc.lib.Utils.nonNull;


public class AlternatingBitProtocol extends PublicGenericProtocol {
    private static final short PROTO_ID = 100;
    private static final String PROTO_NAME = "Alternating Bit Protocol";
    private static final Logger logger = nonNull(LogManager.getLogger(AlternatingBitProtocol.class));

    // Networking
    private Host sender;
    private Host receiver;
    private Host myself;

    // State
    private BitSend toSendMessage;
    private int lastAckBit;

    public AlternatingBitProtocol() {
        super(PROTO_NAME, PROTO_ID);

        // Setup to avoid nullables
        sender = new LoopbackHost();
        receiver = new LoopbackHost();
        myself = new LoopbackHost();
        toSendMessage = new BitSend();
        lastAckBit = 0;
    }

    @Override
    public void init(Properties properties) throws UnknownHostException, HandlerRegistrationException {
        PropertiesReader propsReader = PropertiesReader.create(properties);
        String myPort = propsReader.read("port");
        String address = propsReader.read("address");
        String senderPort = propsReader.read("sender_port");
        String receiverPort = propsReader.read("receiver_port");

        // Networking
        NetworkEndpoint networkEndpoint = NetworkEndpoint.create(address, myPort);
        InetAddress inetAddress = networkEndpoint.getInetAddress();
        int channelId = createTcpChannel(networkEndpoint);

        this.myself = new Host(inetAddress, Integer.parseInt(myPort));
        this.receiver = new Host(inetAddress, Integer.parseInt(receiverPort));
        this.sender = new Host(inetAddress, Integer.parseInt(senderPort));

        // State
        this.lastAckBit = BitMessage.STARTING_RECEIVER_BIT;
        this.toSendMessage = new BitSend(BitMessage.STARING_SENDING_BIT);


        // register channel event handlers
        registerChannelEventHandler(channelId, OutConnectionFailed.EVENT_ID, new OutConnectionFailedHandler<>(this));
        registerChannelEventHandler(channelId, OutConnectionUp.EVENT_ID, new OutConnectionUpHandler<>(this));

        // Register timers
        registerTimerHandler(ConnectionRetryTimer.ID, new ConnectionRetryHandler<>(this));
        registerTimerHandler(BitTimer.ID, new BitTimerHandler<>(this));

        // Register message handlers and serializers
        registerMessageHandler(channelId, BitSend.ID, new BitSendHandler<>(this));
        registerMessageHandler(channelId, BitAck.ID, new BitAckHandler<>(this));
        registerMessageSerializer(channelId, BitSend.ID, new BitSendSerializer<>());
        registerMessageSerializer(channelId, BitAck.ID, new BitAckSerializer<>());


        // Finally connect
        connect();
    }

    public void connect() {
        if (myself == null || receiver == null || sender == null) return;

        if (myself.equals(receiver)) {
            openConnection(sender);
        } else {
            openConnection(receiver);
        }
    }

    public void startSend() {
        if (myself.equals(sender)) {
            setupPeriodicTimer(new BitTimer(), 0, 1000);
        }
    }

    public Host getReceiver() {
        return receiver;
    }

    public Host getSender() {
        return sender;
    }

    public BitSend getToSendMessage() {
        return toSendMessage;
    }

    public int getLastAckBit() {
        return lastAckBit;
    }

    public void setLastAckBit(int bit) {
        lastAckBit = bit;
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
