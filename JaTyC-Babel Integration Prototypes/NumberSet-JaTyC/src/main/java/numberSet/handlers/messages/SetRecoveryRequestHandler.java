package numberSet.handlers.messages;

import numberSet.NumberSetProtocol;
import numberSet.common.Globals;
import numberSet.messages.SetRecoveryMessage;
import numberSet.messages.SetRecoveryRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.babel.handlers.MessageInHandler;
import pt.unl.fct.di.novasys.network.data.Host;

import java.util.LinkedList;

import static jatyc.lib.Utils.nonNull;

public class SetRecoveryRequestHandler <T extends ProtoMessage> implements MessageInHandler<T> {
    private static final Logger logger = nonNull(LogManager.getLogger(SetRecoveryRequestHandler.class));
    private final NumberSetProtocol protocol;

    public SetRecoveryRequestHandler(NumberSetProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void receive(T t, Host host, short protoId, int channelId) {
        if (!(t instanceof SetRecoveryRequest)) {
            logger.warn(Globals.WRONG_CLASS_FOR_HANDLER_MSG);
            return;
        }

        var message = (SetRecoveryRequest) t;
        logger.info(Globals.RECEIVED_MESSAGE, protocol.getMyself(), host, message);
        protocol.sendMessagePublic(new SetRecoveryMessage(new LinkedList<>(protocol.numberSetToList())), host);
    }
}
