package trading.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import trading.domain.AlgoResult;
import trading.domain.TrendLine;
import trading.service.TradingDataService;

public abstract class Algo implements Callable<String> {

	protected TradingDataService dataService;

	public void setDataService(TradingDataService dataService) {
		this.dataService = dataService;
	}

	protected List<AlgoResult> getAlgoResults(String ticker, String algoName) {
		Map<String, List<AlgoResult>> map = this.dataService.getAlgoResults();
		List<AlgoResult> list = map.get(ticker);
		if (list == null) {
			return new ArrayList<AlgoResult>();
		}
		else{
			return list.stream().filter(p -> p.getAlgoName().equals(algoName)).collect(Collectors.toList());
		}
	}
	
	protected void addAlgoResult(AlgoResult result) {
		Map<String, List<AlgoResult>> map = this.dataService.getAlgoResults();
		List<AlgoResult> list = map.get(result.getTicker());
		if (list == null) {
			list = new ArrayList<AlgoResult>();
			map.put(result.getTicker(), list);
		}
		list.add(result);
	}
	
	protected List<TrendLine> getTrendLines(String ticker) {
		Map<String, List<TrendLine>> map = this.dataService.getTrendLines();
		List<TrendLine> list = map.get(ticker);
		if (list == null) {
			list = new ArrayList<TrendLine>();
		}
		return list;
	}
	
	protected void addTrendLine(TrendLine line) {
		Map<String, List<TrendLine>> map = this.dataService.getTrendLines();
		List<TrendLine> list = map.get(line.getTicker());
		if (list == null) {
			list = new ArrayList<TrendLine>();
			map.put(line.getTicker(), list);
		}
		list.add(line);
	}
	
}
