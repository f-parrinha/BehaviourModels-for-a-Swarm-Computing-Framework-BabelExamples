package bitVote.messages;

import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;

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
        return "[VoteRequest roundID: " + roundID + ", New vote!]";
    }
}
