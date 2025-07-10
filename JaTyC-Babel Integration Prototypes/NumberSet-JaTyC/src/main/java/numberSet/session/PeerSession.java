package numberSet.session;

import jatyc.lib.Ensures;
import jatyc.lib.Typestate;
import numberSet.common.LoopbackHost;
import numberSet.messages.SetAckMessage;
import pt.unl.fct.di.novasys.network.data.Host;

import java.util.*;

/**
 * Class {@code PeerSession} stores information regarding which numbers are to be acknowledged by the leader, following
 *  its peer's updates
 */
//@Typestate("PeerSession")
public class PeerSession {
    private final Set<Integer> toAck;
    private Host peer;

    public PeerSession() {
        this.toAck = new LinkedHashSet<>();
        this.peer = new LoopbackHost();
    }

    //@Ensures("Exec")
    public static PeerSession create(Host peer) {
          var peerSession = new PeerSession();
          peerSession.init(peer);
          return peerSession;
    }

    public void init(Host peer) {
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
            if (numb == null) continue;

            toAck.remove(numb);
        }
    }
}
