package trading.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import trading.domain.Stock;
import trading.domain.StockType;

public class Utils {

	private static Logger logger = Logger.getLogger(Utils.class);

	private Utils() {
	}

	public static byte[] getBytesFromURL(String url) throws Exception {
		InputStream in = new URL(url).openStream();
		try {
			return IOUtils.toByteArray(in);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public static String getStringFromURL(String url) throws Exception {
		InputStream in = new URL(url).openStream();
		try {
			return IOUtils.toString(in, "UTF-8");
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public static byte[] getBytesFromHttpsURL(String url) throws Exception {
		HttpsURLConnection conn = (HttpsURLConnection) (new URL(url)).openConnection();
		InputStream in = conn.getInputStream();
		try {
			return IOUtils.toByteArray(in);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public static String getStringFromHttpsURL(String url) throws Exception {
		HttpsURLConnection conn = (HttpsURLConnection) (new URL(url)).openConnection();
		InputStream in = conn.getInputStream();
		try {
			return IOUtils.toString(in, "UTF-8");
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public static byte[] getBytesFromFile(String filename) throws Exception {
		FileInputStream fis = new FileInputStream(filename);
		try {
			return IOUtils.toByteArray(fis);
		} finally {
			IOUtils.closeQuietly(fis);
		}

	}

	public static String getStringFromFile(String filename) throws Exception {
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(filename));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			return sb.toString();
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		}

	}

	public static Document fetchJsoupDoc(String url, int numRetry) throws Exception {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (Exception ex) {
			if (numRetry > 1) {
				logger.warn("Fetch error, retry", ex);
				Thread.sleep(2000);
				doc = fetchJsoupDoc(url, --numRetry);
			} else {
				throw (ex);
			}
		}
		return doc;
	}

	public static BufferedReader getReaderFromUrl(String url, int numRetry) throws Exception {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
		} catch (Exception ex) {
			if (numRetry > 1) {
				logger.warn("Fetch error, retry", ex);
				Thread.sleep(2000);
				in = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
			} else {
				throw (ex);
			}
		}
		return in;
	}

	public static void createVIXStats(Stock vix) {
		vix.getFundamentalData().setStockType(StockType.VIX);
		vix.getFundamentalData().setCountry("USA");
		vix.getFundamentalData().setName("VOLATILITY S&P 500");
		vix.getFundamentalData().setExchange("CBOE");
		vix.getFundamentalData().setSector("VOLATILITY");
		;
		vix.getFundamentalData().setIndustry("VOLATILITY");
		vix.getFundamentalData().setOptionable(true);
	}
}