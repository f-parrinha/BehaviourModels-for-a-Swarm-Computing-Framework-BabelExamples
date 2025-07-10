package numberSet.timers;

import pt.unl.fct.di.novasys.babel.generic.ProtoTimer;

public class SendTimer extends ProtoTimer {
    public static final short ID = 200;

    public SendTimer() {
        super(ID);
    }

    @Override
    public ProtoTimer clone() {
        return this;
    }
}
