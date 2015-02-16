package trading.domain;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class Stock {
	private String name;
	private String ticker;
	private String sector;
	private String industry;
	private StockType stockType;
	private boolean optionable;
	private List<String> indices=new ArrayList<String>();
	private FundamentalData FundamentalData =new FundamentalData();
	private List<OptionData> options=new  ArrayList<OptionData>();
	private List<Quote> quotes = new ArrayList<Quote>() ;

	public boolean isOptionable() {
		return optionable;
	}

	public void setOptionable(boolean optionable) {
		this.optionable = optionable;
	}
	
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

	public List<String> getIndices() {
		return indices;
	}

	public void setIndices(List<String> indices) {
		this.indices = indices;
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

	@Override
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (Throwable th) {
			th.printStackTrace();
			return null;
		}
	}

	public void toJSONFile(File file) throws JsonGenerationException,
			JsonMappingException, IOException {
		new ObjectMapper().writeValue(file, this);
	}

	public static Stock fromJSONFile(File file) throws JsonParseException,
			JsonMappingException, IOException {
		return new ObjectMapper().readValue(file, Stock.class);
	}

}
