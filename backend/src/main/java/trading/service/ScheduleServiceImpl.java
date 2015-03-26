package trading.service;

import org.apache.log4j.Logger;

import trading.util.Constants;

public class ScheduleServiceImpl implements ScheduleService {

	private static final Logger logger = Logger.getLogger(ScheduleServiceImpl.class);

	private TradingDataService dataService;

	public TradingDataService getDataService() {
		return dataService;
	}

	public void setDataService(TradingDataService dataService) {
		this.dataService = dataService;
	}

	public void weekdayRun() {
		// TODO Auto-generated method stub
		logger.debug("Inside weekdayRun()");
		try {
			dataService.loadStocks(Constants.LOAD_TYPE_QUOTES);
		} catch (Throwable th) {
			logger.error("Weekday Stock update failure", th);
		}
	}

	public void weekendRun() {
		// TODO Auto-generated method stub
		logger.debug("Inside weekendRun()");
		try {
			dataService.loadStocks(Constants.LOAD_TYPE_STATS);
		} catch (Throwable th) {
			logger.error("Weekday Stock update failure", th);
		}
	}

}
