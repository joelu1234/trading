package trading.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mortbay.jetty.*;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.QueuedThreadPool;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import trading.domain.Stock;
import trading.util.PropertyManager;

public class TradingServer implements Runnable {
	private static final Logger logger = Logger.getLogger(TradingServer.class);

	public static final String URL_STATUS = "/status";
	public static final String PATH_CONTEXT = "/";

	private PropertyManager propertyManager;
	private ReceiveService receiveService;
	private Collection<Stock> stocks;
	
	public PropertyManager getPropertyManager() {
		return propertyManager;
	}

	public void setPropertyManager(PropertyManager propertyManager) {
		this.propertyManager = propertyManager;
	}

	public ReceiveService getReceiveService() {
		return receiveService;
	}

	public void setReceiveService(ReceiveService receiveService) {
		this.receiveService = receiveService;
	}

	private void loadStocks() throws Exception {
		logger.info("Load in stocks");
		 Map<String, List<String>> portMap = this.propertyManager.getPortfolio();
		 stocks = receiveService.loadStocks(portMap);
	}

	private void scheduleQuartzJobs() throws Exception {
		
		logger.info("Schedule Quartz jobs");
		JobDetail job1 = JobBuilder.newJob(WeekdayJob.class).withIdentity("Weekday Job", "trading group").build();
		Trigger trigger1 = TriggerBuilder.newTrigger().withIdentity("Weekday Trigger", "trading group").withSchedule(CronScheduleBuilder.cronSchedule(propertyManager.getProperty(PropertyManager.QUARTZ_WEEKDAY_SCHEDULE))).build();
		JobDetail job2 = JobBuilder.newJob(WeekdayJob.class).withIdentity("Weekend Job", "trading group").build();
		Trigger trigger2 = TriggerBuilder.newTrigger().withIdentity("Weekend Trigger", "trading group").withSchedule(CronScheduleBuilder.cronSchedule(propertyManager.getProperty(PropertyManager.QUARTZ_WEEKEND_SCHEDULE))).build();

		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		scheduler.start();
		scheduler.scheduleJob(job1, trigger1);
		scheduler.scheduleJob(job2, trigger2);
	}

	public void run() {
		String serverName = propertyManager.getProperty(PropertyManager.JETTY_SERVER_NAME);
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
		setPropertyManager(PropertyManager.getInstance());
		setReceiveService(new ReceiveServiceImpl());
		loadStocks();
		scheduleQuartzJobs();
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
