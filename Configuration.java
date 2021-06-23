/****************************************************************
 File name: Configuration.java
 This file is Configuration reads the properties of the device on which tests to be executed from android device,
 properties file.
 Configuration reads various configuration files and provides configuration, properties as java.util.Properties.
 This class is implemented as a Singleton and clients should use the static get Configuration method to get the 
 configuration.
 *****************************************************************/
package com.gm.core;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

/**
 * Configuration reads various configuration files and provides configuration
 * properties as java.util.Properties. This class is implemented as a Singleton
 * and clients should use the static getConfiguration method to get the
 * configuration.
 * 
 * This class reads the main configuration file
 * src/main/resources/config.properties and then based on the environment and
 * device properties it will read corresponding files.
 * 
 * It is very important to provide the files with same name as listed in
 * environment and device properties to load the configuration properties
 * correctly.
 * 
 * This class also sets the MDC log property and it is important not to write
 * any logs until that property is set appropriately.
 * 
 * 
 */
public class Configuration {

	//protected static Logger logger= Logger.getLogger(Configuration.class);
	private static Properties properties = null;
	private static boolean isLoaded = false;

	private Configuration() {
	}

	/**
	 * Returns the configuration properties after loading them from various config
	 * files.
	 * 
	 * @return java.util.Properties - properties from various configuration files.
	 */
	public static Properties getConfiguration() {
		Configuration.initProperties();
		return Configuration.isLoaded ? Configuration.properties : null;
	}

	private static synchronized void initProperties() {
		if (Configuration.isLoaded)
			return;

		try {
			FileInputStream inputStream = new FileInputStream("Config/config.properties");
			properties = new Properties();
			properties.load(inputStream);
			inputStream.close();
		} catch (Exception e) {
			//logger.error("Error reading main.properties file" +e.getMessage());
			e.printStackTrace();
			return;
		}

		String environment = Configuration.properties.getProperty("environment");

		if (StringUtils.isBlank(environment)) {
			//logger.info("Environment is not set in main.properties");
			return;
		}

		String device = properties.getProperty("device");

		if (StringUtils.isBlank(device)) {

			return;
		}

		String deviceProperties = device + ".properties";
		try {
			FileInputStream inputStream = new FileInputStream("Config/" + deviceProperties);
			properties.load(inputStream);
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		Configuration.isLoaded = true;
		return;
	}
}
