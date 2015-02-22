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

public class RSITest {

	private Stock stock;

	private float[] closes = { 44.34f, 44.09f, 44.15f, 43.61f, 44.33f, 44.83f, 45.10f, 45.42f, 45.84f, 46.08f, 45.89f, 46.03f, 45.61f, 46.28f, 46.28f, 46.00f, 46.03f, 46.41f, 46.22f, 45.64f, 46.21f, 46.25f, 45.71f, 46.45f, 45.78f, 45.35f, 44.03f, 44.18f, 44.22f, 44.57f, 43.42f, 42.66f };

	private float[] rsi14s = { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 70.46f, 66.25f, 66.48f, 69.35f, 66.29f, 57.92f, 62.88f, 63.21f, 56.01f, 62.34f, 54.67f, 50.39f, 40.02f, 41.49f, 41.90f, 45.50f, 37.32f, 33.09f, 37.79f };

	@Before
	public void setup() {
		stock = new Stock();
		Date date = DateUtils.truncate(new Date(), Calendar.DATE);

		for (int i = 0; i < closes.length; i++) {
			Quote q = new Quote();
			q.setDate(DateUtils.addDays(date, i));
			q.setClose(closes[i]);
			stock.getQuotes().add(q);
		}

	}

	@Test
	public void testCalcRSI14s() {
		RSI.calcRSI(stock.getQuotes().toArray(new Quote[0]), 14);

		String str1 = String.format("%.2f", stock.getQuotes().get(31).getRsi5());
		String str2 = String.format("%.2f", rsi14s[31]);
		assertEquals(str1, str2);

		Quote q = new Quote();
		q.setDate(new Date());
		q.setClose(43.13f);
		stock.getQuotes().add(q);

		RSI.calcRSI(stock.getQuotes().toArray(new Quote[0]), 14);

		str1 = String.format("%.2f", stock.getQuotes().get(32).getRsi5());
		str2 = String.format("%.2f", rsi14s[32]);
		assertEquals(str1, str2);

		assertTrue(stock.toString() != null);
	}
}
