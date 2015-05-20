package trading.indicator;

import java.util.List;

import trading.domain.Quote;

public final class K {
	private static final int DAYS = 14;

	private K() {
	}

	public static void calc(List<Quote> quotes) {
		calc(quotes.toArray(new Quote[0]), DAYS);
	}
	
	public static void calc(Quote[] quotes) {
		calc(quotes, DAYS);
	}

	public static void calc(Quote[] quotes, int days) {
		int end = quotes.length - 1;
		int start = days - 1;
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

		for (int i = pos; i <= end; i++) {
			float high = 0;
			float low = Float.MAX_VALUE;
			for (int j = i - start; j <= i; j++) {
				if (quotes[j].getHigh() > high)
					high = quotes[j].getHigh();
				if (quotes[j].getLow() < low)
					low = quotes[j].getLow();
			}
			quotes[i].setPercentK((high > low) ? (quotes[i].getClose() - low) * 100 / (high - low) : 0);
		}
	}
}
