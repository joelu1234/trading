package trading.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import trading.dao.FileDaoImpl;
import trading.dao.TradingDataDao;
import trading.domain.Stock;
import trading.domain.StockType;
import trading.receiver.FinvizStatsReceiver;
import trading.receiver.GoogleQuoteReceiver;
import trading.receiver.ReutersStatsReceiver;
import trading.receiver.YahooAeReceiver;
import trading.receiver.YahooOptionReceiver;
import trading.receiver.YahooStatsReceiver;

public class ReceiveServiceImpl implements ReceiveService {
	
	private static final Logger logger = Logger.getLogger(ReceiveService.class);
	private TradingDataDao dao;

	public ReceiveServiceImpl() {
		this.dao = new FileDaoImpl();

	}

	public TradingDataDao getDao() {
		return dao;
	}

	public void setDao(TradingDataDao dao) {
		this.dao = dao;
	}

	public void fetchFundamentalData(Collection<Stock> stocks) throws Exception {
		for(Stock stock : stocks){
			FinvizStatsReceiver.fetch(stock);
			ReutersStatsReceiver.fetch(stock);
			YahooStatsReceiver.fetch(stock);
			YahooAeReceiver.fetch(stock);
		}
	}

	public void fetchQuotes(Collection<Stock> stocks) throws Exception {
		for(Stock stock : stocks){
			GoogleQuoteReceiver.fetch(stock);
			
		}
	}

	public void fetchOptionData(Collection<Stock> stocks) throws Exception {
		for(Stock stock : stocks){
			YahooOptionReceiver.fetch(stock);
		}
	}

	public Collection<Stock> loadStocks(Map<String, List<String>> portMap) throws Exception {

		Map<String, Stock> stockMap = dao.loadStocks();

		Set<String> addedStocks = new HashSet<String>(portMap.keySet());
		Set<String> removedStocks = new HashSet<String>(stockMap.keySet());
		// these are new adds
		addedStocks.removeAll(stockMap.keySet());
		// these are removed
		removedStocks.removeAll(portMap.keySet());

		for (String str : removedStocks) {
			stockMap.remove(str);
		}
        logger.debug("Removed stocks: "+removedStocks);
        logger.debug("Added stocks: "+addedStocks);
		ArrayList<Stock> newStockList = new ArrayList<Stock>();
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
			newStockList.add(stock);
		}
		if (newStockList.size() > 0) {
	        logger.debug("Fetch stock stats");
			fetchFundamentalData(newStockList);
	        logger.debug("Fetch stock quotes");
			fetchQuotes(newStockList);
	        logger.debug("Fetch stock options");
			fetchOptionData(newStockList);
	        logger.debug("Save stock stats");
			saveStats(stockMap.values());
	        logger.debug("Save stock quotes");
			saveQuotes(stockMap.values());
	        logger.debug("Save stock options");
			saveOptions(stockMap.values());
		}
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
