package trading.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

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

	@Value("${trading.receiver.finviz.stats}")
	private String finvizStatsUrl;
	@Value("${trading.receiver.reuters.stats}")
	private String reutersStatsUrl;
	@Value("${trading.receiver.yahoo.stats}")
	private String yahooStatsUrl;
	@Value("${trading.receiver.yahoo.ae}")
	private String yahooAeUrl;
	@Value("${trading.receiver.yahoo.option}")
	private String yahooOptionUrl;
	@Value("${trading.receiver.google.quote}")
	private String googleQuoteUrl;
	// @Value( "${trading.receiver.google.option}" )
	// private String googleOptionUrl;

	private TradingDataDao dao;

	private Map<String, Stock> stocks = new HashMap<String, Stock>();

	public TradingDataDao getDao() {
		return dao;
	}

	public void setDao(TradingDataDao dao) {
		this.dao = dao;
	}

	public Map<String, Stock> getStocks() {
		return stocks;
	}

	public Stock getStock(String ticker) {
		return stocks.get(ticker);
	}

	public Map<String, List<String>> getPortfolio() throws Exception {
		return dao.getPortfolio();
	}

	private List<Stock> fetchFundamentalData(Collection<Stock> stocks) {
		List<Stock> failedStocks = new ArrayList<Stock>();
		for (Stock stock : stocks) {
			try {
				FinvizStatsReceiver.fetch(stock, finvizStatsUrl);
				ReutersStatsReceiver.fetch(stock, reutersStatsUrl);
				YahooStatsReceiver.fetch(stock, yahooStatsUrl);
				YahooAeReceiver.fetch(stock, yahooAeUrl);
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
				GoogleQuoteReceiver.fetch(stock, googleQuoteUrl);
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
				YahooOptionReceiver.fetch(stock, oeDate, yahooOptionUrl);
			} catch (Throwable th) {
				logger.error("Fetching options error for " + stock.getTicker(), th);
			}
		}
	}

	public void loadStocks() throws Exception {

		Map<String, List<String>> portMap = dao.getPortfolio();
		if (this.stocks.size() == 0){
			stocks = dao.loadStocks();
		}
		logger.debug("Existing stock #: " + stocks.size());
		Set<String> addedStocks = new HashSet<String>(portMap.keySet());
		Set<String> removedStocks = new HashSet<String>(stocks.keySet());
		// these are new adds
		addedStocks.removeAll(stocks.keySet());
		// these are removed
		removedStocks.removeAll(portMap.keySet());

		for (String str : removedStocks) {
			stocks.remove(str);
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
				} else if(StockType.VIX.toString().equalsIgnoreCase(indices.get(0))){
					stock.getFundamentalData().setStockType(StockType.VIX);
					stock.getFundamentalData().setCountry("USA");
					stock.getFundamentalData().setName("VOLATILITY S&P 500");
					stock.getFundamentalData().setExchange("CBOE");
					stock.getFundamentalData().setOptionable(true);
				}else {
					stock.getFundamentalData().setStockType(StockType.STOCK);
				}
			}
			stocks.put(str, stock);
			tempList.add(stock);
		}

		if (tempList.size() > 0) {
			logger.debug("Fetch stock stats");
			List<Stock> failedStocks = fetchFundamentalData(tempList);
			for (Stock stock : failedStocks) {
				stocks.remove(stock.getTicker());
				tempList.remove(stock);
			}
		}
		logger.debug("Save stock stats");
		dao.saveStats(stocks.values());

		logger.debug("Fetch stock quotes");
		tempList.clear();
		for (Stock stock : stocks.values()) {
			if (stock.getQuotes().size() == 0) {
				tempList.add(stock);
			}
		}
		fetchQuotes(tempList);
		logger.debug("Save stock quotes");
		dao.saveQuotes(stocks.values());

		logger.debug("Fetch stock options");
		tempList.clear();
		for (Stock stock : stocks.values()) {
			if (stock.getOptions().size() == 0) {
				tempList.add(stock);
			}
		}
		this.fetchOptionData(tempList);

		logger.debug("Save stock options");
		dao.saveOptions(stocks.values());
	}

	public void saveStats() throws Exception {
		dao.saveStats(stocks.values());
	}

	public void saveQuotes() throws Exception {
		dao.saveQuotes(stocks.values());
	}

	public void saveOptions() throws Exception {
		dao.saveOptions(stocks.values());
	}

	private boolean isHoliday(Date date) throws Exception {
		Set<Date> holidays = dao.getHolidays();
		return holidays != null && holidays.contains(DateUtils.truncate(date, Calendar.DATE));
	}

}
