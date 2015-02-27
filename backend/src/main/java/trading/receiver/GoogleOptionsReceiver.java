package trading.receiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import trading.domain.OptionData;
import trading.domain.Stock;
import trading.util.PropertyManager;
import trading.util.Utils;

public class GoogleOptionsReceiver implements Receiver {
	
	private static final Log LOGGER = LogFactory.getLog(GoogleOptionsReceiver.class);
	@SuppressWarnings("unchecked")
	public boolean fetch(Stock stock) {		
		CloseableHttpClient client = HttpClients.createDefault();
		try
		{
			//			HttpGet request = new HttpGet("https://www.google.com/finance/option_chain?cid=22144&expd=20&expm=2&expy=2015&output=json");
			Calendar cal = Calendar.getInstance();
			cal.setTime(Utils.getNextMonthlyOEDate());
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			int day = cal.get(Calendar.DAY_OF_MONTH);
			String uri = String.format(PropertyManager.getInstance().getProperty(PropertyManager.GOOGLE_OPTION), 
					stock.getTicker().toUpperCase(), day, month, year);
			HttpGet request = new HttpGet(uri);
			ResponseHandler<String> responseHdlr = new ResponseHandler<String>() {
				public String handleResponse(HttpResponse httpresponse) throws ClientProtocolException, IOException {
					HttpEntity entity = httpresponse.getEntity();
					return EntityUtils.toString(entity);
				}
			};
			String response = client.execute(request, responseHdlr);
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
			Map<String, Object> rawResult = mapper.readValue(response.getBytes(), Map.class);
			Map<String, Integer> expiry = (Map<String, Integer> ) rawResult.get("expiry");
			if(year != expiry.get("y") || month != expiry.get("m") || day != expiry.get("d")){
				throw new Exception(String.format("Request was %s, but response is %s", uri, expiry));
			}
			List<OptionData> options = parseOptionChain(rawResult);
			System.out.println(options);
		}
		catch(Exception e){
			LOGGER.error(e);
		}
		finally{
			try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}
	@SuppressWarnings("unchecked")
	private List<OptionData> parseOptionChain(Map<String, Object> raw){
		List<OptionData> results = new ArrayList<OptionData>();
		List<Map<String, String>> puts = (List<Map<String, String>>) raw.get("puts");
		if(puts != null && puts.size() > 0)
			results.addAll(parseCallsPuts(puts, false));
		List<Map<String, String>> calls = (List<Map<String, String>>) raw.get("calls");
		if(calls != null && calls.size() > 0)
			results.addAll(parseCallsPuts(calls, true));
		return results;
	}
	private List<OptionData> parseCallsPuts(List<Map<String, String>> data, boolean calltype){
		List<OptionData> results = new ArrayList<OptionData>();
		for(Map<String, String> map : data){
			OptionData o = parseOptionData(map);
			o.setCallType(calltype);
			results.add(o);
		}
		return results;
	}
	private OptionData parseOptionData(Map<String, String> map){
		OptionData o = new OptionData();
		o.setIv(0);
		o.setLast(parseFloat(map.get("p")));
		o.setOi(parseLong(map.get("oi")));
		o.setStrike(parseFloat(map.get("strike")));
		o.setVolume(parseLong(map.get("vol")));
		return o;
	}
	private long parseLong(String s) {
		if(StringUtils.isEmpty(s) || s.equals("-"))
			return 0;
		return Long.valueOf(s);
	}
	private float parseFloat(String s) {
		if(StringUtils.isEmpty(s) || s.equals("-"))
			return 0;
		return Float.valueOf(s);
	}
	

}
