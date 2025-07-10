package numberSet.common;

import jatyc.lib.Ensures;
import jatyc.lib.Typestate;

import java.util.Properties;


@Typestate("PropertiesReader")
public class PropertiesReader {
    private Properties props;

    public void load(Properties props) {
        this.props = props;
    }

    public Properties getProperties() {
        return props;
    }

    public String read(String name) {
        String prop = props.getProperty(name);

        if (prop == null) {
            return Globals.EMPTY_STRING;
        }

        return prop;
    }

    @Ensures("Init")
    public static PropertiesReader create() {
        return new PropertiesReader();
    }

    @Ensures("Exec")
    public static PropertiesReader create(Properties properties) {
        PropertiesReader reader = new PropertiesReader();
        reader.load(properties);
        return reader;
    }
}
