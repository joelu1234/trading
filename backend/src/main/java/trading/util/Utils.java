package trading.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;

public class Utils {
	private Utils() {
	}

	public static Date getNextMonthlyOEDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, 3);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		if (System.currentTimeMillis() >= cal.getTimeInMillis()) {
			cal.add(Calendar.MONTH, 1);
			cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
			cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, 3);
		}

		if (PropertyManager.getInstance().isHoliday(cal.getTime())) {
			cal.add(Calendar.DATE, -1);
		}
		return cal.getTime();
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

}