package trading.service;

import org.apache.log4j.Logger;

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

	}

	public void weekendRun() {
		// TODO Auto-generated method stub
		logger.debug("Inside weekendRun()");
	}
	

}
