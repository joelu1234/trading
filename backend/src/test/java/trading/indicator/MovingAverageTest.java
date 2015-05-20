package trading.indicator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import trading.domain.Quote;
import trading.domain.Stock;

public class MovingAverageTest {

	private Stock stock;
	
	private float[] closes = { 			
			22.27f,
			22.19f,
			22.08f,
			22.17f,
			22.18f,
			22.13f,
			22.23f,
			22.43f,
			22.24f,
			22.29f,
			22.15f,
			22.39f,
			22.38f,
			22.61f,
			23.36f,
			24.05f,
			23.75f,
			23.83f,
			23.95f,
			23.63f,
			23.82f,
			23.87f,
			23.65f,
			23.19f,
			23.10f,
			23.33f,
			22.68f,
			23.10f,
			22.40f,
			22.17f};

	private float[] smas = { 			
			0.00f,
			0.00f,
			0.00f,
			0.00f,
			0.00f,
			0.00f,
			0.00f,
			0.00f,
			0.00f,
			22.22f,
			22.21f,
			22.23f,
			22.26f,
			22.31f,
			22.42f,
			22.61f,
			22.77f,
			22.91f,
			23.08f,
			23.21f,
			23.38f,
			23.53f,
			23.65f,
			23.71f,
			23.69f,
			23.61f,
			23.51f,
			23.43f,
			23.28f,
			23.13f};

	private float[] emas = { 
			0.00f,
			0.00f,
			0.00f,
			0.00f,
			0.00f,
			0.00f,
			0.00f,
			0.00f,
			0.00f,
			22.22f,
			22.21f,
			22.24f,
			22.27f,
			22.33f,
			22.52f,
			22.80f,
			22.97f,
			23.13f,
			23.28f,
			23.34f,
			23.43f,
			23.51f,
			23.54f,
			23.47f,
			23.40f,
			23.39f,
			23.26f,
			23.23f,
			23.08f,
			22.92f};

	

	@Before
	public void setup() {
		stock = new Stock();
		Date date = DateUtils.truncate(new Date(), Calendar.DATE);

		for (int i = 0; i < 29; i++) {
			Quote q = new Quote();
			q.setDate(DateUtils.addDays(date, i - 49));
			q.setClose(closes[i]);
			stock.getQuotes().add(q);
		}

	}

	@Test
	public void testCalcMA() {

		DecimalFormat fmt = new DecimalFormat("##.##");
		
		MovingAverage.calc(stock.getQuotes().toArray(new Quote[0]), 10);

		assertEquals(fmt.format(stock.getQuotes().get(28).getSimpleMA(10)), fmt.format(smas[28])); 
		assertEquals(fmt.format(stock.getQuotes().get(28).getExpMA(10)), fmt.format(emas[28])); 

		
		Quote q = new Quote();
		q.setDate(DateUtils.truncate(new Date(), Calendar.DATE));
		q.setClose(closes[29]);
		stock.getQuotes().add(q);

		MovingAverage.calc(stock.getQuotes().toArray(new Quote[0]), 10);

		assertEquals(fmt.format(stock.getQuotes().get(28).getSimpleMA(10)), fmt.format(smas[28])); 
		assertEquals(fmt.format(stock.getQuotes().get(28).getExpMA(10)), fmt.format(emas[28])); 
		assertEquals(fmt.format(stock.getQuotes().get(29).getSimpleMA(10)), fmt.format(smas[29]));
		assertEquals(fmt.format(stock.getQuotes().get(29).getExpMA(10)), fmt.format(emas[29])); 
		
		assertTrue(stock.toString() != null);
	}
}
