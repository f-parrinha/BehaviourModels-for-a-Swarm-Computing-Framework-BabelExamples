package numberSet.session;

import pt.unl.fct.di.novasys.network.data.Host;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Class {@code SessionController} manages the different sessions for the leader's peers
 */
public class SessionController {
    private final Map<Host, PeerSession> sessions;

    public SessionController(List<Host> peers) {
        sessions = new HashMap<>(peers.size());

        for(var peer : peers) {
            sessions.put(peer, new PeerSession(peer));
        }
    }

    public Iterator<PeerSession> iterator() {
        return sessions.values().iterator();
    }

    public PeerSession get(Host peer) {
        return sessions.get(peer);
    }

    public boolean isComplete() {
       Iterator<PeerSession> it = iterator();
       while(it.hasNext()) {
           if (!it.next().isEmpty()) return false;
       }

       return true;
    }
}
