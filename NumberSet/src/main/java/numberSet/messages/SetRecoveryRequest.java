package numberSet.messages;

import io.netty.buffer.ByteBuf;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.network.ISerializer;

import java.io.IOException;

public class SetRecoveryRequest extends ProtoMessage {
    public static final short UID = 303;

    public SetRecoveryRequest() {
        super(UID);
    }

    @Override
    public String toString() {
        return "";
    }

    public static ISerializer<SetRecoveryRequest> serializer = new ISerializer<>() {
        @Override
        public void serialize(SetRecoveryRequest setMessage, ByteBuf byteBuf) throws IOException {

        }

        @Override
        public SetRecoveryRequest deserialize(ByteBuf byteBuf) throws IOException {
            return new SetRecoveryRequest();
        }
    };
}
