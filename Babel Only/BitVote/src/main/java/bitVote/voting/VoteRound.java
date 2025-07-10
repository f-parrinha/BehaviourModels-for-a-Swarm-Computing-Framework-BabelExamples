package bitVote.voting;

import bitVote.messages.VoteAck;
import pt.unl.fct.di.novasys.network.data.Host;

import java.util.*;

public class VoteRound {
    public enum Status {
        SUCCESS,
        PENDING,
        FAIL
    }

    public static final int RETRIES = 3;
    public static final String EMPTY_ID = "EMPTY";
    public static final int UNDECIDED_BIT_VALUE = -1;


    private final Map<Host, VoteAck> acks;
    private final String roundID;
    private final Host leader;
    private final int minQuorum;
    private final int participants;

    private Status status;
    private int retries;

    public VoteRound(Host leader, List<Host> peers) {
        this.roundID = UUID.randomUUID().toString();
        this.leader = leader;
        this.acks = new HashMap<>();
        this.participants = peers.size();
        this.minQuorum = peers.size() / 2 + 1;
        this.status = Status.PENDING;
        this.retries = RETRIES;
    }

    public VoteRound(Host leader, List<Host> peers, String roundID) {
        this.roundID = roundID;
        this.leader = leader;
        this.acks = new HashMap<>();
        this.participants = peers.size();
        this.minQuorum = peers.size() / 2 + 1;
        this.status = Status.PENDING;
        this.retries = RETRIES;
    }



    @Override
    public String toString() {
        return "VoteRound " + roundID +
                " status: " + status +
                " retries: " + retries;
    }

    public boolean isRetryFinished() {
        return retries <= 0;
    }

    public int retry() {
        return isRetryFinished() ? 0 : --retries;
    }

    public Status getStatus() {
        return status;
    }

    public String getRoundID() {
        return roundID;
    }

    public List<VoteAck> getAcks() {
        return new ArrayList<>(acks.values());
    }

    public boolean isAcksFull() {
        return participants == acks.size();
    }

    public VoteAck getAck(Host peer) {
        return acks.get(peer);
    }

    public void ack(Host peer, VoteAck vote) {
        if (status == Status.PENDING) {
            acks.put(peer, vote);
        }
    }

    public boolean hasQuorum() {
        return acks.size() >= minQuorum && acks.containsKey(leader);
    }

    public boolean isPending() {
        return status == Status.PENDING;
    }

    /**
     * Evaluates a VoteRound by checking whether it has failed or succeeded and assigning the corresponding status
     * <p>
     *     To succeed, either a quorum has been achieved (and no more retires are left) or the entire ACKs have been received
     * </p>
     * @return round status
     */
    public Status evaluate() {
        boolean finishedRetries = isRetryFinished();
        if (isAcksFull() || (hasQuorum() && finishedRetries)) {
            status = Status.SUCCESS;
        } else if(finishedRetries) {
            status = Status.FAIL;
        }

        return status;
    }

    /**
     * Computes the final decision for the vote round
     * <p>
     *  Can only return a correct bit value if the VoteRound is already finished (no PENDING status)
     * </p>
     * @return 1 or 0 (decision). -1 (UNDECIDED_BIT_VALUE) if the method is called before the round is finished
     */
    public int decide() {
        if (status == Status.PENDING) return UNDECIDED_BIT_VALUE;

        int oneCount = 0;
        int zeroCount = 0;
        int leaderBit = acks.get(leader).getBit();
        for(VoteAck ack : acks.values()) {
            if (ack.getBit() == 1) {
                oneCount++;
            } else {
                zeroCount++;
            }
        }

        return oneCount == zeroCount ? leaderBit : oneCount > zeroCount ? 1 : 0;
    }
}
