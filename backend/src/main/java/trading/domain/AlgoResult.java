package trading.domain;

import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class AlgoResult implements Comparable<AlgoResult> {
	public static final String DATE_FORMAT = "yyyyMMdd";
	private Date date;
	private String ticker;
	private String algoName;
	private boolean isBuy;
	private Properties props = new Properties();

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public String getAlgoName() {
		return algoName;
	}

	public void setAlgoName(String algoName) {
		this.algoName = algoName;
	}

	public boolean isBuy() {
		return isBuy;
	}

	public void setBuy(boolean buy) {
		this.isBuy = buy;
	}

	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public int compareTo(AlgoResult o) {
		return new CompareToBuilder().append(this.date, o.date).append(this.ticker, o.ticker).append(this.algoName, o.algoName).toComparison();
	}

}
