package abp.messages;

import io.netty.buffer.ByteBuf;
import pt.unl.fct.di.novasys.network.ISerializer;

public class BitSend extends BitMessage {
    public static final short ID = 102;

    public BitSend(int bit) {
        super(ID, bit);
    }

    public void flip() {
        bit = bit == 0 ? 1 : 0;
    }

    public static ISerializer<BitSend> serializer = new ISerializer<>() {
        @Override
        public void serialize(BitSend bitSend, ByteBuf byteBuf) {
            byteBuf.writeInt(bitSend.getBit());
        }

        @Override
        public BitSend deserialize(ByteBuf byteBuf) {
            int bit = byteBuf.readInt();
            return new BitSend(bit);
        }
    };
}
