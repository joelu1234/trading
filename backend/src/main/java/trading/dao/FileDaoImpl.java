package trading.dao;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import trading.domain.FundamentalData;
import trading.domain.OptionData;
import trading.domain.Quote;
import trading.domain.Stock;
import trading.util.Constants;
import trading.util.PropertyManager;

public class FileDaoImpl {

	public Map<String, Stock> loadStocks() throws Exception{
		Map<String, Stock> stocks = new HashMap<String, Stock>();
		File file = PropertyManager.getInstance().getStatsFile();
		if (file.exists()) {
			Map<String, FundamentalData> map = getObjectMapper().readValue(
					file,
					new TypeReference<HashMap<String, FundamentalData>>() {
					});
			for (Map.Entry<String, FundamentalData> entry : map.entrySet()) {
				String ticker = entry.getKey();
				Stock stock = new Stock();
				stock.setTicker(ticker);
				stock.setFundamentalData(entry.getValue());
				stocks.put(ticker, stock);
			}
		}
		file = PropertyManager.getInstance().getQuoteFile();
		if (file.exists()) {
			Map<String, List<Quote>> map = getObjectMapper().readValue(file,
					new TypeReference<HashMap<String, List<Quote>>>() {
					});
			for (Map.Entry<String, List<Quote>> entry : map.entrySet()) {
				String ticker = entry.getKey();
				Stock stock = stocks.get(ticker);
				if (stock == null) {
					stock = new Stock();
					stock.setTicker(ticker);
					stocks.put(ticker, stock);
				}
				stock.setQuotes(entry.getValue());
			}

		}
		file = PropertyManager.getInstance().getOptionFile();
		if (file.exists()) {
			Map<String, List<OptionData>> map = getObjectMapper().readValue(
					file,
					new TypeReference<HashMap<String, List<OptionData>>>() {
					});
			for (Map.Entry<String, List<OptionData>> entry : map.entrySet()) {
				String ticker = entry.getKey();
				Stock stock = stocks.get(ticker);
				if (stock == null) {
					stock = new Stock();
					stock.setTicker(ticker);
					stocks.put(ticker, stock);
				}
				stock.setOptions(entry.getValue());
			}

		}
		return stocks;
	}

	public void saveStats(Collection<Stock> stocks) throws IOException {
		Map<String, FundamentalData> map = new HashMap<String, FundamentalData>();
		for (Stock stock : stocks) {
			map.put(stock.getTicker(), stock.getFundamentalData());
		}
		getObjectMapper().writeValue(
				PropertyManager.getInstance().getStatsFile(), map);
	}

	public void saveQuotes(Collection<Stock> stocks) throws Exception {
		Map<String, List<Quote>> map = new HashMap<String, List<Quote>>();
		for (Stock stock : stocks) {
			map.put(stock.getTicker(), stock.getQuotes());
		}
		getObjectMapper().writeValue(
				PropertyManager.getInstance().getQuoteFile(), map);
	}

	public void saveOptions(Collection<Stock> stocks) throws Exception {
		Map<String, List<OptionData>> map = new HashMap<String, List<OptionData>>();
		for (Stock stock : stocks) {
			map.put(stock.getTicker(), stock.getOptions());
		}
		getObjectMapper().writeValue(
				PropertyManager.getInstance().getOptionFile(), map);
	}

	private static ObjectMapper getObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat(Constants.DATE_FORMAT));
		return mapper;
	}

}
