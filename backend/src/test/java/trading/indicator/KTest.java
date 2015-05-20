package trading.indicator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import trading.domain.Quote;
import trading.domain.Stock;

public class KTest {

	private Stock stock;

	private float[] highs = { 127.01f, 127.62f, 126.59f, 127.35f, 128.17f, 128.43f, 127.37f, 126.42f, 126.90f, 126.85f, 125.65f, 125.72f, 127.16f, 127.72f, 127.69f, 128.22f, 128.27f, 128.09f, 128.27f, 127.74f, 128.77f, 129.29f, 130.06f, 129.12f, 129.29f, 128.47f, 128.09f, 128.65f, 129.14f};
	private float[] lows = { 125.36f, 126.16f, 124.93f, 126.09f, 126.82f, 126.48f, 126.03f, 124.83f, 126.39f, 125.72f, 124.56f, 124.57f, 125.07f, 126.86f, 126.63f, 126.80f, 126.71f, 126.80f, 126.13f, 125.92f, 126.99f, 127.81f, 128.47f, 128.06f, 127.61f, 127.60f, 127.00f, 126.90f, 127.49f};
	private float[] closes = { 125.36f, 126.16f, 124.93f, 126.09f, 126.82f, 126.48f, 126.03f, 124.83f, 126.39f, 125.72f, 124.56f, 124.57f, 125.07f, 127.29f, 127.18f, 128.01f, 127.11f, 127.73f, 127.06f, 127.33f, 128.71f, 127.87f, 128.58f, 128.60f, 127.93f, 128.11f, 127.60f, 127.60f, 128.69f};
	private float[] k14s = { 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 70.54f, 67.70f, 89.15f, 65.89f, 81.91f, 64.60f, 74.66f, 98.57f, 69.98f, 73.09f, 73.45f, 61.20f, 60.92f, 40.58f, 40.58f, 66.91f, 56.76f };

	@Before
	public void setup() {
		stock = new Stock();
		Date date = DateUtils.truncate(new Date(), Calendar.DATE);

		for (int i = 0; i < closes.length; i++) {
			Quote q = new Quote();
			q.setDate(DateUtils.addDays(date, i));
			q.setClose(closes[i]);
			q.setLow(lows[i]);
			q.setHigh(highs[i]);
			stock.getQuotes().add(q);
		}

	}

	@Test
	public void testCalcK14s() {
		K.calc(stock.getQuotes().toArray(new Quote[0]));
		String str1 = String.format("%.2f", stock.getQuotes().get(28).getPercentK());
		String str2 = String.format("%.2f", k14s[28]);
		assertEquals(str1, str2);
		
		
		Quote q = new Quote();
		q.setDate(new Date());
		q.setClose(128.27f);
		q.setLow(127.40f );
		q.setHigh(128.64f);
		stock.getQuotes().add(q);
		K.calc(stock.getQuotes().toArray(new Quote[0]));
		str1 = String.format("%.2f", stock.getQuotes().get(29).getPercentK());
		str2 = String.format("%.2f", k14s[29]);
		assertEquals(str1, str2);		
		
		assertTrue(stock.toString() != null);
	}
}
