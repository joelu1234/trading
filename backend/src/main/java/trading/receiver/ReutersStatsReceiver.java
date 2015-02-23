package trading.receiver;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import trading.domain.Stock;
import trading.domain.StockType;
import trading.util.Constants;
import trading.util.PropertyManager;

public class ReutersStatsReceiver {
	private static Logger logger = Logger.getLogger(ReutersStatsReceiver.class);

	private static void fetchSnapshot(Document doc, Stock stock) {
		Element tr = doc.select("td:contains(P/E High - Last 5 Yrs.)").first().parent();
		Elements tds = tr.getElementsByTag("td");
		String value = tds.get(1).text();
		if (!"--".equals(value)) {
			stock.getFundamentalData().setPeHight5yrs(Float.parseFloat(value));
		}

		tr = doc.select("td:contains(P/E Low - Last 5 Yrs.)").first().parent();
		tds = tr.getElementsByTag("td");
		value = tds.get(1).text();
		if (!"--".equals(value)) {
			stock.getFundamentalData().setPeLow5yrs(Float.parseFloat(value));
		}

		tr = doc.select("td:contains(Dividend 5 Year Growth Rate)").first().parent();
		tds = tr.getElementsByTag("td");
		value = tds.get(1).text();
		if (!"--".equals(value)) {
			stock.getFundamentalData().setDivGrowthRate5Yr(Float.parseFloat(value) * Constants.ONE_PERCENT);
		}
	}

	public static void fetch(Stock stock) throws Exception {
		if (stock.getFundamentalData().getStockType() == StockType.STOCK) {
			String url = PropertyManager.getInstance().getProperty(PropertyManager.REUTERS_STATS) + stock.getTicker();
			if (stock.getFundamentalData().getExchange().contains("NASD")) {
				url = url + ".O";
			}
			// NYSE/NASD
			logger.debug("url=" + url);
			Document doc = Jsoup.connect(url).get();
			fetchSnapshot(doc, stock);
		} else {
			logger.debug(stock.getTicker() + " is not stock type");
		}

	}

	public static void main(String[] args) throws Exception {
		Stock stock = new Stock();
		stock.setTicker("T");
		stock.getFundamentalData().setStockType(StockType.STOCK);
		stock.getFundamentalData().setExchange("[NYSE]");
		fetch(stock);
		System.out.println(stock);
	}
}
