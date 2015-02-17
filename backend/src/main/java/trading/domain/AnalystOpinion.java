package trading.domain;

import java.util.Date;

public class AnalystOpinion implements Comparable<AnalystOpinion> {
	private Date date;
	private String action;
	private String firm;
	private String rate;
	private String priceRange;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getFirm() {
		return firm;
	}

	public void setFirm(String firm) {
		this.firm = firm;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getPriceRange() {
		return priceRange;
	}

	public void setPriceRange(String priceRange) {
		this.priceRange = priceRange;
	}

	public int compareTo(AnalystOpinion arg0) {
		return this.date.compareTo(arg0.date);
	}
}
