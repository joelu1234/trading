package trading.indicator;

import java.util.List;

import trading.domain.Quote;

public final class RSI {
	private static final int DAYS = 5;

	private RSI() {
	}
	
	public static void calc(List<Quote> quotes) {
		calc(quotes.toArray(new Quote[0]));
	}

	public static void calc(Quote[] quotes) {
		calc(quotes, DAYS);
	}

	public static void calc(Quote[] quotes, int days) {
		int end = quotes.length - 1;
		int start = days;
        int dLess1=days-1;
		int pos = start;
		for (int i = end; i >= start; i--) {
			if (quotes[i].getRsi5ns() != null) {
				pos = i + 1;
				break;
			}
		}
		if (pos > end) {
			return;
		}

		float[] diff = new float[quotes.length];

		for (int i = pos - dLess1; i <= end; i++) {
			diff[i] = quotes[i].getClose() - quotes[i - 1].getClose();
		}

		Float ps = quotes[pos-1].getRsi5ps();
		Float ns = quotes[pos-1].getRsi5ns();
		
		for (int i = pos; i <= end; i++) {
			if(ps==null)
			{
				ps = 0.0f;
				ns=0.0f;
				for (int j =  i- dLess1; j <= i; j++) {
					if (diff[j] < 0) {
					ns += diff[j];
					} else if (diff[j] > 0) {
					ps += diff[j];
					}
				}
				ps = ps / days;
				ns = 0 - ns / days;
			}
			else
			{
				if (diff[i] < 0) {
					ns = (ns * dLess1 - diff[i]) / days;
					ps = (ps * dLess1) / days;
				} else if (diff[i] > 0) {
					ns = (ns * dLess1) / days;
					ps = (ps * dLess1 + diff[i]) / days;
				}
			}
			quotes[i].setRsi5ns(ns);
			quotes[i].setRsi5ps(ps);
		}
	}
}
