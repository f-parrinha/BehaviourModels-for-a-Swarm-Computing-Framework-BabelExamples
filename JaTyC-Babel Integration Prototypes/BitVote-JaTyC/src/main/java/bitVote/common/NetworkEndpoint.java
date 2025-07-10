package bitVote.common;

import bitVote.exceptions.NullInetAddress;
import jatyc.lib.Ensures;
import jatyc.lib.Typestate;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Typestate("NetworkEndpoint")
public class NetworkEndpoint {
    private String address;
    private String port;

    public void init(String ip, String port) throws UnknownHostException {
        this.address = ip;
        this.port = port;
    }

    public InetAddress getInetAddress() {
        InetAddress inetAddress;

        // Get loopback if the given address does not exist
        try {
            inetAddress = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            inetAddress = InetAddress.getLoopbackAddress();
        }

        // Throw exception to make sure @Nullable is not required
        if (inetAddress == null) {
            throw new NullInetAddress();
        }

        return inetAddress;
    }

    public String getAddress() {
        return address;
    }

    public String getPort() {
        return port;
    }

    public String getName() {
        return address + ":" + port;
    }


    @Ensures("Exec")
    public static NetworkEndpoint create(String ip, String port) throws UnknownHostException {
        var netProps = new NetworkEndpoint();
        netProps.init(ip, port);

        return netProps;
    }
}
