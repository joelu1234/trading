package trading.util;

import java.util.Properties;
import org.apache.log4j.Logger;

final public class PropertyManager {
	private static Logger logger = Logger.getLogger(PropertyManager.class);

	final public static String FINVIZ_STATS = "trading.receiver.finviz.stats";
	final public static String REUTERS_STATS = "trading.reciever.reuters.stats";

	final public static String MAIL_SMTPS_AUTH = "mail.smtps.auth";
	final public static String MAIL_SMTPS_HOST = "mail.smtps.host";
	final public static String MAIL_SMTPS_PORT = "mail.smtps.port";
	final public static String MAIL_TO = "mail.to";
	final public static String MAIL_FROM = "mail.from";
	final public static String MAIL_USERNAME = "mail.username";
	final public static String MAIL_PASSWORD = "mail.password";

	private static PropertyManager propertyManager = null;

	private Properties props = new Properties();

	private PropertyManager() throws Exception {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		props.load(loader.getResourceAsStream("conf.properties"));
	}

	public synchronized static PropertyManager getInstance() {
		if (propertyManager == null) {
			try {
				propertyManager = new PropertyManager();
			} catch (Throwable th) {
				logger.error("Unable to create PropertyManager", th);
			}
		}
		return propertyManager;
	}

	public String getProperty(String key) {
		return props.getProperty(key, "");
	}

}
