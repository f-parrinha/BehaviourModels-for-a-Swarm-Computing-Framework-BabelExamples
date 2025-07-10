package numberSet.handlers;

import jatyc.lib.Nullable;
import numberSet.common.Globals;
import numberSet.common.PublicGenericProtocol;
import numberSet.timers.ConnectionRetryTimer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.babel.handlers.ChannelEventHandler;
import pt.unl.fct.di.novasys.channel.ChannelEvent;
import pt.unl.fct.di.novasys.channel.tcp.events.OutConnectionFailed;

import static jatyc.lib.Utils.nonNull;


public class OutConnectionFailedHandler<V extends ChannelEvent> implements ChannelEventHandler<V> {
    private static final Logger logger = nonNull(LogManager.getLogger(OutConnectionFailedHandler.class));

    private final PublicGenericProtocol protocol;

    public OutConnectionFailedHandler(PublicGenericProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void handleEvent(@Nullable V protoMessageOutConnectionFailed, int i) {
        if (!(protoMessageOutConnectionFailed instanceof OutConnectionFailed)) {
            logger.warn(Globals.WRONG_CLASS_FOR_HANDLER_MSG);
            return;
        }

        var event = (OutConnectionFailed<?>) protoMessageOutConnectionFailed;
        var host = event.getNode();
        if (host == null) {
            logger.warn("Null host on OutConnectionFailed event. This should never happen!");
            return;
        }

        logger.info("Connection failed. Retrying...");
        protocol.setupTimerPublic(new ConnectionRetryTimer(host, i), 2000);
    }
}
