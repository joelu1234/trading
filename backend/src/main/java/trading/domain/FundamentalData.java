package trading.domain;

import java.util.ArrayList;
import java.util.List;

public class FundamentalData {

	private String name;
	private String exchange="";
	private String sector;
	private String industry;
	private String country;
	private StockType stockType;
	private boolean optionable;

	private Long shareOutstanding;
	private Long shareFloat;
	private Float bookPerShare;
	private Float cashPerShare;
	private Float dividend = 0.0f; // div payout ratio = div/eps
	private Float peg;
	private Float eps;
	private Float epsNextYear;
	private Float epsThisYearChange;
	private Float epsNextYearChange;
	private Float epsNext5YearChange;
	private Float epsPast5YearChange;
	private Float salesPast5YearChange;
	private Float salesQQ;
	private Float epsQQ;
	private Float insiderOwn;
	private Float institutionOwn;
	private Float roa;
	private Float roe;
	private Float roi;
	private Float grossMargin;
	private Float operationMargin;
	private Float profitMargin;
	private Float shortFloatPercent;
	private Float shortRatio;
	private Float beta;
	private Long avgVol;
	private String earningDate;
	private Float recommendationRate;
	private Float debtOverEquity;

	private Float divGrowthRate5Yr;
	private Float peHight5yrs;
	private Float peLow5yrs;

	private Long ebitda;
	private Long totalDebt;
	private Long operationCashFlow;
	private Long leveredFreeCashFlow;

	private Float high52weeks;
	private Float low52weeks;

	private List<String> indices = new ArrayList<String>();
	private List<AnalystOpinion> opinions = new ArrayList<AnalystOpinion>();
	private List<QuartlyEps> epsHistory = new ArrayList<QuartlyEps>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public StockType getStockType() {
		return stockType;
	}

	public void setStockType(StockType stockType) {
		this.stockType = stockType;
	}

	public boolean isOptionable() {
		return optionable;
	}

	public void setOptionable(boolean optionable) {
		this.optionable = optionable;
	}

	public Long getShareOutstanding() {
		return shareOutstanding;
	}

	public void setShareOutstanding(Long shareOutstanding) {
		this.shareOutstanding = shareOutstanding;
	}

	public Long getShareFloat() {
		return shareFloat;
	}

	public void setShareFloat(Long shareFloat) {
		this.shareFloat = shareFloat;
	}

	public Float getBookPerShare() {
		return bookPerShare;
	}

	public void setBookPerShare(Float bookPerShare) {
		this.bookPerShare = bookPerShare;
	}

	public Float getCashPerShare() {
		return cashPerShare;
	}

	public void setCashPerShare(Float cashPerShare) {
		this.cashPerShare = cashPerShare;
	}

	public Float getDividend() {
		return dividend;
	}

	public void setDividend(Float dividend) {
		this.dividend = dividend;
	}

	public Float getPeg() {
		return peg;
	}

	public void setPeg(Float peg) {
		this.peg = peg;
	}

	public Float getEps() {
		return eps;
	}

	public void setEps(Float eps) {
		this.eps = eps;
	}

	public Float getEpsNextYear() {
		return epsNextYear;
	}

	public void setEpsNextYear(Float epsNextYear) {
		this.epsNextYear = epsNextYear;
	}

	public Float getEpsThisYearChange() {
		return epsThisYearChange;
	}

	public void setEpsThisYearChange(Float epsThisYearChange) {
		this.epsThisYearChange = epsThisYearChange;
	}

	public Float getEpsNextYearChange() {
		return epsNextYearChange;
	}

	public void setEpsNextYearChange(Float epsNextYearChange) {
		this.epsNextYearChange = epsNextYearChange;
	}

	public Float getEpsNext5YearChange() {
		return epsNext5YearChange;
	}

	public void setEpsNext5YearChange(Float epsNext5YearChange) {
		this.epsNext5YearChange = epsNext5YearChange;
	}

	public Float getEpsPast5YearChange() {
		return epsPast5YearChange;
	}

	public void setEpsPast5YearChange(Float epsPast5YearChange) {
		this.epsPast5YearChange = epsPast5YearChange;
	}

	public Float getSalesPast5YearChange() {
		return salesPast5YearChange;
	}

	public void setSalesPast5YearChange(Float salesPast5YearChange) {
		this.salesPast5YearChange = salesPast5YearChange;
	}

	public Float getSalesQQ() {
		return salesQQ;
	}

	public void setSalesQQ(Float salesQQ) {
		this.salesQQ = salesQQ;
	}

	public Float getEpsQQ() {
		return epsQQ;
	}

	public void setEpsQQ(Float epsQQ) {
		this.epsQQ = epsQQ;
	}

