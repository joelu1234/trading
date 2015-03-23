package trading.service;

import org.apache.log4j.Logger;

public class ScheduleServiceImpl implements ScheduleService {

	private static final Logger logger = Logger.getLogger(ScheduleServiceImpl.class);

	private TradingDataService receiveService;

	public TradingDataService getReceiveService() {
		return receiveService;
	}

	public void setReceiveService(TradingDataService receiveService) {
		this.receiveService = receiveService;
	}

	public void weekdayRun() {
		// TODO Auto-generated method stub

	}

	public void weekendRun() {
		// TODO Auto-generated method stub

	}

}
