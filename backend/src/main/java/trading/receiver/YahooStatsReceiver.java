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

public class YahooStatsReceiver {
	private static Logger logger = Logger.getLogger(YahooStatsReceiver.class);

	private static void fetchSnapshot(Document doc, Stock stock) {
		Element tr = doc.select("td:containsOwn(EBITDA (ttm))").get(1).parent(); // 2nd
																					// one
		Elements tds = tr.getElementsByTag("td");
		String value = tds.get(1).text().replace(",", "");
		long s = 0;
		if (value.endsWith("B")) {
			s = (long) (Double.parseDouble(value.substring(0, value.length() - 1)) * Constants.ONE_BILLION);
		} else if (value.endsWith("M")) {
			s = (long) (Double.parseDouble(value.substring(0, value.length() - 1)) * Constants.ONE_MILLION);
		}
		stock.getFundamentalData().setEbitda(s);

		tr = doc.select("td:containsOwn(Total Debt (mrq))").first().parent();
		tds = tr.getElementsByTag("td");
		value = tds.get(1).text().replace(",", "");
		s = 0;
		if (value.endsWith("B")) {
			s = (long) (Double.parseDouble(value.substring(0, value.length() - 1)) * Constants.ONE_BILLION);
		} else if (value.endsWith("M")) {
			s = (long) (Double.parseDouble(value.substring(0, value.length() - 1)) * Constants.ONE_MILLION);
		}
		stock.getFundamentalData().setTotalDebt(s);

		tr = doc.select("td:containsOwn(Operating Cash Flow)").first().parent();
		tds = tr.getElementsByTag("td");
		value = tds.get(1).text().replace(",", "");
		s = 0;
		if (value.endsWith("B")) {
			s = (long) (Double.parseDouble(value.substring(0, value.length() - 1)) * Constants.ONE_BILLION);
		} else if (value.endsWith("M")) {
			s = (long) (Double.parseDouble(value.substring(0, value.length() - 1)) * Constants.ONE_MILLION);
		}
		stock.getFundamentalData().setOperationCashFlow(s);

		tr = doc.select("td:containsOwn(Levered Free Cash Flow)").first().parent();
		tds = tr.getElementsByTag("td");
		value = tds.get(1).text().replace(",", "");
		s = 0;
		if (value.endsWith("B")) {
			s = (long) (Double.parseDouble(value.substring(0, value.length() - 1)) * Constants.ONE_BILLION);
		} else if (value.endsWith("M")) {
			s = (long) (Double.parseDouble(value.substring(0, value.length() - 1)) * Constants.ONE_MILLION);
		}
		stock.getFundamentalData().setLeveredFreeCashFlow(s);
	}

	public static void fetch(Stock stock) throws Exception {
		if (stock.getFundamentalData().getStockType() == StockType.STOCK) {
			String url = PropertyManager.getInstance().getProperty(PropertyManager.YAHOO_STATS) + stock.getTicker();
			logger.debug("url=" + url);
			Document doc = null;
			try
			{
				doc = Jsoup.connect(url).get();
			}
			catch(Exception ex)
			{
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
