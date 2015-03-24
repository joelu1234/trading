package trading.receiver;

import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import trading.domain.Quote;
import trading.domain.Stock;
import trading.util.Utils;


public class GoogleQuoteReceiver {

	private static Logger logger = Logger.getLogger(GoogleQuoteReceiver.class);
	public static final String URL_DATE_FORMAT = "MMM+dd%2'C'+yyyy";
	public static final String QUOTE_DATE_FORMAT = "dd-MMM-yy";

	public static final float SPLIT_THRESHOLD = 0.3f;

	private static Quote parseQuote(String[] strs, Quote prevQ) throws Exception {
		boolean valid = true;
		Quote q = new Quote();
		q.setDate((new SimpleDateFormat(QUOTE_DATE_FORMAT).parse(strs[0])));

		if (!"-".equals(strs[1])) {
			q.setOpen(Float.parseFloat(strs[1]));
		} else {
			valid = false;
			if (prevQ != null) {
				q.setOpen(prevQ.getOpen());
			}
		}
		if (!"-".equals(strs[2])) {
			q.setHigh(Float.parseFloat(strs[2]));
		} else {
			valid = false;
			if (prevQ != null) {
				q.setHigh(prevQ.getHigh());
			}
		}
		if (!"-".equals(strs[3])) {
			q.setLow(Float.parseFloat(strs[3]));
		} else {
			valid = false;
			if (prevQ != null) {
				q.setLow(prevQ.getLow());
			}
		}
		if (!"-".equals(strs[4])) {
			q.setClose(Float.parseFloat(strs[4]));
		} else {
			valid = false;
			if (prevQ != null) {
				q.setClose(prevQ.getClose());
			}
		}
		if (!"-".equals(strs[5]) && !"0".equals(strs[5])) {
			q.setVolume(Long.parseLong(strs[5]));
		} else {
			valid = false;
			if (prevQ != null) {
				q.setVolume(prevQ.getVolume());
			}
		}

		if (!valid) {
			logger.warn("Invalid quote input: " + Arrays.toString(strs));
			if (prevQ == null) {
				return null;
			} else {
				if (q.getClose() > q.getHigh()) {
					q.setHigh(q.getClose());
				} else if (q.getClose() < q.getLow()) {
					q.setLow(q.getClose());
				}
				if (q.getOpen() > q.getHigh()) {
					q.setHigh(q.getOpen());
				} else if (q.getOpen() < q.getLow()) {
					q.setLow(q.getOpen());
				}
				logger.warn("Prev quote: " + prevQ.toString());
				logger.warn("Faked quote: " + q.toString());
			}
		}
		return q;
	}

	private static boolean hasSplit(Quote lastQuote, Quote newQuote) {
		if (newQuote == null) {
			return false; // invalid quote
		}
		float lastClose = lastQuote.getClose();
		float newClose = newQuote.getClose();
		float delta = (newClose - lastClose) / lastClose;
		return Math.abs(delta) > SPLIT_THRESHOLD;
	}

	private static boolean fetch0(Stock stock, Date startDate, Date endDate, String url) throws Exception {
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
				return true;
			}

			if (list.size() > 0 && hasSplit(list.get(list.size() - 1), parseQuote(rowList.get(rowList.size() - 1), null))) {
				list.clear();
				logger.info("Possible split, reload all quotes");
				return false;
			}
			//rowList.remove(0); // header

			Quote prevQuote = null;
			for(int i=rowList.size()-1;i>=1;i--){
				String[] strs = rowList.get(i);
				Quote q = parseQuote(strs, prevQuote);
				if (q != null) {
					list.add(q);
					prevQuote = q;
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return true;
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
		if (fetch0(stock, startDate, endDate, url) == false) {
			startDate = DateUtils.addYears(endDate, -5);
			fetch0(stock, startDate, endDate, url);
		}
	}

	// http://www.google.com/finance/historical?output=csv&q=AAPL&startdate=Feb+19%2C+2011&enddate=Feb+18%2C+2015
	private static String getUrl(Stock stock, Date startDate, Date endDate, String url) {
		StringBuilder sb = new StringBuilder();
		if (stock.getFundamentalData().getExchange().contains("NASD")) {
			sb.append("NASDAQ%3A");   
		} else if (stock.getFundamentalData().getExchange().contains("NYSE")) {
			sb.append("NYSE%3A");
		}
		sb.append(stock.getTicker().replaceAll("-","\\."));
		String ticker =sb.toString();
		String strStartDate=new SimpleDateFormat(URL_DATE_FORMAT).format(startDate);
		String strEndDate=new SimpleDateFormat(URL_DATE_FORMAT).format(endDate);
		return String.format(url, ticker, strStartDate, strEndDate);
	}

	public static void main(String[] args) throws Exception {

		// Date date = Utils.getNextMonthlyOEDate(TimeZone.getTimeZone("GMT"));
		// Date date = Utils.getNextMonthlyOEDate();
		// System.out.println(date+" "+date.getTime());
		Stock stock = new Stock();
		stock.setTicker("T");
		fetch(stock,"http://www.google.com/finance/historical?output=csv&q=%s&startdate=%s&enddate=%s");
		System.out.println(stock.getQuotes());
	}
}
