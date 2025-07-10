package bitVote.common;

import pt.unl.fct.di.novasys.network.data.Host;

public class LoopbackHost extends Host {
    public LoopbackHost() {
        super(Globals.LOOPBACK, Globals.DEFAULT_PORT);
    }
}
