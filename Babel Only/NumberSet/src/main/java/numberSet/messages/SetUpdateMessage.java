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
        return "update list: " + numbers;
    }

    public static ISerializer<SetUpdateMessage> serializer = new ISerializer<>() {
        @Override
        public void serialize(SetUpdateMessage setMessage, ByteBuf byteBuf) throws IOException {
            Iterator<Integer> iterator = setMessage.getSetIterator();
            byteBuf.writeInt(setMessage.getSetSize());
            while (iterator.hasNext()) {
                byteBuf.writeInt(iterator.next());
            }
        }

        @Override
        public SetUpdateMessage deserialize(ByteBuf byteBuf) throws IOException {
            List<Integer> set = new LinkedList<>();
            int size = byteBuf.readInt();
            for(int i = 0; i < size; i++) {
                set.add(byteBuf.readInt());
            }

            return new SetUpdateMessage(set);
        }
    };
}
