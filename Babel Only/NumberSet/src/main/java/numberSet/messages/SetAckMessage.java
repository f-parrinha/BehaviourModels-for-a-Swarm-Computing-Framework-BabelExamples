package numberSet.messages;

import io.netty.buffer.ByteBuf;
import pt.unl.fct.di.novasys.network.ISerializer;

import java.io.IOException;
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
        return "ack list: " + numbers;
    }

    public static ISerializer<SetAckMessage> serializer = new ISerializer<>() {
        @Override
        public void serialize(SetAckMessage setMessage, ByteBuf byteBuf) throws IOException {
            Iterator<Integer> iterator = setMessage.getSetIterator();
            byteBuf.writeInt(setMessage.getSetSize());
            while (iterator.hasNext()) {
                byteBuf.writeInt(iterator.next());
            }
        }

        @Override
        public SetAckMessage deserialize(ByteBuf byteBuf) throws IOException {
            List<Integer> set = new LinkedList<>();
            int size = byteBuf.readInt();
            for(int i = 0; i < size; i++) {
                set.add(byteBuf.readInt());
            }

            return new SetAckMessage(set);
        }
    };
}
