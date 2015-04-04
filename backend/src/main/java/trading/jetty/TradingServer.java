package trading.jetty;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;;

public class TradingServer implements Runnable {
	private static final Logger logger = Logger.getLogger(TradingServer.class);
	
	final public static String KEY_SERVER_NAME = "server.name";
	final public static String KEY_SERVER_PORT = "server.port";
	final public static String KEY_PATH_WAR = "path.war";
	final public static String KEY_WEB_XML = "web.xml";
	
	final public static String FILE_CONF = "config/jetty.properties";
	final public static String DIR_CONFIG = "config";

	public static final String URL_STATUS = "/status";
	public static final String PATH_CONTEXT = "/";

	private Properties props = new Properties();
	private File warPath;
	private File webXmlPath;
	
	private void loadProperties() throws Exception {
 		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		props.load(loader.getResourceAsStream(FILE_CONF));
		File rootDir = new File(loader.getResource(DIR_CONFIG).toURI());
		warPath = new File(rootDir, props.getProperty(KEY_PATH_WAR));
		webXmlPath = new File(warPath, props.getProperty(KEY_WEB_XML));
 	}
		
	public void run() {
		String serverName = props.getProperty(KEY_SERVER_NAME);
		int port = Integer.parseInt(props.getProperty(KEY_SERVER_PORT));
		try {
			logger.info("Starting " + serverName);

			Server server = new Server();
			ServerConnector connector = new ServerConnector(server);
			connector.setPort(port);
			server.setConnectors(new Connector[] { connector });
			server.setStopAtShutdown(true);
			
			WebAppContext context = new WebAppContext();
			context.setContextPath(PATH_CONTEXT);
			context.setWar(warPath.getAbsolutePath());
			context.setDefaultsDescriptor(webXmlPath.getAbsolutePath());

			server.setHandler(context);
			server.start();
			server.join();
		} catch (Throwable th) {
			String error = serverName + " web server exception, server is down.";
			logger.fatal(error, th);
			System.exit(1);
		}
	}
	
	public static void main(String[] args) {
		TradingServer server = new TradingServer();
		try {
			server.loadProperties();
		} 
		catch (Throwable th) 
		{
			logger.error("init error", th);
			System.exit(1);
		}
		new Thread(server).start();
	}
}
