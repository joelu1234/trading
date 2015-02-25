package trading.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import trading.domain.Quote;
import trading.domain.Stock;
import trading.util.PropertyManager;

public class FileDaoImplTest {

	@Test
	public void test() {
		PropertyManager mockPropertyManager = mock(PropertyManager.class);
		when(PropertyManager.getInstance()).thenReturn(mockPropertyManager);
		
		Stock stock1 = new Stock();
		stock1.setTicker("T");
		stock1.getFundamentalData().setName("ATT");
        Quote q1= new Quote();
        q1.setDate(new Date());
        q1.setHigh(4.0f);
        q1.setClose(3.0f);
        q1.setOpen(2.0f);
        q1.setLow(1.0f);
        q1.setVolume(1000L);
		stock1.getQuotes().add(q1);
		
				
	}
}
