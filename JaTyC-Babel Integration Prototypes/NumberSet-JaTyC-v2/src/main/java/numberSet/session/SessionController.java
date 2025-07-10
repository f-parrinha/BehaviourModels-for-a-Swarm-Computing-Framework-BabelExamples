package numberSet.session;

import jatyc.lib.Ensures;
import jatyc.lib.Typestate;
import pt.unl.fct.di.novasys.network.data.Host;

import java.util.*;

import static jatyc.lib.Utils.nonNull;

/**
 * Class {@code SessionController} manages the different sessions for the leader's peers
 */
//@Typestate("SessionController")
public class SessionController {
    private final Map<Host, PeerSession> sessions;

    public SessionController(int peersSize) {
        sessions = new HashMap<>(peersSize);
    }

    //@Ensures("Exec")
    public static SessionController create(List<Host> peers) {
        var sessionController = new SessionController(peers.size());
        sessionController.init(peers);
        return sessionController;
    }

    public void init(List<Host> peers) {
        for(var peer : peers) {
            if (peer == null) continue;
            sessions.put(peer, PeerSession.create(peer));
        }
    }

    public Iterator<PeerSession> iterator() {
        return createIterator();
    }

    public PeerSession get(Host peer) {
        PeerSession session = sessions.get(peer);
        if (session == null) {
            throw new NullPointerException();
        }

        return session;
    }

    public boolean isComplete() {
       Iterator<PeerSession> it = createIterator();
       while(it.hasNext()) {
           PeerSession peerSession = it.next();
           if (peerSession == null) continue;
           if (!peerSession.isEmpty()) return false;
       }

       return true;
    }

    private Iterator<PeerSession> createIterator() {
        Collection<PeerSession> values = nonNull(sessions.values());
        return nonNull(values.iterator());
    }
}
