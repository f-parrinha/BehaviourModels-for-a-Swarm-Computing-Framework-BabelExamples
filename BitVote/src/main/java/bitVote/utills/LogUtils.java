package bitVote.utills;

import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.network.data.Host;

public class LogUtils {

    public static void ReceivedMessage(Logger logger,Host receiver, Host sender, ProtoMessage message) {
        logger.info("({}) Received message '{}' from '{}: {}", receiver, message.getClass().getSimpleName(), sender, message);
    }

    public static void SendingMessage(Logger logger, Host sender, Host receiver, ProtoMessage message) {
        logger.info("({}) Sending message '{}' to '{}: {}", sender, message.getClass().getSimpleName(), receiver, message);
    }
}
