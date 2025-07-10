package bitVote.handlers.timers;

import bitVote.BitVoteProtocol;
import bitVote.common.Globals;
import bitVote.timers.VoteTimer;
import jatyc.lib.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.babel.generic.ProtoTimer;
import pt.unl.fct.di.novasys.babel.handlers.TimerHandler;

import static jatyc.lib.Utils.nonNull;

public class VoteTimerHandler<V extends ProtoTimer> implements TimerHandler<V> {
    private static final Logger logger = nonNull(LogManager.getLogger(VoteTimerHandler.class));
    private final BitVoteProtocol protocol;

    public VoteTimerHandler(BitVoteProtocol protocol) {
        this.protocol = protocol;
    }


    @Override
    public void uponTimer(@Nullable V v, long l) {
        if (v instanceof VoteTimer) {
            protocol.uponVoteTimer((VoteTimer) v, l);
        }
    }
}
