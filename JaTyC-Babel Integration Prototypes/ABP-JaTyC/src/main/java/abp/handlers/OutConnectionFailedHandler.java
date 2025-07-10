package abp.handlers;

import abp.AlternatingBitProtocol;
import abp.common.PublicGenericProtocol;
import abp.timers.ConnectionRetryTimer;
import jatyc.lib.Nullable;
import jatyc.lib.Typestate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.babel.handlers.ChannelEventHandler;
import pt.unl.fct.di.novasys.channel.ChannelEvent;
import pt.unl.fct.di.novasys.channel.tcp.events.OutConnectionFailed;

import static jatyc.lib.Utils.nonNull;


public class OutConnectionFailedHandler<V extends ChannelEvent> implements ChannelEventHandler<V> {
    private static final Logger logger = nonNull(LogManager.getLogger(OutConnectionFailedHandler.class));

    private final AlternatingBitProtocol protocol;

    public OutConnectionFailedHandler(AlternatingBitProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void handleEvent(@Nullable V protoMessageOutConnectionFailed, int i) {
        logger.info("Connection failed. Retrying...");
        protocol.setupTimerPublic(new ConnectionRetryTimer(), 2000);
    }
}
