package trading.receiver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import trading.domain.Quote;
import trading.domain.Stock;
import trading.util.PropertyManager;

public class GoogleQuoteReceiver {

	private static Logger logger = Logger.getLogger(GoogleQuoteReceiver.class);
	public static final String URL_DATE_FORMAT = "MMM+DD%2'C'+yyyy";
	public static final String QUOTE_DATE_FORMAT = "dd-MMM-yy";

	public static final float SPLIT_THRESHOLD = 0.3f;

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

	private static boolean hasSplit(Quote lastQuote, Quote newQuote) {
		float lastClose = lastQuote.getClose();
		float newClose = newQuote.getClose();
		float delta = (newClose - lastClose) / lastClose;
		return Math.abs(delta) > SPLIT_THRESHOLD;
	}

	private static boolean fetch0(Stock stock, Date startDate, Date endDate) throws Exception {
		List<Quote> list = stock.getQuotes();
		String url = getUrl(stock.getTicker(), startDate, endDate);
		BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openStream()));

		CSVReader reader = null;
		try {
			reader = new CSVReader(in);
			List<String[]> rowList = reader.readAll();
			if (rowList.size() < 2) {
				logger.info("no quote for" + stock.getTicker());
				return true;
			}
			if (list.size() > 0 && hasSplit(list.get(list.size() - 1), parseQuote(rowList.get(rowList.size() - 1)))) {
				list.clear();
				return false;
			}
			rowList.remove(0); // header
			for (String[] strs : rowList) {
				list.add(parseQuote(strs));
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return true;
	}

	public static void fetch(Stock stock) throws Exception {
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

		if (fetch0(stock, startDate, endDate) == false) {
			startDate = DateUtils.addYears(endDate, -5);
			fetch0(stock, startDate, endDate);
		}
	}

	// http://www.google.com/finance/historical?output=csv&q=AAPL&startdate=Feb+19%2C+2011&enddate=Feb+18%2C+2015
	private static String getUrl(String ticker, Date startDate, Date endDate) {
		StringBuilder sb = new StringBuilder(PropertyManager.getInstance().getProperty(PropertyManager.GOOGLE_QUOTE));
		sb.append(ticker);
		sb.append("&startdate=");
		sb.append(new SimpleDateFormat(URL_DATE_FORMAT).format(startDate));
		sb.append("&enddate=");
		sb.append(new SimpleDateFormat(URL_DATE_FORMAT).format(endDate));
		return sb.toString();

	}

	public static void main(String[] args) throws Exception {

		// Date date = Utils.getNextMonthlyOEDate(TimeZone.getTimeZone("GMT"));
		// Date date = Utils.getNextMonthlyOEDate();
		// System.out.println(date+" "+date.getTime());
		Stock stock = new Stock();
		stock.setTicker("T");
		fetch(stock);
		System.out.println(stock);
	}
}
