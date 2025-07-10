package abp.messages;

import io.netty.buffer.ByteBuf;
import pt.unl.fct.di.novasys.network.ISerializer;

public class BitAck extends BitMessage {
    public static final short ID = 101;

    public BitAck(int bit) {
        super(ID, bit);
    }


    public static ISerializer<BitAck> serializer = new ISerializer<>() {
        @Override
        public void serialize(BitAck bitAck, ByteBuf byteBuf) {
            byteBuf.writeInt(bitAck.getBit());
        }

        @Override
        public BitAck deserialize(ByteBuf byteBuf) {
            int bit = byteBuf.readInt();
            return new BitAck(bit);
        }
    };
}