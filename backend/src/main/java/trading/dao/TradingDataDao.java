package trading.dao;

import java.util.Collection;
import java.util.Map;

import trading.domain.Stock;

public interface TradingDataDao {

	Map<String, Stock> loadStocks() throws Exception;

	void saveStats(Collection<Stock> stocks) throws Exception;

	void saveQuotes(Collection<Stock> stocks) throws Exception;

	void saveOptions(Collection<Stock> stocks) throws Exception;

}
