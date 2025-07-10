package bitVote.messages;

import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;

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
        return "[VoteAck roundID :" + roundID + ", bit: " + bit + "]";
    }
}
