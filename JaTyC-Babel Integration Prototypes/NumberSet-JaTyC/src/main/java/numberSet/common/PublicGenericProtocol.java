package numberSet.common;

import pt.unl.fct.di.novasys.babel.core.GenericProtocol;
import pt.unl.fct.di.novasys.babel.exceptions.HandlerRegistrationException;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.babel.generic.ProtoTimer;
import pt.unl.fct.di.novasys.network.data.Host;

import java.io.IOException;
import java.util.Properties;

/**
 * Class {@code PublicGenericProtocol} adds public methods that call certain protected methods. Useful for event handlers.
 */
public abstract class PublicGenericProtocol extends GenericProtocol {

    public PublicGenericProtocol(String protoName, short protoId) {
        super(protoName, protoId);
    }

    @Override
    public abstract void init(Properties properties) throws HandlerRegistrationException, IOException;

    public void sendMessagePublic(ProtoMessage message, Host destination) {
        super.sendMessage(message, destination);
    }
    public void sendMessagePublic(ProtoMessage message, Host destination, int channelId) {
        super.sendMessage(message, destination, channelId);
    }

    public long setupTimerPublic(ProtoTimer timer, long timeout) {
        return super.setupTimer(timer, timeout);
    }

    public long setupPeriodicTimerPublic(ProtoTimer timer, long start, long timeout) {
        return super.setupPeriodicTimer(timer, start, timeout);
    }

    public void cancelTimerPublic(long timerId) {
        super.cancelTimer(timerId);
    }

    public void openConnectionPublic(Host peer) {
        super.openConnection(peer);
    }

    public void openConnectionPublic(Host peer, int channelId) {
        super.openConnection(peer, channelId);
    }
}
