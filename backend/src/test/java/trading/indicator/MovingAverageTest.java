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

public class MovingAverageTest {

	private Stock stock;

	@Before
	public void setup() {
		stock = new Stock();
		Date date = DateUtils.truncate(new Date(), Calendar.DATE);

		for (int i = 0; i < 49; i++) {
			Quote q = new Quote();
			q.setDate(DateUtils.addDays(date, i - 49));
			q.setClose(1.0f + i);
			stock.getQuotes().add(q);
		}

	}

	@Test
	public void testCalcMA() {

		MovingAverage.calcSimpleMA(stock.getQuotes().toArray(new Quote[0]), 10);

		assertEquals(stock.getQuotes().get(9).getSimpleMA(10), new Float(5.5f)); // 1+2+..+10
		assertEquals(stock.getQuotes().get(48).getSimpleMA(10), new Float(44.5f)); // 40+..+49

		Quote q = new Quote();
		q.setDate(DateUtils.truncate(new Date(), Calendar.DATE));
		q.setClose(50.0f);
		stock.getQuotes().add(q);

		MovingAverage.calcSimpleMA(stock.getQuotes().toArray(new Quote[0]), 10);

		assertEquals(stock.getQuotes().get(9).getSimpleMA(10), new Float(5.5f)); // 1+2+..+10
		assertEquals(stock.getQuotes().get(48).getSimpleMA(10), new Float(44.5f)); // 40+..+49
		assertEquals(stock.getQuotes().get(49).getSimpleMA(10), new Float(45.5f)); // 41+..+50

		MovingAverage.calcSimpleMA(stock.getQuotes().toArray(new Quote[0]), 10);

		assertEquals(stock.getQuotes().get(9).getSimpleMA(10), new Float(5.5f)); // 1+2+..+10
		assertEquals(stock.getQuotes().get(48).getSimpleMA(10), new Float(44.5f)); // 40+..+49
		assertEquals(stock.getQuotes().get(49).getSimpleMA(10), new Float(45.5f)); // 41+..+50
		
		assertTrue(stock.toString() != null);
	}
}
