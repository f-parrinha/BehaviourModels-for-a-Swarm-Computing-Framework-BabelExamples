package bitVote.messages;

import io.netty.buffer.ByteBuf;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.network.ISerializer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class VoteRequest extends ProtoMessage {
    public static final short ID = 300;

    private final String roundID;

    public VoteRequest(String roundID) {
        super(ID);
        this.roundID = roundID;
    }

    public String getRoundID() {
        return roundID;
    }

    @Override
    public String toString() {
        return "roundID: " + roundID +
                " New vote!";
    }

    public static ISerializer<VoteRequest> serializer = new ISerializer<>() {
        @Override
        public void serialize(VoteRequest voteRequest, ByteBuf byteBuf) throws IOException {
            byte[] roundIDBytes = voteRequest.roundID.getBytes(StandardCharsets.UTF_8);
            byteBuf.writeInt(roundIDBytes.length);
            byteBuf.writeBytes(roundIDBytes);
        }

        @Override
        public VoteRequest deserialize(ByteBuf byteBuf) throws IOException {
            int roundIDLength = byteBuf.readInt();
            byte[] roundIDBytes = new byte[roundIDLength];
            byteBuf.readBytes(roundIDBytes);
            return new VoteRequest(new String(roundIDBytes, StandardCharsets.UTF_8));
        }
    };
}
