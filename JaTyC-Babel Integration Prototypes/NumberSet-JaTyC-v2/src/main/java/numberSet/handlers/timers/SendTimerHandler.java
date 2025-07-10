package numberSet.handlers.timers;

import numberSet.NumberSetProtocol;
import numberSet.timers.SendTimer;
import pt.unl.fct.di.novasys.babel.generic.ProtoTimer;
import pt.unl.fct.di.novasys.babel.handlers.TimerHandler;

public class SendTimerHandler<V extends ProtoTimer> implements TimerHandler<V> {
    private final NumberSetProtocol protocol;

    public SendTimerHandler(NumberSetProtocol protocol) {
        this.protocol = protocol;
    }


    @Override
    public void uponTimer(V v, long l) {
        if (v instanceof SendTimer) {
            protocol.uponSendTimer((SendTimer) v, l);
        }
    }
}
