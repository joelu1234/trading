package trading.indicator;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import trading.domain.Quote;
import trading.domain.Stock;

public class BollingerBandsTest {

	private Stock stock;

	private float[] closes = { 90.70f, 92.90f, 92.98f, 91.80f, 92.66f, 92.68f, 92.30f, 92.77f, 92.54f, 92.95f, 93.20f, 91.07f, 89.83f, 89.74f, 90.40f, 90.74f, 88.02f, 88.09f, 88.84f, 90.78f, 90.54f, 91.39f, 90.65f };

	private float[] lowerBands = { 86.12f, 86.14f, 85.87f, 85.85f, 85.70f, 85.65f, 85.59f, 85.56f, 85.60f, 85.98f, 86.27f, 86.82f, 86.87f, 86.91f, 87.12f, 87.63f, 87.83f, 87.56f, 87.76f, 87.97f, 87.95f, 87.96f, 87.95f };

	private float[] upperBands = { 91.29f, 91.95f, 92.61f, 92.93f, 93.31f, 93.73f, 93.90f, 94.27f, 94.57f, 94.79f, 95.04f, 94.91f, 94.90f, 94.90f, 94.86f, 94.67f, 94.56f, 94.68f, 94.58f, 94.53f, 94.53f, 94.37f, 94.15f };

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
	public void testCalcBands() {

		Quote[] quotes = stock.getQuotes().toArray(new Quote[0]);

		BollingerBands.calcBands(quotes);

		String str1 = String.format("%.2f", stock.getQuotes().get(22).getLowerBB20_2());
		String str2 = String.format("%.2f", lowerBands[22]);

		assertEquals(str1, str2);

		str1 = String.format("%.2f", stock.getQuotes().get(22).getUpperBB20_2());
		str2 = String.format("%.2f", upperBands[22]);

		assertEquals(str1, str2);
	}
}
