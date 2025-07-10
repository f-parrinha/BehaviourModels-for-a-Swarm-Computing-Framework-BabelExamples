package abp.handlers;

import abp.AlternatingBitProtocol;
import abp.timers.ConnectionRetryTimer;
import jatyc.lib.Nullable;
import pt.unl.fct.di.novasys.babel.generic.ProtoTimer;
import pt.unl.fct.di.novasys.babel.handlers.TimerHandler;

public class ConnectionRetryHandler<V extends ProtoTimer> implements TimerHandler<V> {
    private final AlternatingBitProtocol protocol;

    public ConnectionRetryHandler(AlternatingBitProtocol protocol) {
        this.protocol = protocol;
    }


    @Override
    public void uponTimer(@Nullable V v, long l) {
        if (v instanceof ConnectionRetryTimer) {
            this.protocol.uponConnectionRetryTimer((ConnectionRetryTimer) v, l);
        }
    }
}
