package trading.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.junit.Test;

import trading.domain.OptionData;
import trading.domain.Quote;
import trading.domain.Stock;
import trading.util.PropertyManager;

public class FileDaoImplTest {

	@Test
	public void test() throws Exception {
		PropertyManager mockPropertyManager = mock(PropertyManager.class);
		when(mockPropertyManager.getProperty(PropertyManager.FILE_STATS)).thenReturn("stats.json");
		when(mockPropertyManager.getProperty(PropertyManager.FILE_QUOTES)).thenReturn("quotes.json");
		when(mockPropertyManager.getProperty(PropertyManager.FILE_OPTIONS)).thenReturn("options.json");
		
		when(mockPropertyManager.getStatsFile()).thenReturn(new File("stats.json.zip"));
		when(mockPropertyManager.getQuoteFile()).thenReturn(new File("quotes.json.zip"));
		when(mockPropertyManager.getOptionFile()).thenReturn(new File("options.json.zip"));

		FileDaoImpl dao = new FileDaoImpl();
		dao.setPropertyManager(mockPropertyManager);

		Stock stock1 = new Stock();
		stock1.setTicker("T");
		stock1.getFundamentalData().setName("ATT");
		Quote q1 = new Quote();
		q1.setDate(new Date());
		q1.setHigh(4.0f);
		q1.setClose(3.0f);
		q1.setOpen(2.0f);
		q1.setLow(1.0f);
		q1.setVolume(1000L);
		stock1.getQuotes().add(q1);
		Quote q2 = new Quote();
		q2.setDate(new Date());
		q2.setHigh(8.0f);
		q2.setClose(7.0f);
		q2.setOpen(6.0f);
		q2.setLow(5.0f);
		q2.setVolume(2000L);
		stock1.getQuotes().add(q1);
		OptionData o1 = new OptionData();
		o1.setCallType(true);
		o1.setIv(0.01f);
		o1.setLast(0.02f);
		o1.setOi(10000L);
		o1.setStrike(0.03f);
		o1.setVolume(100000L);
		stock1.getOptions().add(o1);
		OptionData o2 = new OptionData();
		o2.setCallType(false);
		o2.setIv(0.01f);
		o2.setLast(0.02f);
		o2.setOi(10000L);
		o2.setStrike(0.03f);
		o2.setVolume(100000L);
		stock1.getOptions().add(o2);

		Stock stock2 = new Stock();
		stock2.setTicker("AAPL");
		stock2.getFundamentalData().setName("Apple");
		Quote q3 = new Quote();
		q3.setDate(new Date());
		q3.setHigh(4.0f);
		q3.setClose(3.0f);
		q3.setOpen(2.0f);
		q3.setLow(1.0f);
		q3.setVolume(1000L);
		stock2.getQuotes().add(q3);
		Quote q4 = new Quote();
		q4.setDate(new Date());
		q4.setHigh(8.0f);
		q4.setClose(7.0f);
		q4.setOpen(6.0f);
		q4.setLow(5.0f);
		q4.setVolume(2000L);
		stock2.getQuotes().add(q4);

		ArrayList<Stock> stocks = new ArrayList<Stock>();
		stocks.add(stock1);
		stocks.add(stock2);

		dao.saveStats(stocks);
		dao.saveQuotes(stocks);
		dao.saveOptions(stocks);

		Map<String, Stock> map = dao.loadStocks();
		assertEquals(map.size(), 2);
		Stock stock = map.get("T");
		assertNotNull(stock);
		assertEquals(stock.getFundamentalData().getName(), "ATT");
		assertEquals(stock.getQuotes().get(0).getVolume(), 1000L);
		assertTrue(stock.getOptions().get(0).isCallType());

		mockPropertyManager.getStatsFile().delete();
		mockPropertyManager.getQuoteFile().delete();
		mockPropertyManager.getOptionFile().delete();
	}
}
