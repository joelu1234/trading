package trading.receiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import trading.domain.AnalystOpinion;
import trading.domain.Stock;
import trading.domain.StockType;
import trading.util.PropertyManager;
import trading.util.Constants;

public class FinvizStatsReceiver {
	private static Logger logger = Logger.getLogger(FinvizStatsReceiver.class);

	public static final String DATE_FORMAT = "MMM-dd-yy";
	
	private static void fetchSectorAndIndustry(Document doc, Stock stock) {
		Element table = doc.select("table.fullview-title").first();
		Elements links = table.select("a[href]");
		stock.getFundamentalData().setName(links.get(1).text());
		stock.getFundamentalData().setSector(links.get(2).text());
		stock.getFundamentalData().setIndustry(links.get(3).text());
		Element span = table.getElementsByTag("span").first();
		stock.getFundamentalData().setExchange(span.text());
		
	}

	private static void fetchSnapshot(Document doc, Stock stock) {
		Element table = doc.select("table.snapshot-table2").first();
		Elements links = table.select("td");
		Map<String, String> props = new HashMap<String, String>();
		for (int i = 0; i < links.size(); i += 2) {
			String key = links.get(i).text();
			String value = links.get(i + 1).text();
			if ("EPS next Y".equals(key) && value.contains("%")) {
				key = "EPS next Y%";
			}
			if (!"-".equals(value)) {
				props.put(key, value);
			}
		}

		for (Map.Entry<String, String> entry : props.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if ("Shs Outstand".equals(key)) {
				long s = 0;
				if (value.endsWith("B")) {
					s = (long) (Double.parseDouble(value.substring(0,
							value.length() - 1)) * Constants.ONE_BILLION);
				} else if (value.endsWith("M")) {
					s = (long) (Double.parseDouble(value.substring(0,
							value.length() - 1)) * Constants.ONE_MILLION);
				}

				stock.getFundamentalData().setShareOutstanding(s);
			} else if ("Shs Float".equals(key)) {
				long s = 0;
				if (value.endsWith("B")) {
					s = (long) (Double.parseDouble(value.substring(0,
							value.length() - 1)) * Constants.ONE_BILLION);
				} else if (value.endsWith("M")) {
					s = (long) (Double.parseDouble(value.substring(0,
							value.length() - 1)) * Constants.ONE_MILLION);
				}

				stock.getFundamentalData().setShareFloat(s);
			} else if ("Book/sh".equals(key)) {
				stock.getFundamentalData().setBookPerShare(
						Float.parseFloat(value));
			} else if ("Cash/sh".equals(key)) {
				stock.getFundamentalData().setCashPerShare(
						Float.parseFloat(value));
			} else if ("Dividend".equals(key)) {
				stock.getFundamentalData().setDividend(Float.parseFloat(value));
			} else if ("Optionable".equals(key)) {
				stock.getFundamentalData().setOptionable("Yes".equals(value));
			} else if ("Recom".equals(key)) {
				stock.getFundamentalData().setRecommendationRate(
						Float.parseFloat(value));
			} else if ("PEG".equals(key)) {
				stock.getFundamentalData().setPeg(Float.parseFloat(value));
			} else if ("Debt/Eq".equals(key)) {
				stock.getFundamentalData().setDebtOverEquity(
						Float.parseFloat(value));
			} else if ("EPS (ttm)".equals(key)) {
				stock.getFundamentalData().setEps(Float.parseFloat(value));
			} else if ("EPS next Y".equals(key)) {
				stock.getFundamentalData().setEpsNextYear(
						Float.parseFloat(value));
			} else if ("EPS this Y".equals(key)) {
				stock.getFundamentalData()
						.setEpsThisYearChange(
								Float.parseFloat(value.substring(0,
										value.length() - 1))
										* Constants.ONE_PERCENT);
			} else if ("EPS next Y%".equals(key)) {
				stock.getFundamentalData()
						.setEpsNextYearChange(
								Float.parseFloat(value.substring(0,
										value.length() - 1))
										* Constants.ONE_PERCENT);
			} else if ("EPS next 5Y".equals(key)) {
				stock.getFundamentalData()
						.setEpsNext5YearChange(
								Float.parseFloat(value.substring(0,
										value.length() - 1))
										* Constants.ONE_PERCENT);
			} else if ("EPS past 5Y".equals(key)) {
				stock.getFundamentalData()
						.setEpsPast5YearChange(
								Float.parseFloat(value.substring(0,
										value.length() - 1))
										* Constants.ONE_PERCENT);
			} else if ("Sales past 5Y".equals(key)) {
				stock.getFundamentalData()
						.setSalesPast5YearChange(
								Float.parseFloat(value.substring(0,
										value.length() - 1))
										* Constants.ONE_PERCENT);
			} else if ("EPS Q/Q".equals(key)) {
				stock.getFundamentalData()
						.setEpsQQ(
								Float.parseFloat(value.substring(0,
										value.length() - 1))
										* Constants.ONE_PERCENT);
			} else if ("Earnings".equals(key)) {
				stock.getFundamentalData().setEarningDate(value);
				;
			} else if ("Insider Own".equals(key)) {
				stock.getFundamentalData()
						.setInsiderOwn(
								Float.parseFloat(value.substring(0,
										value.length() - 1))
										* Constants.ONE_PERCENT);
			} else if ("Inst Own".equals(key)) {
				stock.getFundamentalData()
						.setInstitutionOwn(
								Float.parseFloat(value.substring(0,
										value.length() - 1))
										* Constants.ONE_PERCENT);
			}

			else if ("ROA".equals(key)) {
				stock.getFundamentalData()
						.setRoa(Float.parseFloat(value.substring(0,
								value.length() - 1))
								* Constants.ONE_PERCENT);
			} else if ("ROE".equals(key)) {
				stock.getFundamentalData()
						.setRoe(Float.parseFloat(value.substring(0,
								value.length() - 1))
								* Constants.ONE_PERCENT);
			} else if ("ROI".equals(key)) {
				stock.getFundamentalData()
						.setRoi(Float.parseFloat(value.substring(0,
								value.length() - 1))
								* Constants.ONE_PERCENT);
			} else if ("Gross Margin".equals(key)) {
				stock.getFundamentalData()
						.setGrossMargin(
								Float.parseFloat(value.substring(0,
										value.length() - 1))
										* Constants.ONE_PERCENT);
			} else if ("Oper. Margin".equals(key)) {
				stock.getFundamentalData()
						.setOperationMargin(
								Float.parseFloat(value.substring(0,
										value.length() - 1))
										* Constants.ONE_PERCENT);
			} else if ("Profit Margin".equals(key)) {
				stock.getFundamentalData()
						.setProfitMargin(
								Float.parseFloat(value.substring(0,
										value.length() - 1))
										* Constants.ONE_PERCENT);
			} else if ("Short Float".equals(key)) {
				stock.getFundamentalData()
						.setShortFloatPercent(
								Float.parseFloat(value.substring(0,
										value.length() - 1))
										* Constants.ONE_PERCENT);
			} else if ("Short Ratio".equals(key)) {
				stock.getFundamentalData().setShortRatio(
						Float.parseFloat(value) * Constants.ONE_PERCENT);
			} else if ("Avg Volume".equals(key)) {
				long s = 0;
				if (value.endsWith("B")) {
					s = (long) (Double.parseDouble(value.substring(0,
							value.length() - 1)) * Constants.ONE_BILLION);
				} else if (value.endsWith("M")) {
					s = (long) (Double.parseDouble(value.substring(0,
							value.length() - 1)) * Constants.ONE_MILLION);
				}

				stock.getFundamentalData().setAvgVol(s);
			} else if ("Beta".equals(key)) {
				stock.getFundamentalData().setBeta(Float.parseFloat(value));
			}
		}
	}

