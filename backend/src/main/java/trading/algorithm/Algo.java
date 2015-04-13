package trading.algorithm;

import java.util.concurrent.Callable;

import trading.service.TradingDataService;

public abstract class Algo implements Callable<String> {

	protected TradingDataService dataService;

	public void setDataService(TradingDataService dataService) {
		this.dataService = dataService;
	}
}
