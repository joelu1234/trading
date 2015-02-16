package trading.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.io.IOUtils;

public class TradingIOUtils {
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
		HttpsURLConnection conn = (HttpsURLConnection) (new URL(url))
				.openConnection();
		InputStream in = conn.getInputStream();
		try {
			return IOUtils.toByteArray(in);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public static String getStringFromHttpsURL(String url) throws Exception {
		HttpsURLConnection conn = (HttpsURLConnection) (new URL(url))
				.openConnection();
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
}