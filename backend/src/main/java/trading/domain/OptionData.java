package trading.domain;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class OptionData implements Comparable<OptionData> {
	private String contractName;
	private String putCall; //
	private float strike;
	private float last;
	private long volume;
	private long oi;
	private float iv;

	public String getContractName() {
		return contractName;
	}

	public void setContractName(String contractName) {
		this.contractName = contractName;
	}

	public String getPutCall() {
		return putCall;
	}

	public void setPutCall(String putCall) {
		this.putCall = putCall;
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
		sb.append(contractName);
		sb.append(",");
		sb.append(putCall);
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
		optionData.setContractName(strs[pos++]);
		optionData.setPutCall(strs[pos++]);
		optionData.setStrike(Float.parseFloat(strs[pos++]));
		optionData.setLast(Float.parseFloat(strs[pos++]));
		optionData.setVolume(Long.parseLong(strs[pos++]));
		optionData.setOi(Long.parseLong(strs[pos++]));
		optionData.setIv(Float.parseFloat(strs[pos++]));
		return optionData;
	}

	public static void main(String[] args) throws Exception {
		OptionData q = new OptionData();
		q.setContractName("AAPL150206C00080000");
		q.setPutCall("call");
		q.setLast(39.55f);
		q.setVolume(4);
		q.setOi(118);
		q.setIv(334.38f);

		System.out.println(OptionData.header());
		String str = q.toString();
		System.out.println(str);

		OptionData q1 = OptionData.fromString(str);
		System.out.println(q1.toString());
	}

	public int compareTo(OptionData arg0) {
		return this.contractName.compareTo(arg0.contractName);
	}
}
