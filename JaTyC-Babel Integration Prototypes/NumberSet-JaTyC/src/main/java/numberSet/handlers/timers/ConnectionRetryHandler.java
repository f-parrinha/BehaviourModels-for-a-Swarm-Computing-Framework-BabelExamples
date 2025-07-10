package numberSet.handlers.timers;

import jatyc.lib.Nullable;
import numberSet.NumberSetProtocol;
import numberSet.common.Globals;
import numberSet.timers.ConnectionRetryTimer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.babel.generic.ProtoTimer;
import pt.unl.fct.di.novasys.babel.handlers.TimerHandler;

import static jatyc.lib.Utils.nonNull;
public class ConnectionRetryHandler<V extends ProtoTimer> implements TimerHandler<V> {
    private static final Logger logger = nonNull(LogManager.getLogger(ConnectionRetryHandler.class));
    private final NumberSetProtocol protocol;

    public ConnectionRetryHandler(NumberSetProtocol protocol) {
        this.protocol = protocol;
    }


    @Override
    public void uponTimer(@Nullable V v, long l) {
        if (!(v instanceof ConnectionRetryTimer)) {
            logger.warn(Globals.WRONG_CLASS_FOR_HANDLER_MSG);
            return;
        }

        var timer = (ConnectionRetryTimer) v;
        protocol.openConnectionPublic(timer.getHost(), timer.getChannelId());
    }
}
