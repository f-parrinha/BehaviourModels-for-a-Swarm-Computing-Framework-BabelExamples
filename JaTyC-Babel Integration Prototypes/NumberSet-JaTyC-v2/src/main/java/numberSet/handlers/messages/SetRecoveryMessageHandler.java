package numberSet.handlers.messages;

import numberSet.NumberSetProtocol;
import numberSet.messages.SetRecoveryMessage;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.babel.handlers.MessageInHandler;
import pt.unl.fct.di.novasys.network.data.Host;

public class SetRecoveryMessageHandler <T extends ProtoMessage> implements MessageInHandler<T> {
    private final NumberSetProtocol protocol;

    public SetRecoveryMessageHandler(NumberSetProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void receive(T t, Host host, short protoId, int channelId) {
        if (t instanceof SetRecoveryMessage) {
            protocol.uponSetRecoveryMessage((SetRecoveryMessage) t, host, protoId, channelId);
        }
    }
}
