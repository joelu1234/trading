package trading.webmvc;

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

	@RequestMapping(value = "/status", method = RequestMethod.GET)
	public String status() {
		return "Hello World!";
	}

	@RequestMapping(value = "/{ticker}", method = RequestMethod.GET)
	public Stock getStock(@PathVariable("tocket") String ticker) {
		return dataService.getStock(ticker);
	}
}
