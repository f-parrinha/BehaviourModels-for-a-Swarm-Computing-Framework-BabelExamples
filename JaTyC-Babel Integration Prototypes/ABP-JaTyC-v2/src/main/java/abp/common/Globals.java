package abp.common;

import java.net.InetAddress;

import static jatyc.lib.Utils.nonNull;

public class Globals {

    /* COMMON */
    public static final String EMPTY_STRING = "";
    public static final InetAddress LOOPBACK = nonNull(InetAddress.getLoopbackAddress());
    public static final int DEFAULT_PORT = 9090;

    /* MESSAGES */
    public static final String WRONG_CLASS_FOR_HANDLER_MSG = "Wrong class type for this handler";
    public static final String NULL_INET_ADDRESS_MSG = "InetAddress is null. No InetAddress for the given address and no Loopback address found";
    public static final String SENDING_BIT_MESSAGE = "Sending a new BitMessage...";
    public static final String RECEIVED_MESSAGE = "Received message from '{}: {}'";
}
