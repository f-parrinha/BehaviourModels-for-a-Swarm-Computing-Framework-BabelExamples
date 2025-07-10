package bitVote.timers;

import pt.unl.fct.di.novasys.babel.generic.ProtoTimer;

public class VoteTimer extends ProtoTimer {
    public static final short ID = 200;

    public VoteTimer() {
        super(ID);
    }

    @Override
    public ProtoTimer clone() {
        return this;
    }
}
