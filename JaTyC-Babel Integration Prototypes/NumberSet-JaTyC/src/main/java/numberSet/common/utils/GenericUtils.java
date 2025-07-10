package numberSet.common.utils;

import numberSet.common.Globals;
import pt.unl.fct.di.novasys.network.data.Host;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static jatyc.lib.Utils.nonNull;

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
}
