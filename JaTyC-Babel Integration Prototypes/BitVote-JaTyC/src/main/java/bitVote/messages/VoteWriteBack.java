package bitVote.messages;

import bitVote.voting.VoteRound;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;

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
        return "[VoteWriteBack roundID: " + roundID +
                ", decidedBit: " + decidedBit +
                ", status: " + status + "]";
    }
}