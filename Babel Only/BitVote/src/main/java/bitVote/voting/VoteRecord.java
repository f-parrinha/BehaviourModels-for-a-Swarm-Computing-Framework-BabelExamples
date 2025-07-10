package bitVote.voting;

public class VoteRecord {
    private final String roundID;
    private final int votedBit;

    private int decidedBit;
    private VoteRound.Status status;

    public VoteRecord(String roundID, int votedBit) {
        this.roundID = roundID;
        this.votedBit = votedBit;

        this.status = VoteRound.Status.PENDING;
        this.decidedBit = VoteRound.UNDECIDED_BIT_VALUE;
    }

    public String getRoundID() {
        return roundID;
    }

    public int getVotedBit() {
        return votedBit;
    }
    public int getDecidedBit() {
        return decidedBit;
    }

    public void setDecidedBit(int decidedBit) {
        if (status == VoteRound.Status.PENDING) {
            this.decidedBit = decidedBit;
            this.status = VoteRound.Status.SUCCESS;
        }
    }

    public void fail() {
        status = VoteRound.Status.FAIL;
    }
}
