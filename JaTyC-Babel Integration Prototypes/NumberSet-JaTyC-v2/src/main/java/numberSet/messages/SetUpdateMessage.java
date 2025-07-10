package numberSet.messages;

import io.netty.buffer.ByteBuf;
import pt.unl.fct.di.novasys.network.ISerializer;

import java.io.IOException;
import java.util.*;

/**
 * Class {@code SetUpdateMessage} is used by the leader to inform its peers of a new update. Usually, the list of numbers
 *  contains one element, but may have more in case there a lagging peer
 */
public class SetUpdateMessage extends SetMessage {
    public static final short UID = 300;

    public SetUpdateMessage(List<Integer> updateList) {
        super(UID, updateList);
    }


    @Override
    public String toString() {
        return "[SetUpdateMessage: " +
                " update list: " + numbers + "]";
    }
}
