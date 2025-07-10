package abp;

import abp.common.BabelBootstrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.fct.di.novasys.babel.exceptions.HandlerRegistrationException;
import pt.unl.fct.di.novasys.babel.exceptions.InvalidParameterException;
import pt.unl.fct.di.novasys.babel.exceptions.ProtocolAlreadyExistsException;

import java.io.IOException;

import static jatyc.lib.Utils.nonNull;

public class Main {
    private static final String DEFAULT_CONF = "config.properties";
    private static final Logger logger = nonNull(LogManager.getLogger(Main.class));

    public static void main(String[] args) throws InvalidParameterException, IOException, ProtocolAlreadyExistsException, HandlerRegistrationException {
        BabelBootstrapper bootstrapper = BabelBootstrapper.create(args, DEFAULT_CONF);
        bootstrapper.registerProtocol(new AlternatingBitProtocol());
        bootstrapper.boot();

        logger.info("Starting Babel instance");
    }
}