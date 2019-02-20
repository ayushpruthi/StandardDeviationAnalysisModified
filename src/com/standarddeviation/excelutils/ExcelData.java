package com.standarddeviation.excelutils;

public class ExcelData {

	private String security;
	private String url;
	private String strikeGap;
	private String callPut;
	private String spot;
	private String previousClose;
	private String premiumATMStrike;
	private String IV;
	private String premium2SD;
	private String premium3SD;
	private String non3SDStrike;
	private String atmStrike;
	private String stikeNear2SD;
	private String srtikeNear3SD;
	private String daysToExpiry;
	private String SD;
	private String oneSD;
	private String twoSD;
	private String threeSD;
	private String expiryDate;
	private boolean isBanned;

	public boolean isBanned() {
		return isBanned;
	}

	public void setBanned(boolean isBanned) {
		this.isBanned = isBanned;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getOneSD() {
		return oneSD;
	}

	public void setOneSD(String oneSD) {
		this.oneSD = oneSD;
	}

	public String getTwoSD() {
		return twoSD;
	}

	public void setTwoSD(String twoSD) {
		this.twoSD = twoSD;
	}

	public String getThreeSD() {
		return threeSD;
	}

	public void setThreeSD(String threeSD) {
		this.threeSD = threeSD;
	}

	public String getSD() {
		return SD;
	}

	public void setSD(String sD) {
		SD = sD;
	}

	public String getDaysToExpiry() {
		return daysToExpiry;
	}

	public void setDaysToExpiry(String daysToExpiry) {
		this.daysToExpiry = daysToExpiry;
	}

	public String getAtmStrike() {
		return atmStrike;
	}

	public void setAtmStrike(String atmStrike) {
		this.atmStrike = atmStrike;
	}

	public String getStikeNear2SD() {
		return stikeNear2SD;
	}

	public void setStikeNear2SD(String stikeNear2SD) {
		this.stikeNear2SD = stikeNear2SD;
	}

	public String getSrtikeNear3SD() {
		return srtikeNear3SD;
	}

	public void setSrtikeNear3SD(String srtikeNear3SD) {
		this.srtikeNear3SD = srtikeNear3SD;
	}

	public String getSecurity() {
		return security;
	}

	public void setSecurity(String security) {
		this.security = security;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getStrikeGap() {
		return strikeGap;
	}

	public void setStrikeGap(String strikeGap) {
		this.strikeGap = strikeGap;
	}

	public String getCallPut() {
		return callPut;
	}

	public void setCallPut(String callPut) {
		this.callPut = callPut;
	}

	public String getSpot() {
		return spot;
	}

	public void setSpot(String spot) {
		this.spot = spot;
	}

	public String getPreviousClose() {
		return previousClose;
	}

	public void setPreviousClose(String previousClose) {
		this.previousClose = previousClose;
	}

	public String getPremiumATMStrike() {
		return premiumATMStrike;
	}

	public void setPremiumATMStrike(String premiumATMStrike) {
		this.premiumATMStrike = premiumATMStrike;
	}

	public String getIV() {
		return IV;
	}

	public void setIV(String iV) {
		IV = iV;
	}

	public String getPremium2SD() {
		return premium2SD;
	}

	public void setPremium2SD(String premium2sd) {
		premium2SD = premium2sd;
	}

	public String getPremium3SD() {
		return premium3SD;
	}

	public void setPremium3SD(String premium3sd) {
		premium3SD = premium3sd;
	}

	public String getNon3SDStrike() {
		return non3SDStrike;
	}

	public void setNon3SDStrike(String non3sdStrike) {
		non3SDStrike = non3sdStrike;
	}

}
