package trading.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import trading.domain.FundamentalData;
import trading.domain.OptionData;
import trading.domain.Quote;
import trading.domain.Stock;
import trading.util.Constants;
import trading.util.PropertyManager;

public class FileDaoImpl {

	private byte[] readFromZipFile(File file, String entryName) throws IOException {
		ZipFile zFile = null;
		try {
			zFile = new ZipFile(file);
			ZipEntry entry = zFile.getEntry(entryName);
			return IOUtils.toByteArray(zFile.getInputStream(entry));
		} finally {
			if (zFile != null) {
				zFile.close();
			}
		}
	}

	public Map<String, Stock> loadStocks() throws Exception {
		Map<String, Stock> stocks = new HashMap<String, Stock>();
		File file = PropertyManager.getInstance().getStatsFile();
		if (file.exists()) {
			String nameWithoutExt = PropertyManager.FILE_STATS;
			Map<String, FundamentalData> map = getObjectMapper().readValue(readFromZipFile(file, nameWithoutExt), new TypeReference<HashMap<String, FundamentalData>>() {
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
			String nameWithoutExt = PropertyManager.FILE_QUOTES;
			Map<String, List<Quote>> map = getObjectMapper().readValue(readFromZipFile(file, nameWithoutExt), new TypeReference<HashMap<String, List<Quote>>>() {
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
			String nameWithoutExt = PropertyManager.FILE_OPTIONS;
			Map<String, List<OptionData>> map = getObjectMapper().readValue(readFromZipFile(file, nameWithoutExt), new TypeReference<HashMap<String, List<OptionData>>>() {
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

	private void writeToZipFile(File file, String entryName, byte[] bytes) throws IOException {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file, false));
		ZipEntry e = new ZipEntry(entryName);
		out.putNextEntry(e);
		out.write(bytes, 0, bytes.length);
		out.closeEntry();
		out.close();
	}

	public void saveStats(Collection<Stock> stocks) throws IOException {
		Map<String, FundamentalData> map = new HashMap<String, FundamentalData>();
		for (Stock stock : stocks) {
			map.put(stock.getTicker(), stock.getFundamentalData());
		}
		File file = PropertyManager.getInstance().getStatsFile();
		String nameWithoutExt = PropertyManager.FILE_STATS;
		byte[] bytes = getObjectMapper().writeValueAsBytes(map);
		writeToZipFile(file, nameWithoutExt, bytes);
	}

	public void saveQuotes(Collection<Stock> stocks) throws Exception {
		Map<String, List<Quote>> map = new HashMap<String, List<Quote>>();
		for (Stock stock : stocks) {
			map.put(stock.getTicker(), stock.getQuotes());
		}
		File file = PropertyManager.getInstance().getQuoteFile();
		String nameWithoutExt = PropertyManager.FILE_QUOTES;
		byte[] bytes = getObjectMapper().writeValueAsBytes(map);
		writeToZipFile(file, nameWithoutExt, bytes);
	}

	public void saveOptions(Collection<Stock> stocks) throws Exception {
		Map<String, List<OptionData>> map = new HashMap<String, List<OptionData>>();
		for (Stock stock : stocks) {
			map.put(stock.getTicker(), stock.getOptions());
		}
		File file = PropertyManager.getInstance().getOptionFile();
		String nameWithoutExt = PropertyManager.FILE_OPTIONS;
		byte[] bytes = getObjectMapper().writeValueAsBytes(map);
		writeToZipFile(file, nameWithoutExt, bytes);
	}

	private static ObjectMapper getObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat(Constants.DATE_FORMAT));
		return mapper;
	}

}
