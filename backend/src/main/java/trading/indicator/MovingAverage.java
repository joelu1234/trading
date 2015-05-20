package trading.indicator;

import java.util.List;

import trading.domain.Quote;

public final class MovingAverage {
	private MovingAverage() {
	}

	public static void calc(List<Quote> quotes, int days) {
		calc(quotes.toArray(new Quote[0]), days);
	}
	public static void calc(Quote[] quotes, int days) {
		int end = quotes.length - 1;
		int start = days - 1;
		
	    float smoothConst= 2.0f/(1+days); 

		int pos = start;
		float total = 0.0f;
		for (int i = end; i >= start; i--) {
			if (quotes[i].getSimpleMA(days) != null) {
				pos = i + 1;
				break;
			}
		}
		if (pos > end) {
			return;
		}
		for (int i = 1; i < days; i++) {
			total += quotes[pos - i].getClose();
		}
		
		Float ema=quotes[pos-1].getExpMA(days);
		for (int i = pos; i <= end; i++) {
			float close = quotes[i].getClose();
			total += close;
			float sma=total / days;
			quotes[i].setSimpleMA(days, sma);
			if(ema==null){
				ema=sma;
				quotes[i].setExpMA(days, sma);
			}
			else{
				float newEma = smoothConst*(close-ema)+ema;
				quotes[i].setExpMA(days, newEma);
				ema=newEma;
			}
			total -= quotes[i - start].getClose();
		}
	}
	
}
