package trading.receiver;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import trading.domain.OptionData;
import trading.domain.Stock;
import trading.domain.StockType;
import trading.util.Constants;
import trading.util.PropertyManager;
import trading.util.Utils;

public class YahooOptionReceiver {
	private static Logger logger = Logger.getLogger(YahooOptionReceiver.class);

	private static void fetchOptions(Document doc, Stock stock) {
		Elements tables = doc.select("table.details-table");
		for (Element table : tables) {
			boolean isCall = "Calls".equals(table.getElementsByTag("caption").first().text()); // Calls
																								// or
																								// Puts
			Elements trs = table.select("tr[data-row-quote]");
			for (Element tr : trs) {
				Elements tds = tr.getElementsByTag("td");
				OptionData optionData = new OptionData();
				optionData.setCallType(isCall);
				optionData.setStrike(Float.parseFloat(tds.get(0).text().replace(",", "")));
				optionData.setLast(Float.parseFloat(tds.get(2).text().replace(",", "")));
				optionData.setVolume(Long.parseLong(tds.get(7).text().replace(",", "")));
				optionData.setOi(Long.parseLong(tds.get(8).text().replace(",", "")));
				String str = tds.get(9).text().replace(",", "");
				str = str.substring(0, str.length() - 1);
				optionData.setIv(Float.parseFloat(str) * Constants.ONE_PERCENT);
				stock.getOptions().add(optionData);
			}
		}

	}

	public static void fetch(Stock stock) throws Exception {
		if (stock.getFundamentalData().isOptionable()) {
			String url = getUrl(stock.getTicker());
			logger.debug("url=" + url);
			Document doc = null;
			try {
				doc = Jsoup.connect(url).get();
			} catch (Exception ex) {
				logger.warn("http read error, retry", ex);
				Thread.sleep(1000);
				doc = Jsoup.connect(url).get();
			}
			fetchOptions(doc, stock);
		} else {
			logger.info(stock.getTicker() + " is not optionable");
		}
	}

	private static long getGMTSeconds(Date date) {
		Calendar calGMT = Calendar.getInstance();
		calGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
		calGMT.set(Calendar.HOUR_OF_DAY, 0);
		calGMT.set(Calendar.MINUTE, 0);
		calGMT.set(Calendar.SECOND, 0);
		calGMT.set(Calendar.MILLISECOND, 0);

		Calendar calET = Calendar.getInstance();
		calET.setTime(date);
		calGMT.set(Calendar.YEAR, calET.get(Calendar.YEAR));
		calGMT.set(Calendar.MONTH, calET.get(Calendar.MONTH));
		calGMT.set(Calendar.DATE, calET.get(Calendar.DATE));
		return calGMT.getTimeInMillis() / 1000;
	}

	// http://finance.yahoo.com/q/op?s=AAPL&date=1424390400 time is seconds in
	// GMT
	private static String getUrl(String ticker) {
		StringBuilder sb = new StringBuilder(PropertyManager.getProperty(PropertyManager.YAHOO_OPTION));
		sb.append(ticker);
		sb.append("&&date=");
		sb.append(getGMTSeconds(Utils.getNextMonthlyOEDate()));
		return sb.toString();

	}

	public static void main(String[] args) throws Exception {
		Stock stock = new Stock();
		stock.setTicker("T");
		stock.getFundamentalData().setStockType(StockType.STOCK);
		stock.getFundamentalData().setExchange("[NYSE]");
		stock.getFundamentalData().setOptionable(true);
		fetch(stock);
		System.out.println(stock.getOptions());
	}
}
