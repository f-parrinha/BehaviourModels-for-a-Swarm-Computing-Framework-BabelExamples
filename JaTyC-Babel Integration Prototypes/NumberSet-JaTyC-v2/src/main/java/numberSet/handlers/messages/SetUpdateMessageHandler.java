package numberSet.handlers.messages;

import numberSet.NumberSetProtocol;
import numberSet.messages.SetUpdateMessage;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.babel.handlers.MessageInHandler;
import pt.unl.fct.di.novasys.network.data.Host;

public class SetUpdateMessageHandler<T extends ProtoMessage> implements MessageInHandler<T> {
    private final NumberSetProtocol protocol;

    public SetUpdateMessageHandler(NumberSetProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void receive(T t, Host host, short protoId, int channelId) {
        if (t instanceof SetUpdateMessage) {
            protocol.uponSetUpdateMessage((SetUpdateMessage) t, host, protoId, channelId);
        }
    }
}
