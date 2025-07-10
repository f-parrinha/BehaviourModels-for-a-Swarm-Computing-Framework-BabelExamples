package abp;

import abp.utils.NetworkUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.babel.core.Babel;
import pt.unl.fct.di.novasys.babel.exceptions.HandlerRegistrationException;
import pt.unl.fct.di.novasys.babel.exceptions.InvalidParameterException;
import pt.unl.fct.di.novasys.babel.exceptions.ProtocolAlreadyExistsException;

import java.io.IOException;
import java.util.Properties;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final String DEFAULT_CONF = "config.properties";

    public static void main(String[] args) throws InvalidParameterException, IOException, ProtocolAlreadyExistsException, HandlerRegistrationException {
        Babel babel = Babel.getInstance();

        Properties props = Babel.loadConfig(args, DEFAULT_CONF);
        NetworkUtils.addInterfaceIp(props);

        AlternatingBitProtocol alternatingBitProto = new AlternatingBitProtocol();
        babel.registerProtocol(alternatingBitProto);

        alternatingBitProto.init(props);
        babel.start();
    }
}