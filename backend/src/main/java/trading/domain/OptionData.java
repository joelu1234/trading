package trading.domain;

import org.apache.commons.lang.builder.CompareToBuilder;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class OptionData implements Comparable<OptionData> {
	
	public enum Type {
		CALL, PUT
	}
	
	private Type type;
	private float strike;
	private float last;
	private long volume;
	private long oi;
	private float iv;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public float getStrike() {
		return strike;
	}

	public void setStrike(float strike) {
		this.strike = strike;
	}

	public float getLast() {
		return last;
	}

	public void setLast(float last) {
		this.last = last;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	public long getOi() {
		return oi;
	}

	public void setOi(long oi) {
		this.oi = oi;
	}

	public float getIv() {
		return iv;
	}

	public void setIv(float iv) {
		this.iv = iv;
	}

	@Override
	public String toString() {
		NumberFormat formatter = new DecimalFormat("#####.##");
		StringBuilder sb = new StringBuilder();
		sb.append(type);
		sb.append(",");
		sb.append(formatter.format(strike));
		sb.append(",");
		sb.append(formatter.format(last));
		sb.append(",");
		sb.append(volume);
		sb.append(",");
		sb.append(oi);
		sb.append(",");
		sb.append(formatter.format(iv));
		return sb.toString();
	}

	public static OptionData fromString(String str) throws Exception {
		OptionData optionData = new OptionData();
		String[] strs = str.split(",");
		int pos = 0;
		optionData.setType(Type.valueOf(strs[pos++]));
		optionData.setStrike(Float.parseFloat(strs[pos++]));
		optionData.setLast(Float.parseFloat(strs[pos++]));
		optionData.setVolume(Long.parseLong(strs[pos++]));
		optionData.setOi(Long.parseLong(strs[pos++]));
		optionData.setIv(Float.parseFloat(strs[pos++]));
		return optionData;
	}

	public int compareTo(OptionData o) {
		return new CompareToBuilder().append(this.type, o.type).append(this.strike, o.strike).toComparison();
	}
}
