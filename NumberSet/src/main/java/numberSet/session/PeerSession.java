package numberSet.session;

import numberSet.messages.SetAckMessage;
import pt.unl.fct.di.novasys.network.data.Host;

import java.util.*;

/**
 * Class {@code PeerSession} stores information regarding which numbers are to be acknowledged by the leader, following
 *  its peer's updates
 */
public class PeerSession {
    private final Set<Integer> toAck;
    private final Host peer;

    public PeerSession(Host peer) {
        this.toAck = new LinkedHashSet<>();
        this.peer = peer;
    }

    public List<Integer> getToAck() {
        return new ArrayList<>(toAck);
    }

    public Host getPeer() {
        return peer;
    }

    public boolean isEmpty() {
        return toAck.isEmpty();
    }

    /**
     * Adds a new number to be acknowledged
     * @param numb number to be acknowledged
     */
    public void add(int numb) {
        toAck.add(numb);
    }

    /**
     * Receives an ACK message, verifies and removes the correct acknowledged numbers
     * @param ack ACK message containing
     */
    public void ack(SetAckMessage ack) {
        List<Integer> set = ack.getNumbers();
        for (Integer numb : set) {
            toAck.remove(numb);
        }
    }
}
