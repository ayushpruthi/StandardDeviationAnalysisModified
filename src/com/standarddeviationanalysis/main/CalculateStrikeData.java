package com.standarddeviationanalysis.main;

import com.standarddeviation.excelutils.ExcelData;

public class CalculateStrikeData {

	public static Long calculateSD(ExcelData data) {
		Double daysToExpiry = null;
		Double previuosClose = null;
		Double IV = null;
		try {
			daysToExpiry = Double.parseDouble(data.getDaysToExpiry().trim());
			previuosClose = Double.parseDouble(data.getPreviousClose().trim());
			IV = Double.parseDouble(data.getIV().trim());
		} catch (NumberFormatException e) {
			return null;
		}
		Double value1 = Math.pow(daysToExpiry / 365.25, 0.5);
		Double value2 = previuosClose * (IV / 100.0);
		return Math.round(value1 * value2);
	}

	public static Double calculateSDData(ExcelData data, String type) {
		Double previousClose = null;
		Double SD = null;
		try {
			previousClose = Double.parseDouble(data.getPreviousClose().trim());
			SD = Double.parseDouble(data.getSD().trim());
		} catch (NumberFormatException e) {
			return null;
		}

		switch (type) {
		case "1SD":
			if (data.getCallPut().trim().equalsIgnoreCase("CE")) {
				return previousClose + SD;
			} else {
				return previousClose - SD;
			}
		case "2SD":
			if (data.getCallPut().trim().equalsIgnoreCase("CE")) {
				return previousClose + (2 * SD);
			} else {
				return previousClose - (2 * SD);
			}
		case "3SD":
			if (data.getCallPut().trim().equalsIgnoreCase("CE")) {
				return previousClose + (3 * SD);
			} else {
				return previousClose - (3 * SD);
			}
		default:
			return null;
		}

	}

	public static Double calculateStrike(ExcelData data, String type) {
		Double strikeGap = null;
		try {
			strikeGap = Double.parseDouble(data.getStrikeGap().trim());
		} catch (NumberFormatException e) {
			return null;
		}

		switch (type) {
		case "atm":
			Double previousClose = null;
			try {
				previousClose = Double.parseDouble(data.getPreviousClose().trim());
			} catch (NumberFormatException e) {
				return null;
			}
			return Math.round(previousClose / strikeGap) * strikeGap;
		case "2SD":
			Double twoSD = null;
			try {
				twoSD = Double.parseDouble(data.getTwoSD().trim());
			} catch (NumberFormatException e) {
				return null;
			}
			return Math.round(twoSD / strikeGap) * strikeGap;

		case "3SD":
			Double threeSD = null;
			try {
				threeSD = Double.parseDouble(data.getThreeSD().trim());
			} catch (NumberFormatException e) {
				return null;
			}
			return Math.round(threeSD / strikeGap) * strikeGap;
		default:
			return null;
		}
	}

}
