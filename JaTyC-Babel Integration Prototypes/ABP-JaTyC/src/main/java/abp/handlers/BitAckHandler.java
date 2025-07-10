package abp.handlers;

import abp.AlternatingBitProtocol;
import abp.common.Globals;
import abp.messages.BitAck;
import abp.messages.BitSend;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.babel.handlers.MessageInHandler;
import pt.unl.fct.di.novasys.network.data.Host;

import static jatyc.lib.Utils.nonNull;

public class BitAckHandler<T extends ProtoMessage> implements MessageInHandler<T> {
    private static final Logger logger = nonNull(LogManager.getLogger(BitAckHandler.class));

    private final AlternatingBitProtocol protocol;

    public BitAckHandler(AlternatingBitProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void receive(T t, Host host, short i, int i1) {
        if (!(t instanceof BitAck)) {
            logger.warn(Globals.WRONG_CLASS_FOR_HANDLER_MSG);
            return;
        }

        logger.info(Globals.RECEIVED_MESSAGE, host, t);

        var message = (BitAck) t;
        var toSendMessage = protocol.getToSendMessage();
        if (message.getBit() == toSendMessage.getBit()) {
            toSendMessage.flip();
        }
    }
}