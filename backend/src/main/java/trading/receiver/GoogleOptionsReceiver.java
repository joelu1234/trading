package trading.receiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonParser;

import trading.domain.OptionData;
import trading.domain.Stock;
import trading.util.Utils;

public class GoogleOptionsReceiver {

	private static Logger logger = Logger.getLogger(GoogleOptionsReceiver.class);

	@SuppressWarnings("unchecked")
	public void fetch(Stock stock, Date oeDate, String url) throws Exception {

			// HttpGet request = new
			// HttpGet("https://www.google.com/finance/option_chain?cid=22144&expd=20&expm=2&expy=2015&output=json");
			Calendar cal = Calendar.getInstance();
			cal.setTime(oeDate);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			int day = cal.get(Calendar.DAY_OF_MONTH);
			url = String.format(url, stock.getTicker().toUpperCase(), day, month, year);
			logger.debug("url=" + url);
			String response = Utils.getStringFromURL(url);
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
			Map<String, Object> rawResult = mapper.readValue(response.getBytes(), Map.class);
			Map<String, Integer> expiry = (Map<String, Integer>) rawResult.get("expiry");
			if (year != expiry.get("y") || month != expiry.get("m") || day != expiry.get("d")) {
				throw new Exception(String.format("Request was %s, but response is %s", url, expiry));
			}
			stock.setOptions(parseOptionChain(rawResult));

	}

	@SuppressWarnings("unchecked")
	private List<OptionData> parseOptionChain(Map<String, Object> raw) {
		List<OptionData> results = new ArrayList<OptionData>();
		List<Map<String, String>> puts = (List<Map<String, String>>) raw.get("puts");
		if (puts != null && puts.size() > 0)
			results.addAll(parseCallsPuts(puts, OptionData.Type.PUT));
		List<Map<String, String>> calls = (List<Map<String, String>>) raw.get("calls");
		if (calls != null && calls.size() > 0)
			results.addAll(parseCallsPuts(calls, OptionData.Type.CALL));
		return results;
	}

	private List<OptionData> parseCallsPuts(List<Map<String, String>> data, OptionData.Type type) {
		List<OptionData> results = new ArrayList<OptionData>();
		for (Map<String, String> map : data) {
			OptionData o = parseOptionData(map);
			o.setType(type);
			results.add(o);
		}
		return results;
	}

	private OptionData parseOptionData(Map<String, String> map) {
		OptionData o = new OptionData();
		o.setIv(0);
		o.setLast(parseFloat(map.get("p")));
		o.setOi(parseLong(map.get("oi")));
		o.setStrike(parseFloat(map.get("strike")));
		o.setVolume(parseLong(map.get("vol")));
		return o;
	}

	private long parseLong(String s) {
		if (StringUtils.isEmpty(s) || s.equals("-"))
			return 0;
		return Long.valueOf(s);
	}

	private float parseFloat(String s) {
		if (StringUtils.isEmpty(s) || s.equals("-"))
			return 0;
		return Float.valueOf(s);
	}

}
