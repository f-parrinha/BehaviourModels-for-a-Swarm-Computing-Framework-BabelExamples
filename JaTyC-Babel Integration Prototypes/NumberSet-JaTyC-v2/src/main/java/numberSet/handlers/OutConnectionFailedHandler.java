package numberSet.handlers;

import jatyc.lib.Nullable;
import numberSet.NumberSetProtocol;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.babel.handlers.ChannelEventHandler;
import pt.unl.fct.di.novasys.channel.ChannelEvent;
import pt.unl.fct.di.novasys.channel.tcp.events.OutConnectionFailed;


public class OutConnectionFailedHandler<V extends ChannelEvent> implements ChannelEventHandler<V> {
    private final NumberSetProtocol protocol;

    public OutConnectionFailedHandler(NumberSetProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void handleEvent(@Nullable V protoMessageOutConnectionFailed, int i) {
        if (protoMessageOutConnectionFailed instanceof OutConnectionFailed) {
            protocol.uponOutConnectionFailed((OutConnectionFailed<ProtoMessage>) protoMessageOutConnectionFailed, i);
        }
    }
}
