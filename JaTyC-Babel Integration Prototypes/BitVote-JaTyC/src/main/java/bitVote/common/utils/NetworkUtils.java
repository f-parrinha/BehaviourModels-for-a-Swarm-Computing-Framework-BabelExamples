package bitVote.common.utils;

import bitVote.common.Globals;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.network.data.Host;

import java.net.*;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import static jatyc.lib.Utils.nonNull;

public class NetworkUtils {
    private static final Logger logger = nonNull(LogManager.getLogger(NetworkUtils.class));

    public static List<Host> setupPeersList(String peersString) {
        List<Host> peers = new ArrayList<>();
        String[] peersInStr = peersString.split(",");

        try {
            for(var peer : peersInStr) {
                if (peer == null) continue;

                String[] address_port = peer.split(":");
                if (address_port.length != 2) continue;

                String address = Globals.EMPTY_STRING + address_port[0];
                String port = Globals.EMPTY_STRING + address_port[1];
                InetAddress inetAddress = nonNull(InetAddress.getByName(address));
                peers.add(new Host(inetAddress, Integer.parseInt(port)));
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
                logger.error("No interface named {}", inter);
                return Globals.EMPTY_STRING;
            }

            Enumeration<InetAddress> addresses = nonNull(byName.getInetAddresses());
            InetAddress currentAddress;
            while (addresses.hasMoreElements()) {
                currentAddress = addresses.nextElement();
                if (currentAddress instanceof Inet4Address)
                    return Globals.EMPTY_STRING + currentAddress.getHostAddress();
            }

            logger.error("No ipv4 found for interface " + inter);
            return Globals.EMPTY_STRING;
        } catch (SocketException e) {
            logger.error(e.fillInStackTrace().getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void addInterfaceIp(Properties props) throws InvalidParameterException {
        String interfaceName = props.getProperty("interface");
        if (interfaceName == null) {
            logger.error("No property for 'interface' was found");
            return;
        }

        if (interfaceName.trim().isEmpty()) {
            logger.error("Interface is empty");
            return;
        }

        String ip = getAddress(interfaceName);
        if (ip != null)
            props.setProperty("address", ip);
        else {
            throw new InvalidParameterException("Property interface is set to " + interfaceName + ", but has no ip");
        }
    }
}
