package trading.domain;

import org.apache.commons.lang.builder.CompareToBuilder;

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

	public int compareTo(OptionData o) {
		return new CompareToBuilder()
		.append(this.callType,o.callType)
		.append(this.strike,o.strike).toComparison();
	}
}
