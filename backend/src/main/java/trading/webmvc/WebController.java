package trading.webmvc;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import trading.domain.Stock;
import trading.service.TradingDataService;

@RestController
public class WebController {

	@Autowired
	private TradingDataService dataService;

	@RequestMapping(value = "help", method = RequestMethod.GET)
	public String help() {
		StringBuilder sb = new StringBuilder();
		sb.append("category");
		sb.append("\n");
		sb.append("fetch/{ticker}");
		sb.append("\n");
		sb.append("reload/{ticker}");
		sb.append("\n");
		return sb.toString();
	}

	@RequestMapping(value = "category", method = RequestMethod.GET)
	public Map<String, Map<String, Set<String>>> getCategories() {
		return dataService.getCategories();
	}
	
	@RequestMapping(value = "fetch/{ticker}", method = RequestMethod.GET)
	public Stock getStock(@PathVariable("ticker") String ticker) {
		return dataService.getStock(ticker);
	}
	
	@RequestMapping(value = "reload/{ticker}", method = RequestMethod.GET)
	public Stock reload(@PathVariable("ticker") String ticker) throws Exception {
		return dataService.reload(ticker);
	}
}
