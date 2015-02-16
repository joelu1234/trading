package trading.domain;

public class FundamentalData {
	private boolean optionable;
	private long marketCap;
	private long shareOutstanding;
	private long shartFloat;
	private float institutionOwn;
	private long shortFloat;
	private float bookPerShare;
	private float cashPerShare;
	private float dividend; // div payout ratio = div/eps
	private float divGrowthRate5Yr;
	private float eps;
	private float epsNextYear;
	private float roa;
	private float roe;
	private float grossMargin;
	private float operationMargin;
	private float profitMargin;
	private float beta;
	private float peHight5yrs;
	private float peLow5yrs;
	private long avgVol;
	private String earningDate;
	private float recommendationRate;
	private float totalDebt;
	private float debtOverEquity;
	private long operationCashFlow;
	private float high52weeks;
	private float low52weeks;

	public boolean isOptionable() {
		return optionable;
	}

	public void setOptionable(boolean optionable) {
		this.optionable = optionable;
	}

	public long getMarketCap() {
		return marketCap;
	}

	public void setMarketCap(long marketCap) {
		this.marketCap = marketCap;
	}

	public long getShareOutstanding() {
		return shareOutstanding;
	}

	public void setShareOutstanding(long shareOutstanding) {
		this.shareOutstanding = shareOutstanding;
	}

	public long getShartFloat() {
		return shartFloat;
	}

	public void setShartFloat(long shartFloat) {
		this.shartFloat = shartFloat;
	}

	public float getInstitutionOwn() {
		return institutionOwn;
	}

	public void setInstitutionOwn(float institutionOwn) {
		this.institutionOwn = institutionOwn;
	}

	public long getShortFloat() {
		return shortFloat;
	}

	public void setShortFloat(long shortFloat) {
		this.shortFloat = shortFloat;
	}

	public float getBookPerShare() {
		return bookPerShare;
	}

	public void setBookPerShare(float bookPerShare) {
		this.bookPerShare = bookPerShare;
	}

	public float getCashPerShare() {
		return cashPerShare;
	}

	public void setCashPerShare(float cashPerShare) {
		this.cashPerShare = cashPerShare;
	}

	public float getDividend() {
		return dividend;
	}

	public void setDividend(float dividend) {
		this.dividend = dividend;
	}

	public float getDivGrowthRate5Yr() {
		return divGrowthRate5Yr;
	}

	public void setDivGrowthRate5Yr(float divGrowthRate5Yr) {
		this.divGrowthRate5Yr = divGrowthRate5Yr;
	}

	public float getEps() {
		return eps;
	}

	public void setEps(float eps) {
		this.eps = eps;
	}

	public float getEpsNextYear() {
		return epsNextYear;
	}

	public void setEpsNextYear(float epsNextYear) {
		this.epsNextYear = epsNextYear;
	}

	public float getRoa() {
		return roa;
	}

	public void setRoa(float roa) {
		this.roa = roa;
	}

	public float getRoe() {
		return roe;
	}

	public void setRoe(float roe) {
		this.roe = roe;
	}

	public float getGrossMargin() {
		return grossMargin;
	}

	public void setGrossMargin(float grossMargin) {
		this.grossMargin = grossMargin;
	}

	public float getOperationMargin() {
		return operationMargin;
	}

	public void setOperationMargin(float operationMargin) {
		this.operationMargin = operationMargin;
	}

	public float getProfitMargin() {
		return profitMargin;
	}

	public void setProfitMargin(float profitMargin) {
		this.profitMargin = profitMargin;
	}

	public float getBeta() {
		return beta;
	}

	public void setBeta(float beta) {
		this.beta = beta;
	}

	public float getPeHight5yrs() {
		return peHight5yrs;
	}

	public void setPeHight5yrs(float peHight5yrs) {
		this.peHight5yrs = peHight5yrs;
	}

	public float getPeLow5yrs() {
		return peLow5yrs;
	}

	public void setPeLow5yrs(float peLow5yrs) {
		this.peLow5yrs = peLow5yrs;
	}

	public long getAvgVol() {
		return avgVol;
	}

	public void setAvgVol(long avgVol) {
		this.avgVol = avgVol;
	}

	public String getEarningDate() {
		return earningDate;
	}

	public void setEarningDate(String earningDate) {
		this.earningDate = earningDate;
	}

	public float getRecommendationRate() {
		return recommendationRate;
	}

	public void setRecommendationRate(float recommendationRate) {
		this.recommendationRate = recommendationRate;
	}

	public float getTotalDebt() {
		return totalDebt;
	}

	public void setTotalDebt(float totalDebt) {
		this.totalDebt = totalDebt;
	}

	public float getDebtOverEquity() {
		return debtOverEquity;
	}

	public void setDebtOverEquity(float debtOverEquity) {
		this.debtOverEquity = debtOverEquity;
	}

	public long getOperationCashFlow() {
		return operationCashFlow;
	}

	public void setOperationCashFlow(long operationCashFlow) {
		this.operationCashFlow = operationCashFlow;
	}

	public float getHigh52weeks() {
		return high52weeks;
	}

	public void setHigh52weeks(float high52weeks) {
		this.high52weeks = high52weeks;
	}

	public float getLow52weeks() {
		return low52weeks;
	}

	public void setLow52weeks(float low52weeks) {
		this.low52weeks = low52weeks;
	}

}
