package bitVote.messages;

import io.netty.buffer.ByteBuf;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.network.ISerializer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class VoteAck extends ProtoMessage {
    public static final short ID = 301;

    private final String roundID;
    private final int bit;

    public VoteAck(String roundID, int bit) {
        super(ID);
        this.bit = bit;
        this.roundID = roundID;
    }

    public int getBit() {
        return bit;
    }

    public String getRoundID() {
        return roundID;
    }

    @Override
    public String toString() {
        return "roundID :" + roundID +
                " bit: " + bit;
    }

    public static ISerializer<VoteAck> serializer = new ISerializer<>() {
        @Override
        public void serialize(VoteAck voteAck, ByteBuf byteBuf) throws IOException {
            byte[] roundIDBytes = voteAck.roundID.getBytes(StandardCharsets.UTF_8);
            byteBuf.writeInt(roundIDBytes.length);
            byteBuf.writeBytes(roundIDBytes);
            byteBuf.writeInt(voteAck.bit);
        }

        @Override
        public VoteAck deserialize(ByteBuf byteBuf) throws IOException {
            int roundIDLength = byteBuf.readInt();
            byte[] roundIDBytes = new byte[roundIDLength];
            byteBuf.readBytes(roundIDBytes);
            int bit = byteBuf.readInt();
            return new VoteAck(new String(roundIDBytes, StandardCharsets.UTF_8), bit);
        }
    };
}
