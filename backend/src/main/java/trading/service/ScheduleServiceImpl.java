package trading.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import trading.algorithm.Algo;
import trading.util.Constants;

public class ScheduleServiceImpl implements ScheduleService {

	private static final Logger logger = Logger.getLogger(ScheduleServiceImpl.class);

	private static final int MAX_CONCURRENT_ALGO_RUN = 2;
			
	private TradingDataService dataService;

	public TradingDataService getDataService() {
		return dataService;
	}

	public void setDataService(TradingDataService dataService) {
		this.dataService = dataService;
	}

	public void weekdayRun() {
		logger.debug("Inside weekdayRun()");
		try {
			dataService.loadStocks(Constants.LOAD_TYPE_QUOTES);
		} catch (Throwable th) {
			logger.error("Weekday Stock update failure", th);
		}
		try {
			runAnalysis();
		} catch (Exception e) {
			logger.error("runAnalysis failure", e);
		}
	}

	public void weekendRun() {
		logger.debug("Inside weekendRun()");
		try {
			dataService.loadStocks(Constants.LOAD_TYPE_STATS);
		} catch (Throwable th) {
			logger.error("Weekday Stock update failure", th);
		}
	}

	private void runAnalysis() throws Exception{
		Set<String> names = dataService.getAlgoNames();
		List<Algo> algoList=new ArrayList<Algo>();
		for(String name : names){
			Algo algo = (Algo)Class.forName(name).newInstance();
			algo.setDataService(dataService);
			algoList.add(algo);
		}
		
		ExecutorService executor=Executors.newFixedThreadPool(MAX_CONCURRENT_ALGO_RUN);	
		List<Future<String>> futures = executor.invokeAll(algoList);
		for(Future<String> f : futures){
			logger.debug(f.get() + " thread completed");
		}
		
		
		executor.shutdown();
	}
}
