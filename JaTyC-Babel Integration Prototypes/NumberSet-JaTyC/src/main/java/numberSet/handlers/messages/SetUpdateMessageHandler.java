package numberSet.handlers.messages;

import numberSet.NumberSetProtocol;
import numberSet.common.Globals;
import numberSet.messages.SetAckMessage;
import numberSet.messages.SetUpdateMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.babel.handlers.MessageInHandler;
import pt.unl.fct.di.novasys.network.data.Host;

import static jatyc.lib.Utils.nonNull;

public class SetUpdateMessageHandler<T extends ProtoMessage> implements MessageInHandler<T> {
    private static final Logger logger = nonNull(LogManager.getLogger(SetUpdateMessageHandler.class));
    private final NumberSetProtocol protocol;

    public SetUpdateMessageHandler(NumberSetProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void receive(T t, Host host, short protoId, int channelId) {
        if (!(t instanceof SetUpdateMessage)) {
            logger.warn(Globals.WRONG_CLASS_FOR_HANDLER_MSG);
            return;
        }

        var message = (SetUpdateMessage) t;
        Host myself = protocol.getMyself();

        logger.info(Globals.RECEIVED_MESSAGE, myself, host, message);
        protocol.updateNumberSet(message.getSetIterator());
        protocol.sendMessagePublic(new SetAckMessage(message.getNumbers()), host, channelId);
    }
}
