package com.standarddeviationanalysis.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.standarddeviation.excelutils.ExcelData;
import com.standarddeviation.excelutils.SuiteController;
import com.standarddeviationanalysis.htmlresponse.ParseHtmlResponse;
import com.standarddeviationanalysis.httprequest.HttpRequest;
import com.standarddeviationanalysis.optionchain.OptionChainData;
import com.standarddeviationanalysis.properties.ConfigProperties;

public class ExecutionStart {
	private static Map<String, String> properties = null;
	private static String baseUrl = null;
	private static List<String> bannedSecurities = null;

	static {
		try {
			properties = ConfigProperties.getProperties();
			baseUrl = properties.get("BaseUrl");
		} catch (IOException e) {
			System.out.println("Not able to read config.properties file");
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static void main(String[] args) {
		List<ExcelData> suiteData = null;
		Logger.getRootLogger().setLevel(Level.OFF);
		System.out.println("Starting execution...");
		if (properties.get("RunFor").equalsIgnoreCase("Current")) {
			System.out.println("Getting banned securities...");
			bannedSecurities = getBannedSecurities();
		}

		System.out.println("Reading excel file: " + properties.get("SheetName"));
		try {
			suiteData = SuiteController.getExcelData(properties.get("SheetName"), properties.get("TabName"),
					properties.get("RunFor").trim());
		} catch (IOException e) {
			System.out.println("Not able to read excel file " + properties.get("SheetName"));
			e.printStackTrace();
			System.exit(0);
		}
		executeSuite(suiteData);
		// for (ExcelData data : suiteData) {
		// if (properties.get("RunFor").equalsIgnoreCase("Previous")) {
		// System.out.print("Previuos Close: " + data.getPreviousClose() + " IV: " +
		// data.getIV()
		// + " Premium ATM Strike: " + data.getPremiumATMStrike());
		// } else {
		// System.out.print("Spot: " + data.getSpot() + " Premium2SD: " +
		// data.getPremium2SD() + " Premium 3SD: "
		// + data.getPremium3SD() + " Non 3SD strike: " + data.getNon3SDStrike());
		// }
		// System.out.println();
		// }
		System.out.println("Putting results in excel file...");
		try {
			SuiteController.dumpExcelResults(suiteData, properties.get("RunFor"), properties.get("SheetName"),
					properties.get("TabName"));
		} catch (IOException e) {
			System.out.println("Error in writing execution results in excel");
			e.printStackTrace();
		}
		System.out.println("Execution ended...");
	}

	private static void executeSuite(List<ExcelData> suiteData) {
		for (ExcelData data : suiteData) {
			System.out.println("Executing request for security: " + data.getSecurity());
			String response = null;
			ParseHtmlResponse parseResponse = null;
			Double atmStrike = null;
			try {
				if (data.getSecurity().length() > 0) {
					String securityUrl = baseUrl.replace("securityName", data.getSecurity().trim())
							.replace("expiryDate", data.getExpiryDate().replaceAll("-", "").toUpperCase().trim());
					response = HttpRequest.executeRequestAndGetResponse(securityUrl);
					parseResponse = new ParseHtmlResponse(response);
					String securityPrice = parseResponse.getSecurityPrice();
					if (securityPrice == null) {
						securityPrice = "NA";
					}
					List<OptionChainData> optionData = parseResponse.getOptionChainData();
					if (properties.get("RunFor").equalsIgnoreCase("Previous")) {
						data.setPreviousClose(securityPrice);
						atmStrike = CalculateStrikeData.calculateStrike(data, "atm");
						if (atmStrike == null) {
							data.setAtmStrike("NA");
						} else {
							data.setAtmStrike(atmStrike.toString());
						}

						List<OptionChainData> result = getMatchingStrikePriceData(optionData, data.getAtmStrike());
						if (result.size() == 0) {
							data.setIV("NA");
							data.setPremiumATMStrike("NA");
						} else {
							String premiumATMStrike = null;
							if (data.getCallPut().equalsIgnoreCase("CE")) {
								try {
									data.setIV(new Long(Math.round(Double.parseDouble(result.get(0).getCallIV())))
											.toString());
								} catch (NumberFormatException e) {
									data.setIV("NA");
								}
								premiumATMStrike = result.get(0).getCallLTP().trim();

							} else {
								try {
									data.setIV(new Long(Math.round(Double.parseDouble(result.get(0).getPutIV())))
											.toString());
								} catch (NumberFormatException e) {
									data.setIV("NA");
								}
								premiumATMStrike = result.get(0).getPutLTP().trim();
							}
							if (premiumATMStrike.equalsIgnoreCase("-")) {
								data.setPremiumATMStrike("NA");
							} else {
								data.setPremiumATMStrike(premiumATMStrike);
							}
						}
						Long SD = CalculateStrikeData.calculateSD(data);
						if (SD == null) {
							data.setSD("NA");
							data.setOneSD("NA");
							data.setTwoSD("NA");
							data.setThreeSD("NA");
							data.setStikeNear2SD("NA");
							data.setSrtikeNear3SD("NA");
						} else {
							data.setSD(SD.toString());
							Double oneSD = CalculateStrikeData.calculateSDData(data, "1SD");
							Double twoSD = CalculateStrikeData.calculateSDData(data, "2SD");
							Double threeSD = CalculateStrikeData.calculateSDData(data, "3SD");
							data.setOneSD(oneSD.toString());
							data.setTwoSD(twoSD.toString());
							data.setThreeSD(threeSD.toString());
							data.setStikeNear2SD(CalculateStrikeData.calculateStrike(data, "2SD").toString());
							data.setSrtikeNear3SD(CalculateStrikeData.calculateStrike(data, "3SD").toString());
						}

					} else {
						data.setSpot(securityPrice);
						if (bannedSecurities.contains(data.getSecurity().trim())) {
							data.setBanned(true);
						} else {
							data.setBanned(false);
						}
						if (data.getStikeNear2SD().trim().equalsIgnoreCase("NA")) {
							data.setPremium2SD("NA");
						} else {
							List<OptionChainData> resultS2 = getMatchingStrikePriceData(optionData,
									data.getStikeNear2SD());
							if (resultS2.size() == 0) {
								data.setPremium2SD("NA");
							} else {
								if (data.getCallPut().equalsIgnoreCase("CE")) {
									try {
										Float.parseFloat(resultS2.get(0).getCallLTP());
										data.setPremium2SD(resultS2.get(0).getCallLTP());
									} catch (NumberFormatException e) {
										data.setPremium2SD("NA");
									}

								} else {
									try {
										Float.parseFloat((resultS2.get(0).getPutLTP()));
										data.setPremium2SD(resultS2.get(0).getPutLTP());
									} catch (NumberFormatException e) {
										data.setPremium2SD("NA");
									}

								}
							}
						}
						if (data.getSrtikeNear3SD().trim().equalsIgnoreCase("NA")) {
							data.setPremium3SD("NA");
							data.setNon3SDStrike("NA");
						} else {
							List<OptionChainData> resultS3 = getMatchingStrikePriceData(optionData,
									data.getSrtikeNear3SD());
							if (resultS3.size() == 0) {
								setNon3SDPrice(optionData, data);
							} else {
								if (data.getCallPut().equalsIgnoreCase("CE")) {
									try {
										Float.parseFloat(resultS3.get(0).getCallLTP());
										data.setPremium3SD(resultS3.get(0).getCallLTP());
									} catch (NumberFormatException e) {
										setNon3SDPrice(optionData, data);
									}

								} else if (data.getCallPut().equalsIgnoreCase("PE")) {
									try {
										Float.parseFloat(resultS3.get(0).getPutLTP());
										data.setPremium3SD(resultS3.get(0).getPutLTP());
									} catch (NumberFormatException e) {
										setNon3SDPrice(optionData, data);
									}

								} else {
									data.setPremium3SD("NA");
									data.setNon3SDStrike("NA");
								}
							}
						}
					}
				} else {
					if (properties.get("RunFor").equalsIgnoreCase("Previous")) {
						data.setPreviousClose("NA");
						data.setIV("NA");
						data.setPremiumATMStrike("NA");
						data.setAtmStrike("NA");
						data.setSD("NA");
						data.setOneSD("NA");
						data.setTwoSD("NA");
						data.setThreeSD("NA");
						data.setStikeNear2SD("NA");
						data.setSrtikeNear3SD("NA");
					} else {
						data.setSpot("NA");
						data.setPremium2SD("NA");
						data.setPremium3SD("NA");
						data.setNon3SDStrike("NA");
					}
				}

			} catch (IOException e) {
				System.out.println("Error in executing request for " + data.getSecurity());
				e.printStackTrace();
			}

		}
	}

	private static List<OptionChainData> getMatchingStrikePriceData(List<OptionChainData> optionData,
			String strikePrice) {
		List<OptionChainData> result = (List<OptionChainData>) optionData.stream()
				.filter(item -> item.getStrikePrice().equals(Double.parseDouble(strikePrice)))
				.collect(Collectors.toList());
		return result;
	}

	private static OptionChainData getNearestStrikePrice(List<OptionChainData> optionData, String strikePrice,
			String callPut) {
		boolean nearestStrikeFound = false;
		OptionChainData objSearch = new OptionChainData();
		objSearch.setStrikePrice(strikePrice);
		optionData.sort((data1, data2) -> data1.getStrikePrice().compareTo(data2.getStrikePrice()));
		int index = Collections.binarySearch(optionData, objSearch, (data1, data2) -> ((OptionChainData) data1)
				.getStrikePrice().compareTo(((OptionChainData) data2).getStrikePrice()));
		if (index < 0) {
			index = Math.abs(index);
			index--;
		}
		if (callPut.equalsIgnoreCase("CE")) {
			while (index > 0) {
				index--;
				OptionChainData data = optionData.get(index);
				try {
					Float.parseFloat(data.getCallLTP().trim());
					nearestStrikeFound = true;
					break;
				} catch (NumberFormatException e) {

				}
			}
			if (nearestStrikeFound == true) {
				return optionData.get(index);
			}
			return null;
		} else {
			while (index < optionData.size()) {

				OptionChainData data = optionData.get(index);
				try {
					Float.parseFloat(data.getPutLTP().trim());
					nearestStrikeFound = true;
					break;
				} catch (NumberFormatException e) {

				}
				index++;
			}
			if (nearestStrikeFound == true) {
				return optionData.get(index);
			}
			return null;
		}
	}

	private static void setNon3SDPrice(List<OptionChainData> optionData, ExcelData data) {
		OptionChainData nearestPrice = getNearestStrikePrice(optionData, data.getSrtikeNear3SD(), data.getCallPut());
		if (nearestPrice == null) {
			data.setNon3SDStrike("NA");
			data.setPremium3SD("NA");
		} else {
			data.setNon3SDStrike(nearestPrice.getStrikePrice().toString());
			if (data.getCallPut().equalsIgnoreCase("CE")) {
				data.setPremium3SD(nearestPrice.getCallLTP());
			} else {
				data.setPremium3SD(nearestPrice.getPutLTP());
			}
		}

	}

	private static List<String> getBannedSecurities() {
		List<String> bannedSecurities = new ArrayList<>();
		try {
			String response = HttpRequest.executeRequestAndGetResponse(properties.get("BannedSecuritiesUrl"));
			String[] bannedSecuritiesData = response.split(":")[1].split("\n");
			for (int i = 0; i < bannedSecuritiesData.length; i++) {
				if (bannedSecuritiesData[i] != null && bannedSecuritiesData[i].length() != 0
						&& bannedSecuritiesData[i] != " " && bannedSecuritiesData[i].contains(",")) {
					bannedSecurities.add(bannedSecuritiesData[i].split(",")[1].trim());
				}
			}
		} catch (IOException e) {
			System.out.println("Error encountered in getting banned securities...");
			e.printStackTrace();
		}
		return bannedSecurities;
	}

}
