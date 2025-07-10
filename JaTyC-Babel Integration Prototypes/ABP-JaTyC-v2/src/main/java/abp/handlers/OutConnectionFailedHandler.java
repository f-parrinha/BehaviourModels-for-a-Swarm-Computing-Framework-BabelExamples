package abp.handlers;

import abp.AlternatingBitProtocol;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.babel.handlers.ChannelEventHandler;
import pt.unl.fct.di.novasys.channel.ChannelEvent;
import pt.unl.fct.di.novasys.channel.tcp.events.OutConnectionFailed;


public class OutConnectionFailedHandler<V extends ChannelEvent> implements ChannelEventHandler<V> {
    private final AlternatingBitProtocol protocol;

    public OutConnectionFailedHandler(AlternatingBitProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void handleEvent(V protoMessageOutConnectionFailed, int i) {
        if (protoMessageOutConnectionFailed instanceof OutConnectionFailed) {
            protocol.uponOutConnectionFailed((OutConnectionFailed<ProtoMessage>) protoMessageOutConnectionFailed, i);
        }
    }
}
