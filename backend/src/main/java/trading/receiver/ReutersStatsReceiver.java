package trading.receiver;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import trading.domain.Stock;
import trading.util.Constants;
import trading.util.Utils;

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

	public static void fetch(Stock stock, String url) throws Exception {
		if (stock.getFundamentalData().getStockType() == Stock.Type.STOCK) {
			String ticker = stock.getTicker().replace("-", ""); // BF-B changr to BFB
			if (stock.getFundamentalData().getExchange().contains("NASD")) {
				ticker = ticker + ".O";
			}
			else if (stock.getFundamentalData().getExchange().contains("NYSE")) {
				ticker = ticker + ".N";
			}
			// NYSE/NASD
			url=String.format(url, ticker);
			logger.debug("url=" + url);
			Document doc = Utils.fetchJsoupDoc(url, 3);
			fetchSnapshot(doc, stock);
		} else {
			logger.debug(stock.getTicker() + " is not stock type");
		}

	}

	public static void main(String[] args) throws Exception {
		Stock stock = new Stock();
		stock.setTicker("T");
		stock.getFundamentalData().setStockType(Stock.Type.STOCK);
		stock.getFundamentalData().setExchange("[NYSE]");
		fetch(stock,"http://www.reuters.com/finance/stocks/financialHighlights?symbol=%s");
		System.out.println(stock);
	}
}
