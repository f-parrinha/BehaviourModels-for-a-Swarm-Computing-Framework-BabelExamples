package numberSet.handlers.messages;

import numberSet.NumberSetProtocol;
import numberSet.messages.SetRecoveryRequest;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.babel.handlers.MessageInHandler;
import pt.unl.fct.di.novasys.network.data.Host;

public class SetRecoveryRequestHandler <T extends ProtoMessage> implements MessageInHandler<T> {
    private final NumberSetProtocol protocol;

    public SetRecoveryRequestHandler(NumberSetProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void receive(T t, Host host, short protoId, int channelId) {
        if (t instanceof SetRecoveryRequest) {
            protocol.uponSetRecoveryRequest((SetRecoveryRequest) t, host, protoId, channelId);
        }
    }
}
