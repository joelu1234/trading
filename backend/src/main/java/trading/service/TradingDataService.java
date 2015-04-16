package trading.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import trading.domain.AlgoResult;
import trading.domain.Stock;
import trading.domain.TrendLine;

public interface TradingDataService {

	void loadStocks(int loadType) throws Exception;
	
	void loadAlgoResults() throws Exception;
	
	void loadTrendLines() throws Exception;

	void saveStats() throws Exception;

	void saveQuotes() throws Exception;

	void saveOptions() throws Exception;
	
	Map<String, Stock> getStocks();
    
	Map<String, List<String>> getPortfolio() throws Exception;
	
	Set<String> getAlgoNames()  throws Exception;
	
	Stock getStock(String ticker);
	
	Stock reload(String ticker) throws Exception;

	Map<String, Map<String, Set<String>>> getCategories();
	
	Map<String, List<AlgoResult>> getAlgoResults();
	
	void saveAlgoResults() throws Exception;
	
	Map<String, List<TrendLine>> getTrendLines();
	
	void saveTrendLines() throws Exception;
}
