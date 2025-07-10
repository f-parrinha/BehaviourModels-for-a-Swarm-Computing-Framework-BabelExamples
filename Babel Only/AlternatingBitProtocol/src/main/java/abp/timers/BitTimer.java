package abp.timers;

import pt.unl.fct.di.novasys.babel.generic.ProtoTimer;

public class BitTimer extends ProtoTimer {
    public static final short ID = 110;

    public BitTimer() {
        super(ID);
    }

    @Override
    public ProtoTimer clone() {
        return this;
    }
}
