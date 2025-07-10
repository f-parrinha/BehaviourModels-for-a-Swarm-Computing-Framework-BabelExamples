package abp.utils;

import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.network.data.Host;

public class LogUtils {

    public static void ReceivedMessage(Logger logger, Host sender, ProtoMessage message) {
        logger.info("Received message '{}' from '{}: {}'", message.getClass().getName(), sender, message);
    }

    public static void SendingMessage(Logger logger, Host target, ProtoMessage message) {
        logger.info("Sending message '{}' to '{}: {}'", message.getClass().getName(), target, message);
    }
}
