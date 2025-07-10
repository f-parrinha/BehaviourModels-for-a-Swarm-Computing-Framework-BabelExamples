package abp.serializers;

import abp.messages.BitSend;
import io.netty.buffer.ByteBuf;
import pt.unl.fct.di.novasys.network.ISerializer;

import java.io.IOException;

public class BitSendSerializer<T> implements ISerializer<T> {

    @Override
    public void serialize(T bitSend, ByteBuf byteBuf) throws IOException {
        if (bitSend instanceof BitSend) {
            var message = (BitSend) bitSend;
            byteBuf.writeInt(((BitSend) bitSend).getBit());
        }
    }

    @Override
    public T deserialize(ByteBuf byteBuf) throws IOException {
        int bit = byteBuf.readInt();
        return (T) new BitSend(bit);
    }
}
