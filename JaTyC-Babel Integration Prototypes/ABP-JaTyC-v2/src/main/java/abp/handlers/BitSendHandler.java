package abp.handlers;

import abp.AlternatingBitProtocol;
import abp.messages.BitSend;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.babel.handlers.MessageInHandler;
import pt.unl.fct.di.novasys.network.data.Host;

public class BitSendHandler<T extends ProtoMessage> implements MessageInHandler<T> {
    private final AlternatingBitProtocol protocol;

    public BitSendHandler(AlternatingBitProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void receive(T t, Host host, short i, int i1) {
        if (t instanceof BitSend) {
            protocol.uponBitSend((BitSend) t, host, i,i1);
        }
    }
}
