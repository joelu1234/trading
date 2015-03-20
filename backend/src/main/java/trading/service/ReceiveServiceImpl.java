package trading.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

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

public class ReceiveServiceImpl implements ReceiveService {

	private static final Logger logger = Logger.getLogger(ReceiveService.class);
	private TradingDataDao dao;

	public TradingDataDao getDao() {
		return dao;
	}

	public void setDao(TradingDataDao dao) {
		this.dao = dao;
	}

	public List<Stock> fetchFundamentalData(Collection<Stock> stocks) {
		List<Stock> failedStocks = new ArrayList<Stock>();
		for (Stock stock : stocks) {
			try {
				FinvizStatsReceiver.fetch(stock);
				ReutersStatsReceiver.fetch(stock);
				YahooStatsReceiver.fetch(stock);
				YahooAeReceiver.fetch(stock);
			} catch (Throwable th) {
				logger.error("Fetching Fundamemtal data error for " + stock.getTicker(), th);
				failedStocks.add(stock);
			}
		}
		return failedStocks;
	}

	public List<Stock> fetchQuotes(Collection<Stock> stocks) {
		List<Stock> failedStocks = new ArrayList<Stock>();
		for (Stock stock : stocks) {
			try {
				GoogleQuoteReceiver.fetch(stock);
			} catch (Throwable th) {
				logger.error("Fetching quotes error for " + stock.getTicker(), th);
				failedStocks.add(stock);
			}
			List<Quote> quotes = stock.getQuotes();
			BollingerBands.calcBands(quotes);
			K.calcK(quotes);
			MovingAverage.calcSimpleMA(quotes,13);
			MovingAverage.calcSimpleMA(quotes,50);
			MovingAverage.calcSimpleMA(quotes,100);
			RSI.calcRSI(quotes);
		}
		return failedStocks;
	}

	public void fetchOptionData(Collection<Stock> stocks) {
		for (Stock stock : stocks) {
			try {
				YahooOptionReceiver.fetch(stock);
			} catch (Throwable th) {
				logger.error("Fetching options error for " + stock.getTicker(), th);
			}
		}
	}

	public Collection<Stock> loadStocks(Map<String, List<String>> portMap) throws Exception {
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
		saveStats(stockMap.values());
		
		logger.debug("Fetch stock quotes");
		tempList.clear();
		for(Stock stock : stockMap.values()){
			if(stock.getQuotes().size()==0){
				tempList.add(stock);
			}
		}
		fetchQuotes(tempList);
		logger.debug("Save stock quotes");
		saveQuotes(stockMap.values());
		
		logger.debug("Fetch stock options");
		tempList.clear();
		for(Stock stock : stockMap.values()){
			if(stock.getOptions().size()==0){
				tempList.add(stock);
			}
		}
		this.fetchOptionData(tempList);

		logger.debug("Save stock options");
		saveOptions(stockMap.values());
		
		return stockMap.values();
	}

	public void saveStats(Collection<Stock> stocks) throws Exception {
		dao.saveStats(stocks);
	}

	public void saveQuotes(Collection<Stock> stocks) throws Exception {
		dao.saveQuotes(stocks);
	}

	public void saveOptions(Collection<Stock> stocks) throws Exception {
		dao.saveOptions(stocks);
	}

}
