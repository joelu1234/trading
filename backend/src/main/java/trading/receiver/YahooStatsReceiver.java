package trading.receiver;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import trading.domain.Stock;
import trading.domain.StockType;
import trading.util.PropertyManager;

public class YahooStatsReceiver {
	private static Logger logger = Logger.getLogger(YahooStatsReceiver.class);

	private static final long ONE_BILLION = 1000000000;
	private static final long ONE_MILLION = 1000000;

	private static void fetchSnapshot(Document doc, Stock stock) {
		Element tr = doc.select("td:containsOwn(EBITDA (ttm))").get(1).parent(); //2nd one
	    Elements tds = tr.getElementsByTag("td");
		String value = tds.get(1).text();
		long s = 0;
		if (value.endsWith("B")) {
			s = (long) (Double.parseDouble(value.substring(0,
					value.length() - 1)) * ONE_BILLION);
		} else if (value.endsWith("M")) {
			s = (long) (Double.parseDouble(value.substring(0,
					value.length() - 1)) * ONE_MILLION);
		}
		stock.getFundamentalData().setEbitda(s);

		tr = doc.select("td:containsOwn(Total Debt (mrq))").first().parent(); 
		tds = tr.getElementsByTag("td");
		value = tds.get(1).text();
		s = 0;
		if (value.endsWith("B")) {
			s = (long) (Double.parseDouble(value.substring(0,
					value.length() - 1)) * ONE_BILLION);
		} else if (value.endsWith("M")) {
			s = (long) (Double.parseDouble(value.substring(0,
					value.length() - 1)) * ONE_MILLION);
		}
		stock.getFundamentalData().setTotalDebt(s);
		
		tr = doc.select("td:containsOwn(Operating Cash Flow)").first().parent(); 
		tds = tr.getElementsByTag("td");
		value = tds.get(1).text();
		s = 0;
		if (value.endsWith("B")) {
			s = (long) (Double.parseDouble(value.substring(0,
					value.length() - 1)) * ONE_BILLION);
		} else if (value.endsWith("M")) {
			s = (long) (Double.parseDouble(value.substring(0,
					value.length() - 1)) * ONE_MILLION);
		}
		stock.getFundamentalData().setOperationCashFlow(s);		
		
		tr = doc.select("td:containsOwn(Levered Free Cash Flow)").first().parent(); 
		tds = tr.getElementsByTag("td");
		value = tds.get(1).text();
		s = 0;
		if (value.endsWith("B")) {
			s = (long) (Double.parseDouble(value.substring(0,
					value.length() - 1)) * ONE_BILLION);
		} else if (value.endsWith("M")) {
			s = (long) (Double.parseDouble(value.substring(0,
					value.length() - 1)) * ONE_MILLION);
		}
		stock.getFundamentalData().setLeveredFreeCashFlow(s);		
	}

	public static void fetch(Stock stock) throws Exception {
		String url = PropertyManager.getInstance().getProperty(
				PropertyManager.YAHOO_STATS)
				+ stock.getTicker();
		logger.debug("url=" + url);
		Document doc = Jsoup.connect(url).get();
		fetchSnapshot(doc, stock);

	}

	public static void main(String[] args) throws Exception {
		Stock stock = new Stock();
		stock.setTicker("T");
		stock.setStockType(StockType.STOCK);
		stock.setExchange("[NYSE]");
		fetch(stock);
		System.out.println(stock);
	}
}
