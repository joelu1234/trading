package trading.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import trading.domain.Stock;

public interface TradingDataDao {

	Map<String, List<String>> getPortfolio() throws Exception;
	
	Set<Date>  getHolidays() throws Exception;
	
	Map<String, Stock> loadStocks() throws Exception;

	void saveStats(Collection<Stock> stocks) throws Exception;

	void saveQuotes(Collection<Stock> stocks) throws Exception;

	void saveOptions(Collection<Stock> stocks) throws Exception;

}
