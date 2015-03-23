package trading.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import trading.dao.TradingDataDao;
import trading.domain.Quote;
import trading.domain.Stock;
import trading.domain.StockType;
import trading.indicator.BollingerBands;
import trading.indicator.K;
import trading.indicator.MovingAverage;
import trading.indicator.RSI;
import trading.receiver.FinvizStatsReceiver;
import trading.receiver.GoogleQuoteReceiver;
import trading.receiver.ReutersStatsReceiver;
import trading.receiver.YahooAeReceiver;
import trading.receiver.YahooOptionReceiver;
import trading.receiver.YahooStatsReceiver;

public class TradingDataServiceImpl implements TradingDataService {

	private static final Logger logger = Logger.getLogger(TradingDataServiceImpl.class);
	
	final public static String KEY_FINVIZ_STATS = "trading.receiver.finviz.stats";
	final public static String KEY_REUTERS_STATS = "trading.receiver.reuters.stats";
	final public static String KEY_YAHOO_STATS = "trading.receiver.yahoo.stats";
	final public static String KEY_YAHOO_AE = "trading.receiver.yahoo.ae";
	final public static String KEY_YAHOO_OPTION = "trading.receiver.yahoo.option";
	final public static String KEY_GOOGLE_OPTION = "trading.receiver.google.option";
	final public static String KEY_GOOGLE_QUOTE = "trading.receiver.google.quote";
	
	@Autowired
	private Environment env;
	
	private TradingDataDao dao;

	private Collection<Stock> stocks;

	public TradingDataDao getDao() {
		return dao;
	}

	public void setDao(TradingDataDao dao) {
		this.dao = dao;
	}

	public Collection<Stock> getStocks() {
		return stocks;
	}

	public void setStocks(Collection<Stock> stocks) {
		this.stocks = stocks;
	}
	
	private List<Stock> fetchFundamentalData(Collection<Stock> stocks) {
		List<Stock> failedStocks = new ArrayList<Stock>();
		for (Stock stock : stocks) {
			try {
				FinvizStatsReceiver.fetch(stock, env.getProperty(KEY_FINVIZ_STATS));
				ReutersStatsReceiver.fetch(stock, env.getProperty(KEY_REUTERS_STATS));
				YahooStatsReceiver.fetch(stock, env.getProperty(KEY_YAHOO_STATS));
				YahooAeReceiver.fetch(stock, env.getProperty(KEY_YAHOO_AE));
			} catch (Throwable th) {
				logger.error("Fetching Fundamemtal data error for " + stock.getTicker(), th);
				failedStocks.add(stock);
			}
		}
		return failedStocks;
	}

	private List<Stock> fetchQuotes(Collection<Stock> stocks) {
		List<Stock> failedStocks = new ArrayList<Stock>();
		for (Stock stock : stocks) {
			try {
				GoogleQuoteReceiver.fetch(stock, env.getProperty(KEY_GOOGLE_QUOTE));
			} catch (Throwable th) {
				logger.error("Fetching quotes error for " + stock.getTicker(), th);
				failedStocks.add(stock);
			}
			List<Quote> quotes = stock.getQuotes();
			BollingerBands.calcBands(quotes);
			K.calcK(quotes);
			MovingAverage.calcSimpleMA(quotes, 13);
			MovingAverage.calcSimpleMA(quotes, 50);
			MovingAverage.calcSimpleMA(quotes, 100);
			RSI.calcRSI(quotes);
		}
		return failedStocks;
	}

	
    private Date getNextMonthlyOEDate() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, 3);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		if (System.currentTimeMillis() >= cal.getTimeInMillis()) {
			cal.add(Calendar.MONTH, 1);
			cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
			cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, 3);
		}

		if (isHoliday(cal.getTime())) {
			cal.add(Calendar.DATE, -1);
		}
		return cal.getTime();
	}
	
	private void fetchOptionData(Collection<Stock> stocks) throws Exception {
		Date oeDate = getNextMonthlyOEDate();
		for (Stock stock : stocks) {
			try {
				YahooOptionReceiver.fetch(stock, oeDate, env.getProperty(KEY_YAHOO_OPTION));
			} catch (Throwable th) {
				logger.error("Fetching options error for " + stock.getTicker(), th);
			}
		}
	}

	public void loadStocks() throws Exception {
		
		Map<String, List<String>> portMap = dao.getPortfolio();
		Map<String, Stock> stockMap = dao.loadStocks();
		logger.debug("Existing stock #: " + stockMap.size());
		Set<String> addedStocks = new HashSet<String>(portMap.keySet());
		Set<String> removedStocks = new HashSet<String>(stockMap.keySet());
		// these are new adds
		addedStocks.removeAll(stockMap.keySet());
		// these are removed
		removedStocks.removeAll(portMap.keySet());

		for (String str : removedStocks) {
			stockMap.remove(str);
		}
		logger.debug("Removed stocks: " + removedStocks);
		logger.debug("Added stocks: " + addedStocks);
		ArrayList<Stock> tempList = new ArrayList<Stock>();
		for (String str : addedStocks) {
			Stock stock = new Stock();
			stock.setTicker(str);
			List<String> indices = portMap.get(str);
			if (indices != null) {
				stock.getFundamentalData().setIndices(indices);
				if (StockType.ETF.toString().equalsIgnoreCase(indices.get(0))) {
					stock.getFundamentalData().setStockType(StockType.ETF);
				} else {
					stock.getFundamentalData().setStockType(StockType.STOCK);
				}
			}
			stockMap.put(str, stock);
			tempList.add(stock);
		}

		if (tempList.size() > 0) {
			logger.debug("Fetch stock stats");
			List<Stock> failedStocks = fetchFundamentalData(tempList);
			for (Stock stock : failedStocks) {
				stockMap.remove(stock.getTicker());
				tempList.remove(stock);
			}
		}
		logger.debug("Save stock stats");
		saveStats();

		logger.debug("Fetch stock quotes");
		tempList.clear();
		for (Stock stock : stockMap.values()) {
			if (stock.getQuotes().size() == 0) {
				tempList.add(stock);
			}
		}
		fetchQuotes(tempList);
		logger.debug("Save stock quotes");
		saveQuotes();

		logger.debug("Fetch stock options");
		tempList.clear();
		for (Stock stock : stockMap.values()) {
			if (stock.getOptions().size() == 0) {
				tempList.add(stock);
			}
		}
		this.fetchOptionData(tempList);

		logger.debug("Save stock options");
		saveOptions();

		this.stocks = stockMap.values();
	}

	public void saveStats() throws Exception {
		dao.saveStats(stocks);
	}

	public void saveQuotes() throws Exception {
		dao.saveQuotes(stocks);
	}

	public void saveOptions() throws Exception {
		dao.saveOptions(stocks);
	}
	
	private boolean isHoliday(Date date) throws Exception {
		Set<Date> holidays = dao.getHolidays();
		return holidays != null && holidays.contains(DateUtils.truncate(date, Calendar.DATE));
	}


}
