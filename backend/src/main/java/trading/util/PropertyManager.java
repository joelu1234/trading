package trading.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

public class PropertyManager {
	private static Logger logger = Logger.getLogger(PropertyManager.class);

	final public static String FINVIZ_STATS = "trading.receiver.finviz.stats";
	final public static String REUTERS_STATS = "trading.receiver.reuters.stats";
	final public static String YAHOO_STATS = "trading.receiver.yahoo.stats";
	final public static String YAHOO_AE = "trading.receiver.yahoo.ae";
	final public static String YAHOO_SUMMARY = "trading.receiver.yahoo.summary";
	final public static String YAHOO_OPTION = "trading.receiver.yahoo.option";
	final public static String GOOGLE_OPTION = "trading.receiver.google.option";
	final public static String GOOGLE_QUOTE = "trading.receiver.google.quote";

	final public static String MAIL_SMTPS_AUTH = "mail.smtps.auth";
	final public static String MAIL_SMTPS_HOST = "mail.smtps.host";
	final public static String MAIL_SMTPS_PORT = "mail.smtps.port";
	final public static String MAIL_TO = "mail.to";
	final public static String MAIL_FROM = "mail.from";
	final public static String MAIL_USERNAME = "mail.username";
	final public static String MAIL_PASSWORD = "mail.password";

	final public static String FILE_CONF = "config/conf.properties";
	final public static String FILE_HOLIDAY = "config/holidays.properties";
	final public static String DIR_PORTFOLIO = "portfolio";
	final public static String DIR_DATA = "stockdata";
	final public static String DIR_CONFIG = "config";

	final public static String FILE_STATS = "data.stats.file";
	final public static String FILE_QUOTES = "data.quote.file";
	final public static String FILE_OPTIONS = "data.option.file";

	final public static String JETTY_SERVER_NAME = "jetty.server.name";
	final public static String JETTY_SERVER_PORT = "jetty.server.port";
	final public static String JETTY_PATH_WAR = "jetty.path.war";
	final public static String JETTY_WEB_XML = "jetty.web.xml";

	final public static String QUARTZ_WEEKDAY_SCHEDULE = "quartz.weekday.schedule";
	final public static String QUARTZ_WEEKEND_SCHEDULE = "quartz.weekend.schedule";

	private static Properties props = new Properties();

	private static Set<Date> holidays = new HashSet<Date>();

	private Map<String, List<String>> portfolio = new HashMap<String, List<String>>();

	private File statsFile;
	private File quoteFile;
	private File optionFile;

	private File warPath;
	private File webXmlPath;

	private void loadHolidays(Set<String> set) throws ParseException {
		for (String str : set) {
			holidays.add(new SimpleDateFormat(Constants.DATE_FORMAT).parse(str));
		}
	}

	private void loadPortfolio(URL dirUri) throws URISyntaxException, FileNotFoundException, IOException {
		File dir = new File(dirUri.toURI());
		for (String name : dir.list()) {
			String index = name.split("\\.")[0].toUpperCase();
			Properties stocks = new Properties();
			File f = new File(dir, name);
			if (f.isDirectory())
				continue;
			stocks.load(new FileInputStream(f));
			for (String stock : stocks.stringPropertyNames()) {
				stock=stock.replaceAll("\\.", "-"); //BF.B change to BF-B
				List<String> indices = portfolio.get(stock);
				if (indices == null) {
					indices = new ArrayList<String>();
					portfolio.put(stock, indices);
				}
				indices.add(index);
			}
		}
	}

	public void loadRefernceData() throws Exception {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		props.load(loader.getResourceAsStream(FILE_CONF));
		Properties hProps = new Properties();
		hProps.load(loader.getResourceAsStream(FILE_HOLIDAY));
		logger.debug("Load holidays...");
		loadHolidays(hProps.stringPropertyNames());
		logger.debug("LHolidays: "+hProps);
		logger.debug("Load Portfolio...");		
		loadPortfolio(loader.getResource(DIR_PORTFOLIO));
		logger.debug("Portfolio: "+portfolio);

		configDataFiles(new File(loader.getResource(DIR_DATA).toURI()));
		configJettyPaths(new File(loader.getResource(DIR_CONFIG).toURI()));
	}

	private void configDataFiles(File rootDir) {
		statsFile = new File(rootDir, getProperty(FILE_STATS) + ".zip");
		quoteFile = new File(rootDir, getProperty(FILE_QUOTES) + ".zip");
		optionFile = new File(rootDir, getProperty(FILE_OPTIONS) + ".zip");
	}

	private void configJettyPaths(File rootDir) {
		warPath = new File(rootDir, getProperty(JETTY_PATH_WAR));
		webXmlPath = new File(warPath, getProperty(JETTY_WEB_XML));
	}

	public static String getProperty(String key) {
		return props.getProperty(key, "");
	}
	
	public static void setProperty(String key,String value) {
		props.setProperty(key, value);
	}


	public static boolean isHoliday(Date date) {
		return holidays != null && holidays.contains(DateUtils.truncate(date, Calendar.DATE));
	}

	public Map<String, List<String>> getPortfolio() {
		return portfolio;
	}

	public File getStatsFile() {
		return statsFile;
	}

	public File getQuoteFile() {
		return quoteFile;
	}

	public File getOptionFile() {
		return optionFile;
	}

	public File getWarPath() {
		return warPath;
	}

	public void setWarPath(File warPath) {
		this.warPath = warPath;
	}

	public File getWebXmlPath() {
		return webXmlPath;
	}

	public void setWebXmlPath(File webXmlPath) {
		this.webXmlPath = webXmlPath;
	}

}
