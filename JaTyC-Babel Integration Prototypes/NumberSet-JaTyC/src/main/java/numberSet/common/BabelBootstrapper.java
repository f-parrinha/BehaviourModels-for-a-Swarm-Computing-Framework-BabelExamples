package numberSet.common;

import jatyc.lib.Ensures;
import jatyc.lib.Nullable;
import jatyc.lib.Typestate;
import numberSet.exceptions.NoBabelInstanceFound;
import pt.unl.fct.di.novasys.babel.core.Babel;
import pt.unl.fct.di.novasys.babel.core.GenericProtocol;
import pt.unl.fct.di.novasys.babel.exceptions.HandlerRegistrationException;
import pt.unl.fct.di.novasys.babel.exceptions.InvalidParameterException;
import pt.unl.fct.di.novasys.babel.exceptions.ProtocolAlreadyExistsException;

import java.io.IOException;
import java.util.Properties;

@Typestate("BabelBootstrapper")
public class BabelBootstrapper {
    private static final String EMPTY_FILEPATH= "";

    @Nullable
    private final Babel babel;
    private Properties props;

    public BabelBootstrapper() {
        babel = Babel.getInstance();
        props = new Properties();

        if (babel == null) {
            throw new NoBabelInstanceFound();
        }
    }



    @Ensures("Exec")
    public static BabelBootstrapper create() {
        var bootstrapper =  new BabelBootstrapper();
        bootstrapper.loadConfig(new String[] {}, "");
        return bootstrapper;
    }

    @Ensures("Exec")
    public static BabelBootstrapper create(String[] args, String filePath) {
        var bootstrapper =  new BabelBootstrapper();
        bootstrapper.loadConfig(args, filePath);
        return bootstrapper;
    }

    public void loadConfig(String[] args, String filePath) {
        try {
            Properties properties = Babel.loadConfig(args, filePath);

            // Do not remove "if": JaTyC will say it can be null
            if (properties == null) {
                props = new Properties();
            } else {
                props = properties;
            }
        } catch (InvalidParameterException | IOException e) {
            props = new Properties();
        }
    }

    public void registerProtocol(GenericProtocol protocol) {
        if (babel == null) {
            throw new NoBabelInstanceFound();
        }

        try {
            babel.registerProtocol(protocol);
            protocol.init(props);
        } catch (ProtocolAlreadyExistsException | HandlerRegistrationException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void boot() {
        if (babel == null) {
            throw new NoBabelInstanceFound();
        }

        babel.start();
    }
}
