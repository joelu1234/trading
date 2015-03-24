package trading.receiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import trading.domain.QuartlyEps;
import trading.domain.Stock;
import trading.domain.StockType;
import trading.util.Utils;

public class YahooAeReceiver {

	private static Logger logger = Logger.getLogger(YahooAeReceiver.class);
	public static final String DATE_FORMAT = "MMM yy";

	private static void fetchValues(Document doc, Stock stock, String titleSelect, int numEps) throws ParseException {

		Element tr = doc.select(titleSelect).first().parent().parent();
		Elements contents = tr.nextElementSibling().getElementsByTag("td");
		Elements titles = tr.getElementsByTag("th");

		for (int i = 1; i < 1 + numEps; i++) {
			QuartlyEps eps = new QuartlyEps();
			String[] values = titles.get(i).text().split("\\s+");
			String str = values[values.length - 2] + " " + values[values.length - 1];
			eps.setQtr(new SimpleDateFormat(DATE_FORMAT).parse(str));
			eps.setEps(Float.parseFloat(contents.get(i).text()));
			stock.getFundamentalData().getEpsHistory().add(eps);
		}
	}

	public static void fetch(Stock stock, String url) throws Exception {
		if (stock.getFundamentalData().getStockType() == StockType.STOCK && "USA".equalsIgnoreCase(stock.getFundamentalData().getCountry())) {
			url = String.format(url, stock.getTicker());
			logger.debug("url=" + url);
			Document doc = Utils.fetchJsoupDoc(url, 3);
			try {
				fetchValues(doc, stock, ":containsOwn(Earnings Est)", 2);
				fetchValues(doc, stock, ":containsOwn(Earnings History)", 4);
			} catch (Exception ex) {
				logger.warn("unable to fetch for "+stock.getTicker());
				return;
			}
			Collections.sort(stock.getFundamentalData().getEpsHistory());
		} else {
			logger.debug(stock.getTicker() + " is not stock type or not an american stock");
		}
	}

	public static void main(String[] args) throws Exception {
		Stock stock = new Stock();
		stock.setTicker("T");
		stock.getFundamentalData().setStockType(StockType.STOCK);
		stock.getFundamentalData().setCountry("USA");
		stock.getFundamentalData().setExchange("[NYSE]");
		fetch(stock,"http://finance.yahoo.com/q/ae?s=%s");
		System.out.println(stock);
	}
}
