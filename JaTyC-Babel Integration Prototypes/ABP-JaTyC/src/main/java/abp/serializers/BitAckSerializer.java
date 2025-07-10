package abp.serializers;

import abp.messages.BitAck;
import io.netty.buffer.ByteBuf;
import pt.unl.fct.di.novasys.network.ISerializer;

import java.io.IOException;

public class BitAckSerializer <T> implements ISerializer<T> {

    @Override
    public void serialize(T bitAck, ByteBuf byteBuf) throws IOException {
        if (bitAck instanceof BitAck) {
            var message = (BitAck) bitAck;
            byteBuf.writeInt(message.getBit());
        }
    }

    @Override
    public T deserialize(ByteBuf byteBuf) throws IOException {
        int bit = byteBuf.readInt();
        return (T) new BitAck(bit);
    }
}
