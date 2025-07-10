package numberSet.handlers.timers;

import numberSet.NumberSetProtocol;
import numberSet.common.Globals;
import numberSet.exceptions.BadNumberSetUpdate;
import numberSet.timers.SendTimer;
import numberSet.update.NullUpdate;
import numberSet.update.SetUpdate;
import numberSet.update.ValueUpdate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.babel.generic.ProtoTimer;
import pt.unl.fct.di.novasys.babel.handlers.TimerHandler;

import static jatyc.lib.Utils.nonNull;

public class SendTimerHandler<V extends ProtoTimer> implements TimerHandler<V> {
    private static final Logger logger = nonNull(LogManager.getLogger(SendTimerHandler.class));
    private final NumberSetProtocol protocol;

    public SendTimerHandler(NumberSetProtocol protocol) {
        this.protocol = protocol;
    }


    @Override
    public void uponTimer(V v, long l) {
        if (!(v instanceof SendTimer)) {
            logger.warn(Globals.WRONG_CLASS_FOR_HANDLER_MSG);
            return;
        }


        boolean isUniqueNumbersEmpty = protocol.isUniqueNumbersEmpty();
        if (protocol.isSessionComplete() && isUniqueNumbersEmpty) {
            protocol.cancelTimerPublic(l);
            logger.info("Finished sending all numbers!");
            return;
        }

        // Retrieve next update from the "to send" list
        SetUpdate update;
        if (isUniqueNumbersEmpty) {
            update = new NullUpdate();
        } else {
            int val = 0;
            try {
                val = protocol.updateNumberSet();
            } catch (BadNumberSetUpdate e) {
                logger.warn(e.getMessage());
                return;
            }
            update = new ValueUpdate(val);
        }

        logger.info("({}) Leader Set: {}", protocol.getMyself(), protocol.numberSetToList());
        protocol.broadcastSetUpdate(update);
    }
}
