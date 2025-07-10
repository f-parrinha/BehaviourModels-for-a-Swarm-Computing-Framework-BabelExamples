package numberSet.handlers.messages;

import numberSet.NumberSetProtocol;
import numberSet.common.Globals;
import numberSet.messages.SetRecoveryMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.babel.handlers.MessageInHandler;
import pt.unl.fct.di.novasys.network.data.Host;

import static jatyc.lib.Utils.nonNull;

public class SetRecoveryMessageHandler <T extends ProtoMessage> implements MessageInHandler<T> {
    private static final Logger logger = nonNull(LogManager.getLogger(SetRecoveryMessageHandler.class));
    private final NumberSetProtocol protocol;

    public SetRecoveryMessageHandler(NumberSetProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void receive(T t, Host host, short protoId, int channelId) {
        if (!(t instanceof SetRecoveryMessage)) {
            logger.warn(Globals.WRONG_CLASS_FOR_HANDLER_MSG);
            return;
        }


        var message = (SetRecoveryMessage) t;
        var numberSet = protocol.updateNumberSet(message.getNumbers());
        logger.info("({}) Received recovery. Current set: {}", protocol.getMyself(), numberSet);
    }
}
