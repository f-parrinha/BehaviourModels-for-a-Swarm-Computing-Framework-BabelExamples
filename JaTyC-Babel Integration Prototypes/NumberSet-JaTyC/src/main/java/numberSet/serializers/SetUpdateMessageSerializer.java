package numberSet.serializers;

import io.netty.buffer.ByteBuf;
import numberSet.common.Globals;
import numberSet.messages.SetUpdateMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.network.ISerializer;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static jatyc.lib.Utils.nonNull;

public class SetUpdateMessageSerializer  <T> implements ISerializer<T> {
    private static final Logger logger = nonNull(LogManager.getLogger(SetUpdateMessageSerializer.class));

    @Override
    public void serialize(T t, ByteBuf byteBuf) throws IOException {
        if (!(t instanceof SetUpdateMessage)) {
            logger.warn(Globals.WRONG_CLASS_FOR_SERIALIZER_MSG);
            return;
        }

        var setMessage = (SetUpdateMessage) t;
        Iterator<Integer> iterator = setMessage.getSetIterator();
        byteBuf.writeInt(setMessage.getSetSize());
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            if (next == null) continue;

            byteBuf.writeInt(next.intValue());
        }
    }

    @Override
    public T deserialize(ByteBuf byteBuf) throws IOException {
        List<Integer> set = new LinkedList<>();
        int size = byteBuf.readInt();
        for(int i = 0; i < size; i++) {
            set.add(byteBuf.readInt());
        }

        return (T) new SetUpdateMessage(set);
    }
}
