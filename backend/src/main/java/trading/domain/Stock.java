package trading.domain;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import trading.util.Constants;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Stock {
	private String ticker;
	private FundamentalData fundamentalData = new FundamentalData();
	private List<OptionData> options = new ArrayList<OptionData>();
	private List<Quote> quotes = new ArrayList<Quote>();

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public FundamentalData getFundamentalData() {
		return fundamentalData;
	}

	public void setFundamentalData(FundamentalData fundamentalData) {
		this.fundamentalData = fundamentalData;
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

	private static ObjectMapper getObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat(Constants.DATE_FORMAT));
		return mapper;
	}

	@Override
	public String toString() {
		try {
			return getObjectMapper().writeValueAsString(this);
		} catch (Throwable th) {
			th.printStackTrace();
			return null;
		}
	}

	public void toJSONFile(File file) throws JsonGenerationException, JsonMappingException, IOException {
		getObjectMapper().writeValue(file, this);
	}

	public static Stock fromJSONFile(File file) throws JsonParseException, JsonMappingException, IOException {
		return getObjectMapper().readValue(file, Stock.class);
	}

}