	public Float getInsiderOwn() {
		return insiderOwn;
	}

	public void setInsiderOwn(Float insiderOwn) {
		this.insiderOwn = insiderOwn;
	}

	public Float getInstitutionOwn() {
		return institutionOwn;
	}

	public void setInstitutionOwn(Float institutionOwn) {
		this.institutionOwn = institutionOwn;
	}

	public Float getRoa() {
		return roa;
	}

	public void setRoa(Float roa) {
		this.roa = roa;
	}

	public Float getRoe() {
		return roe;
	}

	public void setRoe(Float roe) {
		this.roe = roe;
	}

	public Float getRoi() {
		return roi;
	}

	public void setRoi(Float roi) {
		this.roi = roi;
	}

	public Float getGrossMargin() {
		return grossMargin;
	}

	public void setGrossMargin(Float grossMargin) {
		this.grossMargin = grossMargin;
	}

	public Float getOperationMargin() {
		return operationMargin;
	}

	public void setOperationMargin(Float operationMargin) {
		this.operationMargin = operationMargin;
	}

	public Float getProfitMargin() {
		return profitMargin;
	}

	public void setProfitMargin(Float profitMargin) {
		this.profitMargin = profitMargin;
	}

	public Float getShortFloatPercent() {
		return shortFloatPercent;
	}

	public void setShortFloatPercent(Float shortFloatPercent) {
		this.shortFloatPercent = shortFloatPercent;
	}

	public Float getShortRatio() {
		return shortRatio;
	}

	public void setShortRatio(Float shortRatio) {
		this.shortRatio = shortRatio;
	}

	public Float getBeta() {
		return beta;
	}

	public void setBeta(Float beta) {
		this.beta = beta;
	}

	public Long getAvgVol() {
		return avgVol;
	}

	public void setAvgVol(Long avgVol) {
		this.avgVol = avgVol;
	}

	public String getEarningDate() {
		return earningDate;
	}

	public void setEarningDate(String earningDate) {
		this.earningDate = earningDate;
	}

	public Float getRecommendationRate() {
		return recommendationRate;
	}

	public void setRecommendationRate(Float recommendationRate) {
		this.recommendationRate = recommendationRate;
	}

	public Float getDebtOverEquity() {
		return debtOverEquity;
	}

	public void setDebtOverEquity(Float debtOverEquity) {
		this.debtOverEquity = debtOverEquity;
	}

	public Float getDivGrowthRate5Yr() {
		return divGrowthRate5Yr;
	}

	public void setDivGrowthRate5Yr(Float divGrowthRate5Yr) {
		this.divGrowthRate5Yr = divGrowthRate5Yr;
	}

	public Float getPeHight5yrs() {
		return peHight5yrs;
	}

	public void setPeHight5yrs(Float peHight5yrs) {
		this.peHight5yrs = peHight5yrs;
	}

	public Float getPeLow5yrs() {
		return peLow5yrs;
	}

	public void setPeLow5yrs(Float peLow5yrs) {
		this.peLow5yrs = peLow5yrs;
	}

	public Long getEbitda() {
		return ebitda;
	}

	public void setEbitda(Long ebitda) {
		this.ebitda = ebitda;
	}

	public Long getTotalDebt() {
		return totalDebt;
	}

	public void setTotalDebt(Long totalDebt) {
		this.totalDebt = totalDebt;
	}

	public Long getOperationCashFlow() {
		return operationCashFlow;
	}

	public void setOperationCashFlow(Long operationCashFlow) {
		this.operationCashFlow = operationCashFlow;
	}

	public Long getLeveredFreeCashFlow() {
		return leveredFreeCashFlow;
	}

	public void setLeveredFreeCashFlow(Long leveredFreeCashFlow) {
		this.leveredFreeCashFlow = leveredFreeCashFlow;
	}

	public Float getHigh52weeks() {
		return high52weeks;
	}

	public void setHigh52weeks(Float high52weeks) {
		this.high52weeks = high52weeks;
	}

	public Float getLow52weeks() {
		return low52weeks;
	}

	public void setLow52weeks(Float low52weeks) {
		this.low52weeks = low52weeks;
	}

	public List<String> getIndices() {
		return indices;
	}

	public void setIndices(List<String> indices) {
		this.indices = indices;
	}

	public List<AnalystOpinion> getOpinions() {
		return opinions;
	}

	public void setOpinions(List<AnalystOpinion> opinions) {
		this.opinions = opinions;
	}

	public List<QuartlyEps> getEpsHistory() {
		return epsHistory;
	}

	public void setEpsHistory(List<QuartlyEps> epsHistory) {
		this.epsHistory = epsHistory;
	}

}
