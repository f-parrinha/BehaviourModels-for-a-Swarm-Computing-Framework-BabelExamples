package numberSet.messages;

import io.netty.buffer.ByteBuf;
import pt.unl.fct.di.novasys.network.ISerializer;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SetRecoveryMessage extends SetMessage{
    public static final short UID = 302;

    public SetRecoveryMessage(List<Integer> updateList) {
        super(UID, updateList);
    }

    @Override
    public String toString() {
        return "[ SetRecoveryMessage: recovery list: " + numbers + " ]";
    }
}
