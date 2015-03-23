package trading.jetty;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.mortbay.jetty.*;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.QueuedThreadPool;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import trading.service.TradingDataService;

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

	private static Properties props = new Properties();
	private File warPath;
	private File webXmlPath;
	
	private TradingDataService dataService;
	

	private void loadProperties() throws Exception {
 		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		props.load(loader.getResourceAsStream(FILE_CONF));
		File rootDir = new File(loader.getResource(DIR_CONFIG).toURI());
		warPath = new File(rootDir, props.getProperty(KEY_PATH_WAR));
		webXmlPath = new File(warPath, props.getProperty(KEY_WEB_XML));
 	}
		
	public TradingDataService getDataService() {
		return dataService;
	}

	public void setDataService(TradingDataService dataService) {
		this.dataService = dataService;
	}

	private void loadStocks() throws Exception {
		logger.info("Load in stocks");
		 dataService.loadStocks();
	}

	public void run() {
		String serverName = props.getProperty(KEY_SERVER_NAME);
		int port = Integer.parseInt(props.getProperty(KEY_SERVER_PORT));
		try {
			logger.info("Starting " + serverName);

			Server server = new Server();
			Connector connector = new SelectChannelConnector();
			connector.setPort(port);
			server.setConnectors(new Connector[] { connector });
			server.setStopAtShutdown(true);

			WebAppContext webapp = new WebAppContext();
			webapp.setContextPath(PATH_CONTEXT);
			webapp.setWar(warPath.getAbsolutePath());
			webapp.setDefaultsDescriptor(webXmlPath.getAbsolutePath());
			webapp.addServlet(new ServletHolder(new StatusServlet()), URL_STATUS);

			/*
			 * webapp.addServlet(new ServletHolder(new ListServlet()),
			 * URL_LIST); webapp.addServlet(new ServletHolder(new
			 * ExecServlet(fpBinMap)), URL_EXEC); webapp.addServlet(new
			 * ServletHolder(new StatusServlet()), URL_STATUS);
			 * webapp.addServlet(new ServletHolder(new
			 * DownloadServlet(fpHomeMap)), URL_DOWNLOAD);
			 */
			server.setHandlers(new Handler[] { webapp });

			QueuedThreadPool queuedThreadPool = new QueuedThreadPool(5);
			queuedThreadPool.setMinThreads(1);
			server.setThreadPool(queuedThreadPool);
			server.start();
			/*
			 * if (logger.isInfoEnabled()) { logger.info(serverName +
			 * " started, url: http://" +
			 * InetAddress.getLocalHost().getHostName() + ":" + port +
			 * URL_STATUS); logger.info(serverName + " started, url: http://" +
			 * InetAddress.getLocalHost().getHostName() + ":" + port +
			 * URL_LIST); logger.info(serverName + " started, url: http://" +
			 * InetAddress.getLocalHost().getHostName() + ":" + port +
			 * URL_EXEC); logger.info(serverName + " started, url: http://" +
			 * InetAddress.getLocalHost().getHostName() + ":" + port +
			 * URL_DOWNLOAD); }
			 */
			server.join();
		} catch (Throwable th) {
			String error = serverName + " web server exception, server is down.";
			logger.fatal(error, th);
			System.exit(1);
		}
	}

	private void init() throws Exception {
		loadProperties();
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
		setDataService((TradingDataService)context.getBean("dataService"));
		loadStocks();
	}

	public static void main(String[] args) {
		TradingServer server = new TradingServer();
		try {
			server.init();
		} 
		catch (Throwable th) 
		{
			logger.error("init error", th);
			System.exit(1);
		}
		new Thread(server).start();
	}
}
