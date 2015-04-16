package trading.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import trading.domain.AlgoResult;
import trading.domain.Stock;
import trading.domain.TrendLine;

public interface TradingDataDao {

	Map<String, List<String>> getPortfolio() throws Exception;
	
	Set<Date>  getHolidays() throws Exception;
	
	Map<String, Stock> loadStocks() throws Exception;
	
	Set<String> getAlgoNames()  throws Exception;

	void saveStats(Collection<Stock> stocks) throws Exception;

	void saveQuotes(Collection<Stock> stocks) throws Exception;

	void saveOptions(Collection<Stock> stocks) throws Exception;
	
	Map<String, List<AlgoResult>> loadAlgoResults() throws Exception;
	
	void saveAlgoResults(Map<String, List<AlgoResult>> results) throws Exception;
	
	Map<String, List<TrendLine>> loadTrendLines() throws Exception;
	
	void saveTrendLines(Map<String, List<TrendLine>> lines) throws Exception;

}
