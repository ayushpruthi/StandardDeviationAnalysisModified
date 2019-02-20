package com.standarddeviationanalysis.properties;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigProperties {

	public static Map<String, String> getProperties() throws IOException {
		Map<String, String> propertiesData = new HashMap<>();
		Properties properties = new Properties();
		FileReader fileReader = new FileReader(new File("input" + File.separator + "config.properties"));
		properties.load(fileReader);
		fileReader.close();
		for (Object key : properties.keySet()) {
			propertiesData.put((String) key, properties.getProperty((String) key));
		}
		return propertiesData;
	}

}
