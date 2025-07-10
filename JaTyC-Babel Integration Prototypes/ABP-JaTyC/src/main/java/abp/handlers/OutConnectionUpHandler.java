package abp.handlers;

import abp.AlternatingBitProtocol;
import pt.unl.fct.di.novasys.babel.handlers.ChannelEventHandler;
import pt.unl.fct.di.novasys.channel.ChannelEvent;

public class OutConnectionUpHandler<V extends ChannelEvent> implements ChannelEventHandler<V> {
    AlternatingBitProtocol protocol;

    public OutConnectionUpHandler(AlternatingBitProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void handleEvent(V v, int i) {
        protocol.startSend();
    }
}
