package abp.handlers;

import abp.AlternatingBitProtocol;
import abp.timers.BitTimer;
import pt.unl.fct.di.novasys.babel.generic.ProtoTimer;
import pt.unl.fct.di.novasys.babel.handlers.TimerHandler;

public class BitTimerHandler<V extends ProtoTimer> implements TimerHandler<V> {
    private final AlternatingBitProtocol protocol;

    public BitTimerHandler(AlternatingBitProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void uponTimer(V v, long l) {
        if (v instanceof BitTimer) {
            protocol.uponBitTimer((BitTimer) v, l);
        }
    }
}
