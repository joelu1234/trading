package trading.indicator;

import trading.domain.Quote;

public final class BollingerBands {

	private static final int DAYS = 20;
	private static final int DV = 2;

	private BollingerBands() {
	}
	
	public static void calcBands(Quote[] quotes) {
		MovingAverage.calcSimpleMA(quotes, DAYS);
		int end = quotes.length - 1;
		int start = DAYS - 1;

		int pos = start;
		for (int i = end; i >= start; i--) {
			if (quotes[i].getLowerBB20_2() != null) {
				pos = i + 1;
				break;
			}
		}
		if (pos > end) {
			return;
		}

		for (int i = pos; i <= end; i++) {
			float ma = quotes[i].getSimpleMA(DAYS);
			float sum = 0.0f;
			for (int j = i - start; j <= i; j++) {
				float diff = (quotes[j].getClose() - ma);
				sum += diff * diff;
			}
			float dev = (float) (Math.sqrt(sum / DAYS) * DV);
			float lowerBB = ma - dev;
			quotes[i].setLowerBB20_2(lowerBB < 0 ? 0 : lowerBB);
			quotes[i].setUpperBB20_2(ma + dev);
		}
	}

}
