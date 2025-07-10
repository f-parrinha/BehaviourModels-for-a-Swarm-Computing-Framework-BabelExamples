package numberSet.common.utils;

import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.network.data.Host;

import static jatyc.lib.Utils.nonNull;

public class LogUtils {

    public static void ReceivedMessage(Logger logger,Host receiver, Host sender, ProtoMessage message) {
        var msgClass = nonNull(message.getClass());
        var msgClassName = nonNull(msgClass.getSimpleName());

        logger.info("({}) Received message '{}' from '{}: {}", receiver, msgClassName, sender, message);
    }

    public static void SendingMessage(Logger logger, Host sender, Host receiver, ProtoMessage message) {
        var msgClass = nonNull(message.getClass());
        var msgClassName = nonNull(msgClass.getSimpleName());

        logger.info("({}) Sending message '{}' to '{}: {}", sender, msgClassName, receiver, message);
    }
}