	private static void fetchAnalystOpinions(Document doc, Stock stock) throws ParseException {
		Elements tds = doc.select("td.fullview-ratings-inner");
		for(Element td : tds)
		{
			AnalystOpinion opinion= new AnalystOpinion();
			
			Element table = td.getElementsByTag("table").first();
			Elements els = table.getElementsByTag("td");
			
			opinion.setDate(new SimpleDateFormat(DATE_FORMAT).parse(els.get(0).text()));
			opinion.setAction(els.get(1).text());
			opinion.setFirm(els.get(2).text());
			opinion.setRate(els.get(3).text());
			opinion.setPriceRange(els.get(4).text());
            stock.getFundamentalData().getOpinions().add(opinion);
		}
	}

	public static void fetch(Stock stock) throws Exception {
		String url = PropertyManager.getInstance().getProperty(
				PropertyManager.FINVIZ_STATS)
				+ stock.getTicker();
		logger.debug("url=" + url);
		Document doc = Jsoup.connect(url).get();
		fetchSectorAndIndustry(doc, stock);
		if (stock.getFundamentalData().getStockType() == StockType.STOCK) {
			fetchSnapshot(doc, stock);
			fetchAnalystOpinions(doc, stock);
		}
	}

	public static void main(String[] args) throws Exception {
		Stock stock = new Stock();
		stock.setTicker("T");
		stock.getFundamentalData().setStockType(StockType.STOCK);
		fetch(stock);
		System.out.println(stock);
	}
}
