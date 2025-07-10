package numberSet.handlers;

import numberSet.NumberSetProtocol;
import numberSet.messages.SetRecoveryRequest;
import numberSet.timers.SendTimer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.babel.handlers.ChannelEventHandler;
import pt.unl.fct.di.novasys.channel.ChannelEvent;
import pt.unl.fct.di.novasys.channel.tcp.events.OutConnectionUp;
import pt.unl.fct.di.novasys.network.data.Host;

import static jatyc.lib.Utils.nonNull;

public class OutConnectionUpHandler<V extends ChannelEvent> implements ChannelEventHandler<V> {
    private static final Logger logger = nonNull(LogManager.getLogger(OutConnectionUpHandler.class));

    private final NumberSetProtocol protocol;

    public OutConnectionUpHandler(NumberSetProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void handleEvent(V v, int i) {
        if (!(v instanceof OutConnectionUp)) {
            return;
        }

        var event = (OutConnectionUp) v;

        // Start Number Update task (Only the leader sends updates)
        protocol.increaseConnectionsCount();
        if (protocol.getConnections() == (protocol.peersSize() - 1) && protocol.isLeader()) {
            protocol.setupPeriodicTimerPublic(new SendTimer(), 0, 1000);
        }

        // Request recovery
        Host leader = protocol.getLeader();
        Host peer = event.getNode();
        if (peer == null) {
            logger.warn("Null host on OutConnectionUp event. This should never happen!");
            return;
        }
        if (protocol.isNumberSetEmpty() && peer.equals(leader)) {
            protocol.sendMessagePublic(new SetRecoveryRequest(), leader);
        }
    }
}
