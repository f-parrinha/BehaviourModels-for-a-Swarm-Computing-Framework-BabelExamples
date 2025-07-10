package bitVote.handlers;

import bitVote.BitVoteProtocol;
import pt.unl.fct.di.novasys.babel.handlers.ChannelEventHandler;
import pt.unl.fct.di.novasys.channel.ChannelEvent;
import pt.unl.fct.di.novasys.channel.tcp.events.OutConnectionUp;

public class OutConnectionUpHandler<V extends ChannelEvent> implements ChannelEventHandler<V> {
    private final BitVoteProtocol protocol;

    public OutConnectionUpHandler(BitVoteProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void handleEvent(V v, int channelId) {
        if (v instanceof OutConnectionUp)
        {
            protocol.uponOutConnectionUp((OutConnectionUp) v, channelId);
        }
    }
}
