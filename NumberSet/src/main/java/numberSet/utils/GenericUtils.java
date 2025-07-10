package numberSet.utils;

import pt.unl.fct.di.novasys.network.data.Host;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class GenericUtils {
    public static List<Integer> createPossibleNumberSet(int max) {
        List<Integer> res = new ArrayList<>();

        for(int i=0; i < max; i++) {
            res.add(i);
        }

        return res;
    }


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
}
