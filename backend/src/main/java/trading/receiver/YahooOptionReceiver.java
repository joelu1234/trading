package trading.receiver;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import trading.domain.OptionData;
import trading.domain.Stock;
import trading.util.Constants;
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
				optionData.setType(isCall?OptionData.Type.CALL:OptionData.Type.PUT);
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

	public static void fetch(Stock stock, Date oeDate, String url) throws Exception {
		if (stock.getFundamentalData().isOptionable()) {
			String ticker=stock.getTicker();
			if (stock.getFundamentalData().getStockType()==Stock.Type.VIX) {
				ticker="%5E"+ticker;
			} 
			url = String.format(url, ticker,getGMTSeconds(oeDate));
			logger.debug("url=" + url);
			Document doc = Utils.fetchJsoupDoc(url, 3);
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

	public static void main(String[] args) throws Exception {
		
		
		Date oeDate = new java.text.SimpleDateFormat("yyyy-MM-dd").parse("2015-04-17");
		
		Stock stock = new Stock();
		stock.setTicker("T");
		stock.getFundamentalData().setStockType(Stock.Type.STOCK);
		stock.getFundamentalData().setExchange("[NYSE]");
		stock.getFundamentalData().setOptionable(true);
		fetch(stock, oeDate, "http://finance.yahoo.com/q/op?s=%s&&date=%s");
		System.out.println(stock.getOptions());
	}
	
}
