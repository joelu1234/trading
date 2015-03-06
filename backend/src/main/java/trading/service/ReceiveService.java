package trading.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import trading.domain.Stock;

public interface ReceiveService {
	List<Stock> fetchFundamentalData(Collection<Stock> stocks);

	List<Stock> fetchQuotes(Collection<Stock> stocks);

	void fetchOptionData(Collection<Stock> stocks);

	Collection<Stock> loadStocks(Map<String, List<String>> portMap) throws Exception;

	void saveStats(Collection<Stock> stocks) throws Exception;

	void saveQuotes(Collection<Stock> stocks) throws Exception;

	void saveOptions(Collection<Stock> stocks) throws Exception;

}
