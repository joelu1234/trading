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
		String value = tds.get(1).text().replace(",", "");
		if (!"--".equals(value)) {
			stock.getFundamentalData().setPeHight5yrs(Float.parseFloat(value));
		}

		tr = doc.select("td:contains(P/E Low - Last 5 Yrs.)").first().parent();
		tds = tr.getElementsByTag("td");
		value = tds.get(1).text().replace(",", "");
		if (!"--".equals(value)) {
			stock.getFundamentalData().setPeLow5yrs(Float.parseFloat(value));
		}

		tr = doc.select("td:contains(Dividend 5 Year Growth Rate)").first().parent();
		tds = tr.getElementsByTag("td");
		value = tds.get(1).text().replace(",", "");
		if (!"--".equals(value)) {
			stock.getFundamentalData().setDivGrowthRate5Yr(Float.parseFloat(value) * Constants.ONE_PERCENT);
		}
	}

	public static void fetch(Stock stock) throws Exception {
		if (stock.getFundamentalData().getStockType() == StockType.STOCK) {
			String ticker = stock.getTicker().replace("-", ""); // BF-B changr to BFB
			String url = PropertyManager.getProperty(PropertyManager.REUTERS_STATS) + ticker;
			if (stock.getFundamentalData().getExchange().contains("NASD")) {
				url = url + ".O";
			}
			else if (stock.getFundamentalData().getExchange().contains("NYSE")) {
				url = url + ".N";
			}
			// NYSE/NASD
			logger.debug("url=" + url);
			
			Document doc = null;
			try {
				doc = Jsoup.connect(url).get();
			} catch (Exception ex) {
				logger.warn("http read error, retry", ex);
				Thread.sleep(1000);
				doc = Jsoup.connect(url).get();
			}
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
