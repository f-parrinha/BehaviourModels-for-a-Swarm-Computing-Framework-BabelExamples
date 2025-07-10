package numberSet;

import numberSet.utils.NetworkUtils;
import pt.unl.fct.di.novasys.babel.core.Babel;
import pt.unl.fct.di.novasys.babel.exceptions.HandlerRegistrationException;
import pt.unl.fct.di.novasys.babel.exceptions.InvalidParameterException;
import pt.unl.fct.di.novasys.babel.exceptions.ProtocolAlreadyExistsException;

import java.io.IOException;
import java.util.Properties;

public class Main {
    private static final String DEFAULT_CONFIG = "config.properties";

    public static void main(String[] args) throws InvalidParameterException, IOException, ProtocolAlreadyExistsException, HandlerRegistrationException {
        Babel babel = Babel.getInstance();
        Properties props = Babel.loadConfig(args, DEFAULT_CONFIG);
        NetworkUtils.addInterfaceIp(props);

        NumberSetProtocol numbSetProto = new NumberSetProtocol();
        babel.registerProtocol(numbSetProto);

        numbSetProto.init(props);
        babel.start();
    }
}