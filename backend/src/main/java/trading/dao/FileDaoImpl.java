package trading.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import trading.domain.AlgoResult;
import trading.domain.FundamentalData;
import trading.domain.OptionData;
import trading.domain.Quote;
import trading.domain.Stock;
import trading.domain.TrendLine;
import trading.util.Constants;
import trading.util.CustomObjectMapper;

public class FileDaoImpl implements TradingDataDao {

	private static Logger logger = Logger.getLogger(TradingDataDao.class);

	final public static String DIR_DATA = "stockdata";
	final public static String DIR_PORTFOLIO = DIR_DATA + "/portfolio";
	final public static String FILE_HOLIDAY = DIR_DATA + "/holidays.properties";
	final public static String FILE_ALGO_DEF = DIR_DATA + "/algodef.properties";

	private File statsFile;
	private File quoteFile;
	private File optionFile;
	private File algoFile;
	private File trendLineFile;

	@Value("${data.stats.file}")
	private String statsFileName;
	@Value("${data.quote.file}")
	private String quoteFileName;
	@Value("${data.option.file}")
	private String optionFileName;
	@Value("${data.algo.file}")
	private String algoFileName;
	@Value("${data.trend.line.file}")
	private String trendLineFileName;

	private Map<String, List<String>> portfolio = new HashMap<String, List<String>>();

	@PostConstruct
	public void init() throws Exception {

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		File rootDir = (new File(loader.getResource(DIR_DATA).toURI()));
		statsFile = new File(rootDir, statsFileName + ".zip");
		quoteFile = new File(rootDir, quoteFileName + ".zip");
		optionFile = new File(rootDir, optionFileName + ".zip");
		algoFile = new File(rootDir, algoFileName);
		trendLineFile = new File(rootDir, trendLineFileName);

		logger.debug("Load Portfolio...");
		loadPortfolio(loader.getResource(DIR_PORTFOLIO));
		logger.debug("Portfolio: " + portfolio);
	}

	private void loadPortfolio(URL dirUri) throws URISyntaxException, FileNotFoundException, IOException {
		File dir = new File(dirUri.toURI());
		for (String name : dir.list()) {
			String index = name.split("\\.")[0].toUpperCase();
			Properties stocks = new Properties();
			File f = new File(dir, name);
			if (f.isDirectory())
				continue;
			stocks.load(new FileInputStream(f));
			for (String stock : stocks.stringPropertyNames()) {
				stock = stock.replaceAll("\\.", "-"); // BF.B change to BF-B
				List<String> indices = portfolio.get(stock);
				if (indices == null) {
					indices = new ArrayList<String>();
					portfolio.put(stock, indices);
				}
				indices.add(index);
			}
		}
	}

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
		if (statsFile.exists()) {
			Map<String, FundamentalData> map = getObjectMapper().readValue(readFromZipFile(statsFile, statsFileName), new TypeReference<HashMap<String, FundamentalData>>() {
			});
			for (Map.Entry<String, FundamentalData> entry : map.entrySet()) {
				String ticker = entry.getKey();
				Stock stock = new Stock();
				stock.setTicker(ticker);
				stock.setFundamentalData(entry.getValue());
				stocks.put(ticker, stock);
			}
		}

		if (quoteFile.exists()) {
			Map<String, List<Quote>> map = getObjectMapper().readValue(readFromZipFile(quoteFile, quoteFileName), new TypeReference<HashMap<String, List<Quote>>>() {
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
		if (optionFile.exists()) {
			Map<String, List<OptionData>> map = getObjectMapper().readValue(readFromZipFile(optionFile, optionFileName), new TypeReference<HashMap<String, List<OptionData>>>() {
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

	public void saveStats(Collection<Stock> stocks) throws Exception {
		Map<String, FundamentalData> map = new HashMap<String, FundamentalData>();
		for (Stock stock : stocks) {
			map.put(stock.getTicker(), stock.getFundamentalData());
		}
		byte[] bytes = getObjectMapper().writeValueAsBytes(map);
		writeToZipFile(statsFile, statsFileName, bytes);
	}

	public void saveQuotes(Collection<Stock> stocks) throws Exception {
		Map<String, List<Quote>> map = new HashMap<String, List<Quote>>();
		for (Stock stock : stocks) {
			map.put(stock.getTicker(), stock.getQuotes());
		}
		byte[] bytes = getObjectMapper().writeValueAsBytes(map);
		writeToZipFile(quoteFile, quoteFileName, bytes);
	}

	public void saveOptions(Collection<Stock> stocks) throws Exception {
		Map<String, List<OptionData>> map = new HashMap<String, List<OptionData>>();
		for (Stock stock : stocks) {
			map.put(stock.getTicker(), stock.getOptions());
		}
		byte[] bytes = getObjectMapper().writeValueAsBytes(map);
		writeToZipFile(optionFile, optionFileName, bytes);
	}

	private static ObjectMapper getObjectMapper() {
		return new CustomObjectMapper();
	}

	public Map<String, List<String>> getPortfolio() throws Exception {
		return this.portfolio;
	}

	public Set<Date> getHolidays() throws Exception {
		Set<Date> holidays = new HashSet<Date>();
		logger.debug("Load holidays...");
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties props = new Properties();
		props.load(loader.getResourceAsStream(FILE_HOLIDAY));
		logger.debug("Holidays: " + props);
		for (String str : props.stringPropertyNames()) {
			holidays.add(new SimpleDateFormat(Constants.DATE_FORMAT).parse(str));
		}
		return holidays;
	}

	public Set<String> getAlgoNames() throws Exception {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		logger.debug("Load algo names...");
		Properties props = new Properties();
		props.load(loader.getResourceAsStream(FILE_ALGO_DEF));
		return props.stringPropertyNames();

	}

	public Map<String, List<AlgoResult>> loadAlgoResults() throws Exception {
		if (algoFile.exists()) {
			return getObjectMapper().readValue(algoFile, new TypeReference<HashMap<String, List<AlgoResult>>>() {
			});
		} else {
			return new HashMap<String, List<AlgoResult>>();
		}
	}

	public void saveAlgoResults(Map<String, List<AlgoResult>> results) throws Exception {
		getObjectMapper().writeValue(algoFile, results);
	}

	@Override
	public Map<String, List<TrendLine>> loadTrendLines() throws Exception {
		if (trendLineFile.exists()) {
			return getObjectMapper().readValue(trendLineFile, new TypeReference<HashMap<String, List<TrendLine>>>() {
			});
		} else {
			return new HashMap<String, List<TrendLine>>();
		}
	}

	@Override
	public void saveTrendLines(Map<String, List<TrendLine>> lines) throws Exception {
		getObjectMapper().writeValue(trendLineFile, lines);
		
	}

}
