package trading.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mortbay.jetty.*;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.QueuedThreadPool;

import trading.domain.Stock;
import trading.util.PropertyManager;

public class TradingServer implements Runnable {
	private static final Logger logger = Logger.getLogger(TradingServer.class);

	public static final String URL_STATUS = "/status";
	
	public static final String PATH_CONTEXT = "/";
	
	private PropertyManager propertyManager;

	public PropertyManager getPropertyManager() {
		return propertyManager;
	}

	public void setPropertyManager(PropertyManager propertyManager) {
		this.propertyManager = propertyManager;
	}

	private Map<String, Stock> stockMap = new HashMap<String, Stock>();

	private void loadStocks(){}
	
	public void run() {
		String serverName=propertyManager.getProperty(PropertyManager.JETTY_SERVER_NAME);
		int port = Integer.parseInt(propertyManager.getProperty(PropertyManager.JETTY_SERVER_PORT));
		try {	
			logger.info("Starting " + serverName);

			Server server = new Server();
			Connector connector = new SelectChannelConnector();
			connector.setPort(port);
			server.setConnectors(new Connector[] { connector });
			server.setStopAtShutdown(true);

			WebAppContext webapp = new WebAppContext();
			webapp.setContextPath(PATH_CONTEXT);
			webapp.setWar(propertyManager.getWarPath().getAbsolutePath());
			webapp.setDefaultsDescriptor(propertyManager.getWebXmlPath().getAbsolutePath());
			webapp.addServlet(new ServletHolder(new StatusServlet()), URL_STATUS);

			/*
			webapp.addServlet(new ServletHolder(new ListServlet()), URL_LIST);
			webapp.addServlet(new ServletHolder(new ExecServlet(fpBinMap)), URL_EXEC);
			webapp.addServlet(new ServletHolder(new StatusServlet()), URL_STATUS);
			webapp.addServlet(new ServletHolder(new DownloadServlet(fpHomeMap)), URL_DOWNLOAD);
            */
			server.setHandlers(new Handler[] { webapp });

			QueuedThreadPool queuedThreadPool = new QueuedThreadPool(5);
			queuedThreadPool.setMinThreads(1);
			server.setThreadPool(queuedThreadPool);
			server.start();
			/*
			if (logger.isInfoEnabled()) {
				logger.info(serverName + " started, url: http://" + InetAddress.getLocalHost().getHostName() + ":" + port + URL_STATUS);
				logger.info(serverName + " started, url: http://" + InetAddress.getLocalHost().getHostName() + ":" + port + URL_LIST);
				logger.info(serverName + " started, url: http://" + InetAddress.getLocalHost().getHostName() + ":" + port + URL_EXEC);
				logger.info(serverName + " started, url: http://" + InetAddress.getLocalHost().getHostName() + ":" + port + URL_DOWNLOAD);
			}
			*/
			server.join();
		} catch (Throwable th) {
			String error = serverName + " web server exception, server is down.";
			logger.fatal(error, th);
			System.exit(1);
		}
	}

	private void init() {
		this.setPropertyManager(PropertyManager.getInstance());
		loadStocks();
	}

	public static void main(String[] args) {
		TradingServer  server = new TradingServer();
		server.init();
	    new Thread(server).start();
	}
}
