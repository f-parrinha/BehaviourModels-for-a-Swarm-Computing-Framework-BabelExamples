package numberSet.utils;

import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.network.data.Host;

public class LogUtils {

    public static void ReceivedMessage(Logger logger, Host receiver, Host sender, ProtoMessage message) {
        logger.info("({}) Received message '{}' from '{}: {}'", receiver, message.getClass().getName(), sender, message);
    }

    public static void SendingMessage(Logger logger, Host receiver, Host target, ProtoMessage message) {
        logger.info("({}) Sending message '{}' to '{}: {}'", receiver, message.getClass().getName(), target, message);
    }
}
