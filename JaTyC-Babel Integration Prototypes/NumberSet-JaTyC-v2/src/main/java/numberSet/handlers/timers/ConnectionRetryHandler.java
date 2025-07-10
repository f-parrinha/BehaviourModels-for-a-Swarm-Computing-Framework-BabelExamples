package numberSet.handlers.timers;

import jatyc.lib.Nullable;
import numberSet.NumberSetProtocol;
import numberSet.timers.ConnectionRetryTimer;
import pt.unl.fct.di.novasys.babel.generic.ProtoTimer;
import pt.unl.fct.di.novasys.babel.handlers.TimerHandler;

public class ConnectionRetryHandler<V extends ProtoTimer> implements TimerHandler<V> {
    private final NumberSetProtocol protocol;

    public ConnectionRetryHandler(NumberSetProtocol protocol) {
        this.protocol = protocol;
    }


    @Override
    public void uponTimer(@Nullable V v, long l) {
        if (v instanceof ConnectionRetryTimer) {
            protocol.uponConnectionRetryTimer((ConnectionRetryTimer) v, l);
        }
    }
}
