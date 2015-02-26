package com.theice.fileprocessor.web;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.mortbay.jetty.*;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.QueuedThreadPool;

public class AdminServer implements Runnable
{
   private static final Logger logger = Logger.getLogger(AdminServer.class);
 
	public static final String KEY_PROP_FILE_NAME = "/conf.properties";//this file need to be in classpath
	public static final String KEY_SERVER_NAME = "server.name";
	public static final String KEY_PORT = "server.port";
	
	public static final String KEY_SETTLE_HOME = "settle.home";
	public static final String KEY_TCC_HOME = "tcc.home";
	public static final String KEY_NGX_HOME = "ngx.home";
	
	public static final String KEY_SETTLE_BIN = "settle.bin";
	public static final String KEY_TCC_BIN = "tcc.bin";
	public static final String KEY_NGX_BIN = "ngx.bin";
	
	public static final String URL_LIST = "/list";
	public static final String URL_EXEC = "/exec";
	public static final String URL_STATUS = "/status";
	public static final String URL_DOWNLOAD = "/download";
	
	public static final String PATH_WEB_XML = "./webapp/WEB-INF/web.xml";
	public static final String PATH_CONTEXT = "/";
	public static final String PATH_WAR = "./webapp";
	
   private static String serverName;
   private static int port;
   
   private static Map<FpName,String> fpHomeMap = new HashMap<FpName, String>();
   private static Map<FpName,String> fpBinMap = new HashMap<FpName, String>();
   
   private static Map<String, List<Job>> clientMap = new HashMap<String, List<Job>>();
      
   public static List<Job> getJobList(String client)
   {
   	client = client==null ? "" : client.toLowerCase();
   	return clientMap.get(client);
   }
   
   public void run()
   {
      try
      {
         logger.info("Starting File processor Web Server "+serverName);
         
         Server server = new Server();
         Connector connector = new SelectChannelConnector();
         connector.setPort(port);
         server.setConnectors(new Connector[]
         { connector });
         server.setStopAtShutdown(true);

         WebAppContext webapp = new WebAppContext();
         webapp.setContextPath(PATH_CONTEXT);
         webapp.setWar(PATH_WAR);
         webapp.setDefaultsDescriptor(PATH_WEB_XML);
         
         webapp.addServlet(new ServletHolder(new ListServlet()), URL_LIST);
         webapp.addServlet(new ServletHolder(new ExecServlet(fpBinMap)), URL_EXEC);
         webapp.addServlet(new ServletHolder(new StatusServlet()), URL_STATUS);
         webapp.addServlet(new ServletHolder(new DownloadServlet(fpHomeMap)), URL_DOWNLOAD);
         
         server.setHandlers(new Handler[]
         { webapp });

         QueuedThreadPool queuedThreadPool = new QueuedThreadPool(5);
         queuedThreadPool.setMinThreads(1);
         server.setThreadPool(queuedThreadPool);
         server.start();
         if (logger.isInfoEnabled())
         {
            logger.info(serverName + " started, url: http://" + InetAddress.getLocalHost().getHostName() + ":" + port + URL_STATUS);
            logger.info(serverName + " started, url: http://" + InetAddress.getLocalHost().getHostName() + ":" + port + URL_LIST);
            logger.info(serverName + " started, url: http://" + InetAddress.getLocalHost().getHostName() + ":" + port + URL_EXEC);
            logger.info(serverName + " started, url: http://" + InetAddress.getLocalHost().getHostName() + ":" + port + URL_DOWNLOAD);
         }
         server.join();
      }
      catch (Throwable th)
      {
      	String error = serverName+" web server exception, server is down.";
         logger.fatal(error, th);
         Util.snmpLog(error, th.getMessage(), true);
      }
   }
   
   public static boolean loadAllJobs()
   {
      Map<String, List<Job>> localClientMap = new HashMap<String, List<Job>>();
     	try
   	{
     		List<Job> jobList = JdbcHelper.getJobs();
         for(Job job : jobList)
         {
         	String[] clients=job.getStrApps().split(",");
         	job.setStrApps("");
         	job.setRoles(Arrays.asList(job.getStrRoles().split(",")));
         	job.setStrRoles("");
         	for(String client : clients)
		   	{
		   		List<Job> list = localClientMap.get(client);
		   		if(list == null)
		   		{
		   			list = new ArrayList<Job>();
		   			localClientMap.put(client, list);
		   		}
		   		list.add(job);
		   	}
         }
		   
		   for(List<Job> list : localClientMap.values())
		   {
		   	Collections.sort(list);
		   }
		   
		   clientMap=Collections.unmodifiableMap(localClientMap);
			logger.debug(" FP clients: "+clientMap.keySet().size());
			return true;
   	}
     	catch(Throwable th)
   	{
   		String err= "Unable to load Jobs";
			logger.error(err, th);
         Util.snmpLog(err, th.getMessage(), true);			
			return false;
   	}
   }

   private static void loadProperties()
   {
   	Properties props  = new Properties();    
   	try
   	{	
      	props.load(AdminServer.class.getResourceAsStream(KEY_PROP_FILE_NAME));
      	serverName=props.getProperty(KEY_SERVER_NAME);
      	port=Integer.parseInt(props.getProperty(KEY_PORT));    
      	
      	Map<FpName,String> localFpHomeMap = new HashMap<FpName, String>();
      	Map<FpName,String> localFpBinMap = new HashMap<FpName, String>();
      	localFpHomeMap.put(FpName.SETTLE,props.getProperty(KEY_SETTLE_HOME));
      	localFpBinMap.put(FpName.SETTLE,props.getProperty(KEY_SETTLE_BIN));
      	localFpHomeMap.put(FpName.TCC,props.getProperty(KEY_TCC_HOME));
      	localFpBinMap.put(FpName.TCC,props.getProperty(KEY_TCC_BIN));
      	localFpHomeMap.put(FpName.NGX,props.getProperty(KEY_NGX_HOME));
      	localFpBinMap.put(FpName.NGX,props.getProperty(KEY_NGX_BIN));
      	fpHomeMap=Collections.unmodifiableMap(localFpHomeMap);
      	fpBinMap=Collections.unmodifiableMap(localFpBinMap);
      	logger.debug("fpHomeMap="+fpHomeMap);
      	logger.debug("fpBinMap="+fpBinMap);
   	}
   	catch(Throwable th)
   	{
   		String err= "Unable to load "+KEY_PROP_FILE_NAME;
			logger.error(err, th);
         Util.snmpLog(err, th.getMessage(), true);
			System.exit(1);
   	}
   }
   
   public static void main(String[] args)
   {
   	loadProperties();
   	if(loadAllJobs()==true)
   	{
   		(new Thread(new  AdminServer())).start();
   	}
   }
}

