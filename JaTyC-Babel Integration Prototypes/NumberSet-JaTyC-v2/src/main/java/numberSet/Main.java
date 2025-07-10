package numberSet;

import numberSet.common.BabelBootstrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static jatyc.lib.Utils.nonNull;

public class Main {
    private static final String DEFAULT_CONF = "config.properties";
    private static final Logger logger = nonNull(LogManager.getLogger(Main.class));

    public static void main(String[] args) throws Exception {
        BabelBootstrapper bootstrapper = BabelBootstrapper.create(args, DEFAULT_CONF);
        bootstrapper.registerProtocol(new NumberSetProtocol());
        bootstrapper.boot();

        logger.info("Starting Babel instance");
    }
}