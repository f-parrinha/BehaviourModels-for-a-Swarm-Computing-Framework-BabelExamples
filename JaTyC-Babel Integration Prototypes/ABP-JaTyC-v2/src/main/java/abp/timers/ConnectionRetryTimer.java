package abp.timers;

import pt.unl.fct.di.novasys.babel.generic.ProtoTimer;

public class ConnectionRetryTimer extends ProtoTimer {
    public static final short ID = 111;

    public ConnectionRetryTimer() {
        super(ID);
    }

    @Override
    public ProtoTimer clone() {
        return this;
    }
}
