package trading.domain;

import java.util.List;

public class Stock {
	private String name;
	private String ticker;
	private String sector;
	private String industry;
	private String index;
	private StockType stockType;
	private FundamentalData FundamentalData;
	private List<OptionData> options;
	private List<Quote> quotes;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public StockType getStockType() {
		return stockType;
	}

	public void setStockType(StockType stockType) {
		this.stockType = stockType;
	}

	public FundamentalData getFundamentalData() {
		return FundamentalData;
	}

	public void setFundamentalData(FundamentalData fundamentalData) {
		FundamentalData = fundamentalData;
	}

	public List<OptionData> getOptions() {
		return options;
	}

	public void setOptions(List<OptionData> options) {
		this.options = options;
	}

	public List<Quote> getQuotes() {
		return quotes;
	}

	public void setQuotes(List<Quote> quotes) {
		this.quotes = quotes;
	}
}
