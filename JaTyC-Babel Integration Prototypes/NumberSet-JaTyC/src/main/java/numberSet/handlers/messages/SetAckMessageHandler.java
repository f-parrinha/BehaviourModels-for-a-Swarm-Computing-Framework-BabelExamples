package numberSet.handlers.messages;

import numberSet.NumberSetProtocol;
import numberSet.common.Globals;
import numberSet.messages.SetAckMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.babel.handlers.MessageInHandler;
import pt.unl.fct.di.novasys.network.data.Host;

import static jatyc.lib.Utils.nonNull;

public class SetAckMessageHandler <T extends ProtoMessage> implements MessageInHandler<T> {
    private static final Logger logger = nonNull(LogManager.getLogger(SetAckMessageHandler.class));
    private final NumberSetProtocol protocol;

    public SetAckMessageHandler(NumberSetProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void receive(T t, Host host, short protoId, int channelId) {
        if (!(t instanceof SetAckMessage)) {
            logger.warn(Globals.WRONG_CLASS_FOR_HANDLER_MSG);
            return;
        }

        var message = (SetAckMessage) t;
        Host myself = protocol.getMyself();
        protocol.sessionAck(host, message);
        logger.info(Globals.RECEIVED_MESSAGE, myself, host, message);
    }
}
