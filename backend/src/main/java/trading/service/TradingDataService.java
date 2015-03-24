package trading.service;

import java.util.List;
import java.util.Map;

import trading.domain.Stock;

public interface TradingDataService {

	void loadStocks() throws Exception;

	void saveStats() throws Exception;

	void saveQuotes() throws Exception;

	void saveOptions() throws Exception;
	
	Map<String, Stock> getStocks();
    
	Map<String, List<String>> getPortfolio() throws Exception;
	
	Stock getStock(String ticker);

}
