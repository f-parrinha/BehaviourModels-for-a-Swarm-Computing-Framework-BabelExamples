package numberSet.handlers;

import numberSet.NumberSetProtocol;
import pt.unl.fct.di.novasys.babel.handlers.ChannelEventHandler;
import pt.unl.fct.di.novasys.channel.ChannelEvent;
import pt.unl.fct.di.novasys.channel.tcp.events.OutConnectionUp;

public class OutConnectionUpHandler<V extends ChannelEvent> implements ChannelEventHandler<V> {
    private final NumberSetProtocol protocol;

    public OutConnectionUpHandler(NumberSetProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void handleEvent(V v, int i) {
        if (v instanceof OutConnectionUp) {
            protocol.uponOutConnectionUp((OutConnectionUp) v, i);
        }
    }
}
