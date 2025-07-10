package bitVote.handlers.timers;

import bitVote.BitVoteProtocol;
import bitVote.timers.ConnectionRetryTimer;
import jatyc.lib.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.babel.generic.ProtoTimer;
import pt.unl.fct.di.novasys.babel.handlers.TimerHandler;

import static jatyc.lib.Utils.nonNull;
public class ConnectionRetryHandler<V extends ProtoTimer> implements TimerHandler<V> {
    private static final Logger logger = nonNull(LogManager.getLogger(ConnectionRetryHandler.class));
    private final BitVoteProtocol protocol;

    public ConnectionRetryHandler(BitVoteProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void uponTimer(@Nullable V v, long l) {
        if (v instanceof ConnectionRetryTimer) {
            protocol.uponConnectionRetryTimer((ConnectionRetryTimer) v, l);
        }
    }
}
