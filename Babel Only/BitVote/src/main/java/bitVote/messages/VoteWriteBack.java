package bitVote.messages;

import bitVote.utills.SerializeUtils;
import bitVote.voting.VoteRound;
import io.netty.buffer.ByteBuf;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.network.ISerializer;

import java.io.IOException;

public class VoteWriteBack extends ProtoMessage {
    public static final short ID = 302;

    private final String roundID;
    private final int decidedBit;
    private final VoteRound.Status status;

    public VoteWriteBack(String roundID, int decidedBit, VoteRound.Status status) {
        super(ID);
        this.decidedBit = decidedBit;
        this.roundID = roundID;
        this.status = status;
    }

    public int getDecidedBit() {
        return decidedBit;
    }

    public String getRoundID() {
        return roundID;
    }

    public VoteRound.Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "roundID: " + roundID +
                " decidedBit: " + decidedBit +
                " status: " + status;
    }

    public static ISerializer<VoteWriteBack> serializer = new ISerializer<>() {
        @Override
        public void serialize(VoteWriteBack voteWriteBack, ByteBuf byteBuf) throws IOException {
            SerializeUtils.serializeString(byteBuf, voteWriteBack.roundID);
            byteBuf.writeInt(voteWriteBack.decidedBit);
            SerializeUtils.serializeString(byteBuf, voteWriteBack.getStatus().toString());
        }

        @Override
        public VoteWriteBack deserialize(ByteBuf byteBuf) throws IOException {
            String roundID = SerializeUtils.deserializeString(byteBuf);
            int bit = byteBuf.readInt();
            var status = VoteRound.Status.valueOf(SerializeUtils.deserializeString(byteBuf));
            return new VoteWriteBack(roundID, bit, status);
        }
    };
}