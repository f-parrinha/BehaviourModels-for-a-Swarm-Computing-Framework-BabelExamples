package numberSet.messages;

import java.util.*;

/**
 * Class {@code SetAckMessage} is used by the non-leader peers, to inform the leader of the numbers that were updated/added
 *  to their set
 */
public class SetAckMessage extends SetMessage {
    public static final short UID = 301;

    public SetAckMessage(List<Integer> ackList) {
        super(UID, ackList);
    }

    @Override
    public String toString() {
        return "[SetAckMessage: " +
                " ack list: " + numbers + "]";
    }
}
