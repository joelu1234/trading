package trading.domain;

import java.util.Date;

public class QuartlyEps implements Comparable<QuartlyEps> {
	private Date qtr;
	private float eps;

	public Date getQtr() {
		return qtr;
	}

	public void setQtr(Date qtr) {
		this.qtr = qtr;
	}

	public float getEps() {
		return eps;
	}

	public void setEps(float eps) {
		this.eps = eps;
	}

	public int compareTo(QuartlyEps arg0) {
		return arg0.qtr.compareTo(this.qtr);
	}
}