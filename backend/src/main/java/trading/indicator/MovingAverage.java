package trading.indicator;

import trading.domain.Quote;

public final class MovingAverage {
	private MovingAverage() {
	}

	public static void calcSimpleMA(Quote[] quotes, int days) {
		int end = quotes.length - 1;
		int start = days - 1;

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
		for (int i = pos; i <= end; i++) {
			total += quotes[i].getClose();
			quotes[i].setSimpleMA(days, total / days);
			total -= quotes[i - start].getClose();
		}
	}
}
