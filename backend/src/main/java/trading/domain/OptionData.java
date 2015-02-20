package trading.domain;

import org.apache.commons.lang.builder.CompareToBuilder;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class OptionData implements Comparable<OptionData> {
	private boolean callType;
	private float strike;
	private float last;
	private long volume;
	private long oi;
	private float iv;

	public boolean isCallType() {
		return callType;
	}

	public void setCallType(boolean callType) {
		this.callType = callType;
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

	public static String header() {
		return "Contract,PutCall,Strike,Last,Volume,OI,IV\n";
	}
	
	@Override
	public String toString() {
		NumberFormat formatter = new DecimalFormat("#####.##");
		StringBuilder sb = new StringBuilder();
		sb.append(callType?"call":"put");
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
		sb.append("\n");
		return sb.toString();
	}

	public static OptionData fromString(String str) throws Exception {
		OptionData optionData = new OptionData();
		String[] strs = str.split(",");
		int pos = 0;
		optionData.setCallType("call".equals(strs[pos++]));
		optionData.setStrike(Float.parseFloat(strs[pos++]));
		optionData.setLast(Float.parseFloat(strs[pos++]));
		optionData.setVolume(Long.parseLong(strs[pos++]));
		optionData.setOi(Long.parseLong(strs[pos++]));
		optionData.setIv(Float.parseFloat(strs[pos++]));
		return optionData;
	}
	

	public int compareTo(OptionData o) {
		return new CompareToBuilder()
		.append(this.callType,o.callType)
		.append(this.strike,o.strike).toComparison();
	}
}
