package abp.handlers;

import abp.AlternatingBitProtocol;
import abp.common.Globals;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.babel.generic.ProtoTimer;
import pt.unl.fct.di.novasys.babel.handlers.TimerHandler;

import static jatyc.lib.Utils.nonNull;

public class BitTimerHandler<V extends ProtoTimer> implements TimerHandler<V> {
    private static final Logger logger = nonNull(LogManager.getLogger(BitTimerHandler.class));

    private final AlternatingBitProtocol protocol;

    public BitTimerHandler(AlternatingBitProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void uponTimer(V v, long l) {
        logger.info(Globals.SENDING_BIT_MESSAGE);
        protocol.sendMessagePublic(protocol.getToSendMessage(), protocol.getReceiver());
    }
}
