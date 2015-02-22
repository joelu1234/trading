package trading.domain;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Quote implements Comparable<Quote> {
	public static final String DATE_FORMAT = "yyyyMMdd";
	private Date date;
	private float open;
	private float close;
	private float low;
	private float high;
	private long volume;

	private Map<Integer, Float> simpleMaMap = new HashMap<Integer, Float>();
	private Float rsi5;
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

	public Float getSimpleMA(int days) {
		return simpleMaMap.get(days);
	}

	public void setSimpleMA(int days, Float ma) {
		simpleMaMap.put(days, ma);
	}

	public Float getRsi5() {
		return rsi5;
	}

	public void setRsi5(Float rsi5) {
		this.rsi5 = rsi5;
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

	@Override
	public String toString() {
		NumberFormat formatter = new DecimalFormat("#####.##");
		StringBuilder sb = new StringBuilder();
		sb.append(new SimpleDateFormat(DATE_FORMAT).format(date));
		sb.append(",");
		sb.append(formatter.format(open));
		sb.append(",");
		sb.append(formatter.format(high));
		sb.append(",");
		sb.append(formatter.format(low));
		sb.append(",");
		sb.append(formatter.format(close));
		sb.append(",");
		sb.append(volume);
		sb.append("\n");
		return sb.toString();
	}

	public static Quote fromString(String str) throws Exception {
		Quote quote = new Quote();
		String[] strs = str.split(",");
		int pos = 0;
		quote.setDate(new SimpleDateFormat(DATE_FORMAT).parse(strs[pos++]));
		quote.setOpen(Float.parseFloat(strs[pos++]));
		quote.setHigh(Float.parseFloat(strs[pos++]));
		quote.setLow(Float.parseFloat(strs[pos++]));
		quote.setClose(Float.parseFloat(strs[pos++]));
		quote.setVolume(Long.parseLong(strs[pos++].trim()));
		return quote;
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

		Quote q1 = Quote.fromString(str);
		System.out.println(q1.toString());
	}

	public int compareTo(Quote arg0) {
		return this.date.compareTo(arg0.date);
	}

}
