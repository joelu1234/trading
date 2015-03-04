package trading.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import trading.domain.Stock;

public interface ReceiveService {
	void fetchFundamentalData(Collection<Stock> stocks) throws Exception;

	void fetchQuotes(Collection<Stock> stocks) throws Exception;

	void fetchOptionData(Collection<Stock> stocks) throws Exception;

	Collection<Stock> loadStocks(Map<String, List<String>> portMap) throws Exception;

	void saveStats(Collection<Stock> stocks) throws Exception;

	void saveQuotes(Collection<Stock> stocks) throws Exception;

	void saveOptions(Collection<Stock> stocks) throws Exception;

}
