package trading.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import trading.service.TradingDataService;

public final class InitListener implements ServletContextListener {

	private ServletContext context = null;

	private static final Logger logger = Logger.getLogger(InitListener.class);

	public InitListener() {
	}

	public void contextDestroyed(ServletContextEvent event) {
		this.context = null;
	}

	// This method is invoked when the Web Application
	// is ready to service requests
	public void contextInitialized(ServletContextEvent event) {
		logger.info("inside contextInitialized()");
		try {
			this.context = event.getServletContext();
			ApplicationContext springContext = WebApplicationContextUtils.getRequiredWebApplicationContext(this.context);
			TradingDataService dataService = (TradingDataService) springContext.getBean("dataService");
			logger.info("Load in stocks");
			dataService.loadStocks();
		} catch (Throwable e) {
			logger.error("Inside contextInitialized", e);
		}
	}
}