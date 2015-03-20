package trading.server;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class WeekdayJob implements Job {

	private static final Logger logger = Logger.getLogger(WeekdayJob.class);
	
	public void execute(JobExecutionContext context) throws JobExecutionException {

		logger.debug("Weekday job");

	}

}