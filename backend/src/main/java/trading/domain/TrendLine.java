package trading.domain;

import java.util.Date;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class TrendLine implements Comparable<TrendLine> {

	private Date date1;
	private float price1;
	private Date date2;
	private Date price2;

	private String ticker;
	private boolean support;

	public Date getDate1() {
		return date1;
	}

	public void setDate1(Date date1) {
		this.date1 = date1;
	}

	public float getPrice1() {
		return price1;
	}

	public void setPrice1(float price1) {
		this.price1 = price1;
	}

	public Date getDate2() {
		return date2;
	}

	public void setDate2(Date date2) {
		this.date2 = date2;
	}

	public Date getPrice2() {
		return price2;
	}

	public void setPrice2(Date price2) {
		this.price2 = price2;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public boolean isSupport() {
		return support;
	}

	public void setSupport(boolean support) {
		this.support = support;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public int compareTo(TrendLine o) {
		return new CompareToBuilder().append(this.ticker, o.ticker).append(this.date2, o.date2).append(this.date1, o.date1).toComparison();
	}

}
