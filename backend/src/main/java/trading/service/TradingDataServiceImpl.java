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
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import trading.dao.TradingDataDao;
import trading.domain.AlgoResult;
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
import trading.receiver.YahooQuoteReceiver;
import trading.receiver.YahooStatsReceiver;
import trading.util.Constants;
import trading.util.Utils;

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
	@Value("${trading.receiver.yahoo.quote}")
	private String yahooQuoteUrl;
	// @Value( "${trading.receiver.google.option}" )
	// private String googleOptionUrl;

	private TradingDataDao dao;
	private Map<String, Stock> stocks = new HashMap<String, Stock>();
	private Map<String, Map<String, Set<String>>> categories = new TreeMap<String, Map<String, Set<String>>>();
    private Map<String, List<AlgoResult>> algoResults = new HashMap<String, List<AlgoResult>>();
    
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
		return stocks.get(ticker.toUpperCase());
	}

	public Map<String, Map<String, Set<String>>> getCategories() {
		return categories;
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
			if (stock.getFundamentalData().getStockType() != StockType.VIX) {
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
			} else {
				try {
					YahooQuoteReceiver.fetch(stock, yahooQuoteUrl);
				} catch (Throwable th) {
					logger.error("Fetching quotes error for " + stock.getTicker(), th);
					failedStocks.add(stock);
				}
			}
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

	private void createCategories() {
		Map<String, Map<String, Set<String>>> map = new TreeMap<String, Map<String, Set<String>>>();
		for (Stock stock : stocks.values()) {
			String sector = stock.getFundamentalData().getSector();
			String indu = stock.getFundamentalData().getIndustry();
			Map<String, Set<String>> subMap = map.get(sector);
			if (subMap == null) {
				subMap = new TreeMap<String, Set<String>>();
				map.put(sector, subMap);
			}
			Set<String> set = subMap.get(indu);
			if (set == null) {
				set = new TreeSet<String>();
				subMap.put(indu, set);
			}
			set.add(stock.getTicker());
		}
		this.categories = map;
	}

	public Stock reload(String ticker) throws Exception {
		Stock stock = getStock(ticker);
		if (stock == null) {
			throw new Exception("Invalid ticker " + ticker);
		}

		ArrayList<Stock> tempList = new ArrayList<Stock>();
		tempList.add(stock);
		logger.debug("Fetch stock stats for " + ticker);
		List<Stock> failedStocks = fetchFundamentalData(tempList);
		if (failedStocks.size() > 0) {
			throw new Exception("Unable to fetch stats, ticker " + ticker);
		}
		logger.debug("Save stock stats");
		dao.saveStats(stocks.values());

		logger.debug("Fetch stock quotes for " + ticker);
		fetchQuotes(tempList);
		logger.debug("Save stock quotes");
		dao.saveQuotes(stocks.values());
		logger.debug("Fetch stock options for " + ticker);
		fetchOptionData(tempList);
		logger.debug("Save stock options");
		dao.saveOptions(stocks.values());
		return stock;
	}
	
	public void loadStocks(int loadType) throws Exception {

		Map<String, List<String>> portMap = dao.getPortfolio();
		if (this.stocks.size() == 0) {
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
		Collection<Stock> tempList = new ArrayList<Stock>();
		for (String str : addedStocks) {
			Stock stock = new Stock();
			stock.setTicker(str);
			List<String> indices = portMap.get(str);
			if (indices != null) {
				stock.getFundamentalData().setIndices(indices);
				if (StockType.ETF.toString().equalsIgnoreCase(indices.get(0))) {
					stock.getFundamentalData().setStockType(StockType.ETF);
				} else if (StockType.VIX.toString().equalsIgnoreCase(indices.get(0))) {
					Utils.createVIXStats(stock);

				} else {
					stock.getFundamentalData().setStockType(StockType.STOCK);
				}
			}
			stocks.put(str, stock);
			tempList.add(stock);
		}
		
		if(loadType==Constants.LOAD_TYPE_STATS){
			tempList=stocks.values();
		}

		if (tempList.size() > 0) {
			logger.debug("Fetch stock stats");
			List<Stock> failedStocks = fetchFundamentalData(tempList);
			if(loadType==Constants.LOAD_TYPE_STARTUP){
				for (Stock stock : failedStocks) {
					stocks.remove(stock.getTicker());
				}
				logger.debug("Save stock stats");
				dao.saveStats(stocks.values());
			}
		}

		tempList.clear();
		for (Stock stock : stocks.values()) {
			if (stock.getQuotes().size() == 0 || loadType==Constants.LOAD_TYPE_QUOTES) {
				tempList.add(stock);
			}
		}
		if (tempList.size() > 0) {
			logger.debug("Fetch stock quotes");
			fetchQuotes(tempList);
			logger.debug("Save stock quotes");
			dao.saveQuotes(stocks.values());
		}

		tempList.clear();
		for (Stock stock : stocks.values()) {
			if (stock.getOptions().size() == 0 || loadType==Constants.LOAD_TYPE_QUOTES) {
				tempList.add(stock);
			}
		}
		if (tempList.size() > 0) {
			logger.debug("Fetch stock options");
			fetchOptionData(tempList);
			logger.debug("Save stock options");
			dao.saveOptions(stocks.values());
		}
		createCategories();
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

	public Set<String> getAlgoNames() throws Exception {
		return dao.getAlgoNames();
	}

	public void loadAlgoResults() throws Exception {
		this.algoResults=dao.loadAlgoResults();
	}

	public Map<String, List<AlgoResult>> getAlgoResults() {
		return this.algoResults;
	}

	public void saveAlgoResults() throws Exception {
		Date year5Ago = DateUtils.addWeeks(new Date(), -5);
		Collection<List<AlgoResult>> c = algoResults.values();
		for(List<AlgoResult> list : c){
			AlgoResult[] array = list.toArray(new AlgoResult[0]);
			for(AlgoResult r : array){
				if(r.getDate().before(year5Ago)){
					list.remove(r);
				}
				else{
					break;
				}
			}
		}
		dao.saveAlgoResults(this.algoResults);
	}

}
