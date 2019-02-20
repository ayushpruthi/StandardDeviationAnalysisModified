package com.standarddeviationanalysis.optionchain;

public class OptionChainData {

	private String callLTP;
	private String callIV;
	private Double strikePrice;
	private String putLTP;
	private String putIV;

	public String getCallLTP() {
		return callLTP;
	}

	public void setCallLTP(String callLTP) {
		this.callLTP = callLTP;
	}

	public String getCallIV() {
		return callIV;
	}

	public void setCallIV(String callIV) {
		this.callIV = callIV;
	}

	public Double getStrikePrice() {
		return strikePrice;
	}

	public void setStrikePrice(String strikePrice) {
		this.strikePrice = Double.parseDouble(strikePrice.trim());
	}

	public String getPutLTP() {
		return putLTP;
	}

	public void setPutLTP(String putLTP) {
		this.putLTP = putLTP;
	}

	public String getPutIV() {
		return putIV;
	}

	public void setPutIV(String putIV) {
		this.putIV = putIV;
	}

}
