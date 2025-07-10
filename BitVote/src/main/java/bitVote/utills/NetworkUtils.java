package bitVote.utills;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.network.data.Host;

import java.net.*;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class NetworkUtils {
    private static final Logger logger = LogManager.getLogger(NetworkUtils.class);

    public static List<Host> setupPeersList(String peersString) {
        List<Host> peers = new ArrayList<>();
        String[] peersInStr = peersString.split(",");

        try {
            for(var peer : peersInStr) {
                String[] address_port = peer.split(":");
                InetAddress address = InetAddress.getByName(address_port[0]);
                int port = Integer.parseInt(address_port[1]);

                peers.add(new Host(address, port));
            }

            return peers;
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the ipv4 address of the given interface
     * @param inter name of the interface
     * @return ipv4 address of the interface
     */
    public static String getAddress(String inter) {
        try {
            NetworkInterface byName = NetworkInterface.getByName(inter);
            if (byName == null) {
                logger.error("No interface named " + inter);
                return null;
            }

            Enumeration<InetAddress> addresses = byName.getInetAddresses();
            InetAddress currentAddress;
            while (addresses.hasMoreElements()) {
                currentAddress = addresses.nextElement();
                if (currentAddress instanceof Inet4Address)
                    return currentAddress.getHostAddress();
            }

            logger.error("No ipv4 found for interface " + inter);
            return null;
        } catch (SocketException e) {
            logger.error(e.fillInStackTrace());
            throw new RuntimeException(e);
        }
    }

    public static void addInterfaceIp(Properties props) throws InvalidParameterException {
        String interfaceName;
        if ((interfaceName = props.getProperty("interface")) != null && !interfaceName.trim().isEmpty()) {
            String ip = getAddress(interfaceName);
            if (ip != null)
                props.setProperty("address", ip);
            else {
                throw new InvalidParameterException("Property interface is set to " + interfaceName + ", but has no ip");
            }
        }
    }
}
