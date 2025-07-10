package bitVote.timers;

import pt.unl.fct.di.novasys.babel.generic.ProtoTimer;
import pt.unl.fct.di.novasys.network.data.Host;

public class ConnectionRetryTimer extends ProtoTimer {
    public static final short ID = 201;

    private final int channelId;
    private final Host host;

    public ConnectionRetryTimer(Host host, int channelId) {
        super(ID);

        this.host = host;
        this.channelId = channelId;
    }

    public Host getHost() {
        return host;
    }

    public int getChannelId() {
        return channelId;
    }

    @Override
    public ProtoTimer clone() {
        return this;
    }
}
