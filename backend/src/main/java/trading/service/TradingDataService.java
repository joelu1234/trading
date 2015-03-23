package trading.service;

import java.util.Collection;

import trading.domain.Stock;

public interface TradingDataService {

	void loadStocks() throws Exception;

	void saveStats() throws Exception;

	void saveQuotes() throws Exception;

	void saveOptions() throws Exception;
	
	Collection<Stock> getStocks();

}
