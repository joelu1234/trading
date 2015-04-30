package trading.receiver;

import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import trading.domain.Quote;
import trading.domain.Stock;
import trading.util.Utils;


public class YahooQuoteReceiver {

	private static Logger logger = Logger.getLogger(YahooQuoteReceiver.class);
	
	public static final String QUOTE_DATE_FORMAT = "yyyy-MM-dd";
	
	private static Quote parseQuote(String[] strs) throws Exception {
		Quote q = new Quote();
		q.setDate((new SimpleDateFormat(QUOTE_DATE_FORMAT).parse(strs[0])));
		q.setOpen(Float.parseFloat(strs[1]));
        q.setHigh(Float.parseFloat(strs[2]));
		q.setLow(Float.parseFloat(strs[3]));
	    q.setClose(Float.parseFloat(strs[4]));
		q.setVolume(Long.parseLong(strs[5]));
		return q;
	}

	private static void fetch0(Stock stock, Date startDate, Date endDate, String url) throws Exception {
		List<Quote> list = stock.getQuotes();
		url = getUrl(stock, startDate, endDate, url);
		logger.debug("url=" + url);
		BufferedReader in = Utils.getReaderFromUrl(url, 3);
		CSVReader reader = null;
		try {
			reader = new CSVReader(in);
			List<String[]> rowList = reader.readAll();
			if (rowList.size() < 2) {
				logger.info("no quote for" + stock.getTicker());
				return;
			}
			
			for(int i=rowList.size()-1;i>=1;i--){
				String[] strs = rowList.get(i);
			    list.add(parseQuote(strs));
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	public static void fetch(Stock stock, String url) throws Exception {
		List<Quote> list = stock.getQuotes();
		Date endDate = DateUtils.truncate(new Date(), Calendar.DATE);
		Date startDate = null;
		if (list.size() == 0) {
			startDate = DateUtils.addYears(endDate, -5);
		} else {
			startDate = DateUtils.addDays(list.get(list.size() - 1).getDate(), 1);
		}
		if (startDate.after(endDate)) {
			logger.info("Quote up to date for " + stock.getTicker());
			return;
		}
		endDate = DateUtils.addDays(endDate, 1);
	    fetch0(stock, startDate, endDate, url);
	}

	private static String getUrl(Stock stock, Date startDate, Date endDate, String url) {
		String ticker = stock.getTicker();
		if (stock.getFundamentalData().getStockType()==Stock.Type.VIX) {
			ticker="%5E"+ticker;
		} 
		Calendar cal = Calendar.getInstance();
		cal.setTime(endDate);
		int endYear = cal.get(Calendar.YEAR);
		int endMonth = cal.get(Calendar.MONTH);
		int endDay = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(startDate);
		int startYear = cal.get(Calendar.YEAR);
		int startMonth = cal.get(Calendar.MONTH);
		int startDay = cal.get(Calendar.DAY_OF_MONTH);		
		return String.format(url, ticker, endMonth,endDay,endYear,startMonth,startDay,startYear);
	}

	public static void main(String[] args) throws Exception {
		Stock stock = new Stock();
		stock.setTicker("VIX");
		Utils.createVIXStats(stock);
		fetch(stock,"http://real-chart.finance.yahoo.com/table.csv?s=%s&d=%s&e=%s&f=%s&g=d&a=%s&b=%s&c=%s&ignore=.csv");
		System.out.println(stock.getQuotes());
	}
}
