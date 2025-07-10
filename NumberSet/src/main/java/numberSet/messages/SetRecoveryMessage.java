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
        return "recovery list: " + numbers;
    }

    public static ISerializer<SetRecoveryMessage> serializer = new ISerializer<>() {
        @Override
        public void serialize(SetRecoveryMessage setMessage, ByteBuf byteBuf) throws IOException {
            Iterator<Integer> iterator = setMessage.getSetIterator();
            byteBuf.writeInt(setMessage.getSetSize());
            while (iterator.hasNext()) {
                byteBuf.writeInt(iterator.next());
            }
        }

        @Override
        public SetRecoveryMessage deserialize(ByteBuf byteBuf) throws IOException {
            List<Integer> set = new LinkedList<>();
            int size = byteBuf.readInt();
            for(int i = 0; i < size; i++) {
                set.add(byteBuf.readInt());
            }

            return new SetRecoveryMessage(set);
        }
    };
}
