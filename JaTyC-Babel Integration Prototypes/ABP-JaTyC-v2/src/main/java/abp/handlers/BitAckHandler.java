package abp.handlers;

import abp.AlternatingBitProtocol;
import abp.messages.BitAck;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.babel.handlers.MessageInHandler;
import pt.unl.fct.di.novasys.network.data.Host;

public class BitAckHandler<T extends ProtoMessage> implements MessageInHandler<T> {
    private final AlternatingBitProtocol protocol;

    public BitAckHandler(AlternatingBitProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void receive(T t, Host host, short i, int i1) {
        if (t instanceof BitAck) {
            protocol.uponBitAck((BitAck) t, host, i, i1);
        }
    }
}