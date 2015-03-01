package trading.service;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class WeekendJob implements Job {

	private static final Logger logger = Logger.getLogger(WeekendJob.class);

	public void execute(JobExecutionContext context) throws JobExecutionException {

		logger.debug("Weekend job");

	}

}