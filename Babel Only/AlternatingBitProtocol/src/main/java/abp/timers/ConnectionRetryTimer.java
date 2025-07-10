package abp.timers;

import pt.unl.fct.di.novasys.babel.generic.ProtoTimer;

public class ConnectionRetryTimer extends ProtoTimer {
    public static final short ID = 112;

    public ConnectionRetryTimer() {
        super(ID);
    }

    @Override
    public ProtoTimer clone() {
        return this;
    }
}
