package trading.domain;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonIgnore;

public class Quote implements Comparable<Quote> {
	public static final String DATE_FORMAT = "yyyyMMdd";
	private Date date;
	private float open;
	private float close;
	private float low;
	private float high;
	private long volume;

	private Map<Integer, Float> simpleMaMap = new HashMap<Integer, Float>();
	private Float rsi5ns;
	private Float rsi5ps;
	private Float percentK;
	private Float lowerBB20_2;
	private Float upperBB20_2;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public float getOpen() {
		return open;
	}

	public void setOpen(float open) {
		this.open = open;
	}

	public float getClose() {
		return close;
	}

	public void setClose(float close) {
		this.close = close;
	}

	public float getLow() {
		return low;
	}

	public void setLow(float low) {
		this.low = low;
	}

	public float getHigh() {
		return high;
	}

	public void setHigh(float high) {
		this.high = high;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	public Map<Integer, Float> getSimpleMaMap() {
		return simpleMaMap;
	}

	public void setSimpleMaMap(Map<Integer, Float> simpleMaMap) {
		this.simpleMaMap = simpleMaMap;
	}

	@JsonIgnore
	public Float getSimpleMA(int days) {
		return simpleMaMap.get(days);
	}

	@JsonIgnore
	public void setSimpleMA(int days, Float ma) {
		simpleMaMap.put(days, ma);
	}

	public Float getRsi5ns() {
		return rsi5ns;
	}

	public void setRsi5ns(Float rsi5ns) {
		this.rsi5ns = rsi5ns;
	}

	public Float getRsi5ps() {
		return rsi5ps;
	}

	public void setRsi5ps(Float rsi5ps) {
		this.rsi5ps = rsi5ps;
	}

	public Float getPercentK() {
		return percentK;
	}

	public void setPercentK(Float percentK) {
		this.percentK = percentK;
	}

	public Float getLowerBB20_2() {
		return lowerBB20_2;
	}

	public void setLowerBB20_2(Float lowerBB20_2) {
		this.lowerBB20_2 = lowerBB20_2;
	}

	public Float getUpperBB20_2() {
		return upperBB20_2;
	}

	public void setUpperBB20_2(Float upperBB20_2) {
		this.upperBB20_2 = upperBB20_2;
	}

	@JsonIgnore
	public Float getRsi5() {
		if (rsi5ns != null)
			return (100 * rsi5ps / (rsi5ns + rsi5ps));
		else
			return null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public static void main(String[] args) throws Exception {
		Quote q = new Quote();
		q.setDate(new Date());
		q.setOpen(1.11f);
		q.setLow(1.10f);
		q.setHigh(1.13f);
		q.setClose(1.12f);
		q.setVolume(100);

		String str = q.toString();
		System.out.println(str);
	}

	public int compareTo(Quote arg0) {
		return this.date.compareTo(arg0.date);
	}

}
