/**************************************************
*File Name:-Keywords.java
*This class is implemented as declaring this class instantiate the driver class object and contains Methods,
*which are common for all the modules in the framework
*like clickelement,scroll,verifyText , isElementExist, isElementSelected, isElementChecked etc.
********************************************************/
package com.gm.core;

import static io.appium.java_client.touch.LongPressOptions.longPressOptions;
import static io.appium.java_client.touch.WaitOptions.waitOptions;
import static java.time.Duration.ofMillis;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.text.View;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xslf.model.geom.Context;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ById;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.ConnectionClosedException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import com.gm.utils.Email;
import com.gm.utils.ReportUtil;
import com.google.common.collect.ImmutableMap;
import com.sun.mail.iap.ConnectionException;

import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import io.appium.java_client.touch.offset.ElementOption;
import io.appium.java_client.touch.offset.PointOption;
import sun.awt.im.InputMethodManager;

public class Keywords {

	public static Properties configuration;
	private AppiumDriverLocalService service;
	private AppiumServiceBuilder builder;
	public String saveText;
	public String exceldata;
	public int count = 0;
	public static AndroidDriver<MobileElement> driver;
	protected static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	protected static Calendar cal = Calendar.getInstance();
	public static String today = dateFormat.format(cal.getTime());
	static String nameofCurrMethod = new Exception().getStackTrace()[0].getMethodName();

	public Keywords() throws MalformedURLException, InterruptedException {
		super();

	}

	/***************************************************************************
	 * @function captureScreenshot()--->Captures screenshot when keyword is failed
	 *           or at every step.
	 * 
	 * @param String filename screenshot name
	 * 
	 * @param String keyword_execution_result Pass/Fail
	 **********************************************************************************/

	public void captureScreenshot(String filename, String keyword_execution_result) {
		File scrFile;
		try {
			if (TestDriver.CONFIG.getProperty("screenshot_everystep").equals("Y")) {

				scrFile = (File) ((TakesScreenshot) this.driver).getScreenshotAs(OutputType.FILE);

				FileUtils.copyFile(scrFile,
						new File(System.getProperty("user.dir") + "/" + TestDriver.CONFIG.getProperty("screenshotPath")
								+ "/" + "Report_" + today + "/" + filename + ".jpg"));
			} else if (keyword_execution_result.startsWith(Constants.KEYWORD_FAIL)
					&& TestDriver.CONFIG.getProperty("screenshot_error").equals("Y") && this.driver != null) {
				scrFile = (File) ((TakesScreenshot) this.driver).getScreenshotAs(OutputType.FILE);

				FileUtils.copyFile(scrFile, new File(TestDriver.CONFIG.getProperty("screenshotPath") + "/" + "Report_"
						+ today + "/" + filename + ".jpg"));
			}
		} catch (IOException e) {
			TestDriver.logger.error("Problem at capturing screenshot, Name of current method: captureScreenshot");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**********************************************************
	 * @function openApp() Open the Application under test.
	 * 
	 * @param String object Null
	 * 
	 * @param String data Null
	 * 
	 * @return [Flag] status Flag to indicate Pass or Fail
	 ***************************************************************/
	public static String openApp(String object, String data) {
		String status = "Fail";

		try {
			if (driver != null) {
				driver.launchApp();
				while (driver.findElements(By.xpath("//*[@class='android.widget.Button'][2]")).size() != 0) {
					driver.findElement(By.xpath("//*[@class='android.widget.Button'][2]")).click();
				}
				status = "Pass";
			} else {
				TestDriver.logger.info("AndroidDriver couldn’t locate the element, Name of current method: openApp");
			}
		} catch (NoSuchElementException e) {
			TestDriver.logger.error("AndroidDriver couldn’t locate the element, Name of current method: openApp");
		} catch (NullPointerException e) {
			TestDriver.logger.error("AndroidDriver not initialised ");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/******************************************************************************
	 * @function clickElement() click on given Element present in the View.
	 * 
	 * @param String object Locator of the element to click with given id/xpath.
	 * 
	 * @param String data Null
	 * 
	 * @return [Flag] status Flag To indicate Pass or Fail
	 *******************************************************************************/
	public String clickElement(String object, String data) {
		MobileElement element;
		String status = "Fail";
		try {
			if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
				element = driver.findElementByXPath(TestDriver.UIMap.getProperty(object));
				element.click();
				status = "Pass";
				TestDriver.logger.info("Test Step  passed");
			} else {
				element = driver.findElementById(TestDriver.UIMap.getProperty(object));
				element.click();
				status = "Pass";
				TestDriver.logger.info("Test  Step passed");
			}
		} catch (NoSuchElementException e) {
			TestDriver.logger.error("AndroidDriver couldn’t locate the element, Name of current method: clickElement");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/**********************************************************************
	 * @function isElementExist() Verify the availability of element in view.
	 * 
	 * @param String object Locator of the Element with given id/xpath.
	 * 
	 * @param String data Null
	 * 
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ***********************************************************************/
	public String isElementExist(String object, String data) {
		MobileElement element = null;
		String status = "Fail";
		try {
			if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
				element = driver.findElementByXPath(TestDriver.UIMap.getProperty(object));
				if (element.isDisplayed())
					status = "Pass";
				TestDriver.logger.info("Test passed");
			} else {

				element = driver.findElementById(TestDriver.UIMap.getProperty(object));
				{
					if (element.isDisplayed()) {
						status = "Pass";
						TestDriver.logger.info("Test passed");
					}
				}
			}
		} catch (NoSuchElementException e) {
			TestDriver.logger
					.error("AndroidDriver couldn’t locate the element, Name of current method: isElementExist");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/**********************************************************************************
	 * @function isElementNotExist() Verify the unavailability of element in view.
	 * 
	 * @param String object Locator of the Element is not displayed in the view with
	 *               given id/xpath.
	 * 
	 * @param String data Null
	 * 
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ***********************************************************************************/
	public String isElementNotExist(String object, String data) {
		MobileElement element;
		String status = "Pass";
		try {
			if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
				element = driver.findElementByXPath(TestDriver.UIMap.getProperty(object));
				if (element.isDisplayed())
					status = "Fail";
				TestDriver.logger.info("Test Faild");
			} else {
				element = driver.findElementById(TestDriver.UIMap.getProperty(object));
				if (element.isDisplayed())
					status = "Fail";
				TestDriver.logger.info("Test Faild");
			}
		} catch (NoSuchElementException e) {
			TestDriver.logger
					.error("AndroidDriver couldn’t locate the element, Name of current method: isElementNotExist");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/****************************************************************************************
	 * @function clearTextField() clear the text data in the entry field.
	 * 
	 * @param String object Locator of the entry field to clear the text by using
	 *               id/xpath.
	 * 
	 * @param String data Null
	 * 
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ****************************************************************************************/

	public String clearTextField(String object, String data) {
		MobileElement element = null;
		String status = "Fail";
		try {
			if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
				element = (MobileElement) driver.findElementByXPath(TestDriver.UIMap.getProperty(object));
				element.clear();
				if (element.getText().equals(" ")) {
					status = "Pass";
					TestDriver.logger.info("Test passed");
				}
			} else {
				element = driver.findElementById(TestDriver.UIMap.getProperty(object));
				element.clear();
				if (element.getText().equals(" ")) {
					status = "Pass";
					TestDriver.logger.info("Test passed");
				}
			}
		} catch (NoSuchElementException e) {
			TestDriver.logger
					.error("AndroidDriver couldn’t locate the element, Name of current method: clearTextField");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/********************************************************************************
	 * @function hideAndriodSoftKeyBoard() hide the android softkey pad.
	 * 
	 * @param String object Null
	 * 
	 * @param String data Null
	 * @return [Flag] status Flag To indicate Pass or Fail
	 *******************************************************************************/
	public String hideAndriodSoftKeyBoard(String object, String data) {
		driver.hideKeyboard();
		return "Pass";
	}

	/************************************************************************************
	 * @function closeApp() Close the App under test.
	 * 
	 * @param String object Null
	 * 
	 * @param String data Null
	 * 
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ***********************************************************************************/
	public String closeApp(String object, String data) throws InterruptedException {
		String status = "Fail";
		try {
			if (driver != null) {
				driver.closeApp();
				status = "Pass";
			}
		} catch (WebDriverException e) {

			// Email.send(TestDriver.CONFIG.getProperty("username"),TestDriver.CONFIG.getProperty("password"),TestDriver.CONFIG.getProperty("to_email_two"),TestDriver.CONFIG.getProperty("to_email_one"),
			// "Seems Automations has stopped in middle", "Server will start autometically
			// if you receive more then one email there might me a Hardware issues cable hub
			// etc-----------");
			String path = System.getProperty("user.dir") + TestDriver.UIMap.getProperty("adbStart");
			TestDriver.logger.info("Adb connection got restart!!...");
			String[] command = { "cmd.exe", "/C", "Start/min", path };

			try {
				Process process = Runtime.getRuntime().exec(command);
				Thread.sleep(3000);
				// String currentActivity = driver.currentActivity();
				// String CurrentPackage = driver.getCurrentPackage();
				// adblaunchApplication(currentActivity,CurrentPackage);
				adblaunchApplication((TestDriver.CONFIG.getProperty("AOSPAppActivity")),
						(TestDriver.CONFIG.getProperty("AOSPAppPackage")));
			} catch (IOException e1) { // TODO Auto-generated catch block e1.printStackTrace(); }

			} catch (NullPointerException e2) {
				TestDriver.logger.error("AndroidDriver not initialised");
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}
		return status;
	}

	/************************************************************************************
	 * @function wait() wait upto given amount of time.
	 * 
	 * @param String object Null
	 * 
	 * @param String data number of milli seconds (1 second=1000 milli seconds)
	 * 
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ***********************************************************************************/
	public String wait(String object, String data) throws NumberFormatException, InterruptedException {
		Thread.sleep((Integer.parseInt(data)));
		return "Pass";
	}

	/****************************************************************************************
	 * @function verifyText() verify if text is available on element.
	 * 
	 * @param String object get text from element using id/xpath.
	 * 
	 * @param String data verify text with @param-object text, so that @param-object
	 *               & @param-data has same text.
	 * 
	 * @return status To indicate Pass or Fail
	 *****************************************************************************************/
	public String verifyText(String object, String data) {
		String status = "Fail";
		try {
			String getTextstatus = getText(object, data);
			if (getTextstatus.equalsIgnoreCase("Pass") && data != null) {
				if (data.equals(saveText)) {
					status = "Pass";
					TestDriver.logger.info(saveText + " is matching with " + data);
				} else {
					TestDriver.logger.info(saveText + " is not matching with " + data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/********************************************************************************************
	 * @function getText() gives the text of located element and save it to
	 *           "saveText".
	 * 
	 * @param String object Get the text of the element using id/xpath.
	 * 
	 * @param String data Null
	 * 
	 * @return [Flag] status Flag To indicate Pass or Fail
	 *********************************************************************************************/
	public String getText(String object, String data) {
		String status = "Fail";
		MobileElement element;
		try {
			if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
				element = driver.findElementByXPath(TestDriver.UIMap.getProperty(object));
				saveText = element.getText();
				status = "Pass";
				TestDriver.logger.info(saveText);
			} else {
				element = driver.findElementById(TestDriver.UIMap.getProperty(object));
				saveText = element.getText();
				status = "Pass";
				TestDriver.logger.info(saveText);
			}
		} catch (NoSuchElementException e) {
			TestDriver.logger.error("AndroidDriver couldn’t locate the element, Name of current method: getText");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/******************************************************************************************
	 * @function isElementExistByScroll() Scroll to required element if not exists
	 *           in the view.
	 * 
	 * @param String object Locator of the required element
	 * 
	 * @param String data Null
	 * 
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ********************************************************************************************/
	public String isElementExistByScroll(String object, String data) {
		String status = "Fail";
		try {
			String locator = TestDriver.UIMap.getProperty(object);
			boolean element = driver.findElementByAndroidUIAutomator(
					"new UiScrollable(" + "new UiSelector().scrollable(true)).scrollIntoView("
							+ "new UiSelector().text(\"" + locator + "\"));")
					.isDisplayed();
			if (element == true) {
				status = "Pass";
			} else {
				status = "Fail";
			}

		} catch (NoSuchElementException e) {
			TestDriver.logger
					.error("AndroidDriver couldn’t locate the element, Name of current method: isElementExistByScroll");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/***************************************************************************************************
	 * @function clickAllowNotification() Click on Allow button on App Permissions
	 *           popup.
	 * 
	 * @param String object Null
	 * 
	 * @param String data Null
	 * 
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ***************************************************************************************************/

	public String clickAllowNotification(String object, String data) throws InterruptedException {
		String status = "Fail";
		try {
			if (data != null) {
				while (driver.findElements(By.xpath("//*[@class='android.widget.Button'][2]")).size() != 0) {
					driver.findElement(By.xpath("//*[@class='android.widget.Button'][2]")).click();
					status = "Pass";
				}
			}
		} catch (NoSuchElementException e) {
			TestDriver.logger
					.error("AndroidDriver couldn’t locate the element, Name of current method: ClickAllowNotification");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/**************************************************************************************************
	 * @function clickScrollDown() Scroll upto the required element and click.
	 * 
	 * @param String object Locator of the element to identify & click using Text.
	 * 
	 * @param String data Null
	 * 
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ****************************************************************************************************/
	public String clickScrollDown(String object, String data) throws InterruptedException {
		String status = "Fail";
		try {
			String locator = TestDriver.UIMap.getProperty(object);
			driver.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector())" + ".scrollIntoView("
					+ "new UiSelector().text(" + locator + "));").click();

			status = "Pass";
		} catch (NoSuchElementException e) {
			TestDriver.logger
					.error("AndroidDriver couldn’t locate the element, Name of current method: clickScrollDown");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/***************************************************************************************************
	 * @function isElementEnabled() Find required element is enabled or not.
	 * 
	 * @param String object Locator of the element to validate using id/xpath.
	 * 
	 * @param String data true/false
	 * 
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ***************************************************************************************************/
	public String isElementEnabled(String object, String data) {
		String status = "Fail";
		try {
			if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
				if (driver.findElementByXPath(TestDriver.UIMap.getProperty(object)).getAttribute("enabled")
						.equalsIgnoreCase(data)) {
					status = "Pass";
				}

			} else {
				if (driver.findElementById(TestDriver.UIMap.getProperty(object)).getAttribute("enabled")
						.equalsIgnoreCase(data)) {
					status = "Pass";
				}
			}
		} catch (NoSuchElementException e) {
			TestDriver.logger
					.error("AndroidDriver couldn’t locate the element, Name of current method: isElementEnabled");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/*******************************************************************************
	 * @function isElementSelected() Find the required element is selected or not.
	 * 
	 * @param String object Locator of the element to validate using id/xpath.
	 * 
	 * @param String data true/false
	 * 
	 * @return [Flag] status Flag To indicate Pass or Fail
	 *********************************************************************************/
	public String isElementSelected(String object, String data) {
		String status = "Fail";
		try {
			if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
				if (driver.findElementByXPath(TestDriver.UIMap.getProperty(object)).getAttribute("selected")
						.equalsIgnoreCase(data)) {
					status = "Pass";
				}
			} else {

				if (driver.findElementById(TestDriver.UIMap.getProperty(object)).getAttribute("selected")
						.equalsIgnoreCase(data)) {
					status = "Pass";
				}
			}
		} catch (NoSuchElementException e) {
			TestDriver.logger
					.error("AndroidDriver couldn’t locate the element, Name of current method: isElementSelected");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/***********************************************************************************************
	 * @function launchApplication() launch the Application using given desired
	 *           capabilities,Initialize the driver with apk path & start appium
	 *           server .
	 * @param String apkPath app(.apk) path
	 * 
	 * @param String appActivity app(.apk) activity name
	 * 
	 * @param String appPackage app(.apk) package name
	 * 
	 ************************************************************************************************/
	public void launchApplication(String appActivity, String appPackage, String apkPath)
			throws MalformedURLException, InterruptedException {
		configuration = Configuration.getConfiguration();
		try {
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability(MobileCapabilityType.APP, System.getProperty("user.dir") + apkPath);
			capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION,
					configuration.getProperty(MobileCapabilityType.PLATFORM_VERSION));
			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME,
					configuration.getProperty(MobileCapabilityType.DEVICE_NAME));
			capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME,
					configuration.getProperty(MobileCapabilityType.PLATFORM_NAME));
			capabilities.setCapability("appPackage", appPackage);
			capabilities.setCapability("appActivity", appActivity);
			capabilities.setCapability("noReset", "true");
			capabilities.setCapability("automationName", "UiAutomator2");
			capabilities.setCapability("newCommandTimeout", 300);
			capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 300);
			// Use the below code to start Appium server Explicitly.
			/*
			 * String urlString = new
			 * StringBuffer(configuration.getProperty("protocol")).append("://")
			 * .append(configuration.getProperty("hostname")).append(":").append(
			 * configuration.getProperty("port")) .append("/wd/hub").toString();
			 */
			startAppium();
			driver = new AndroidDriver<MobileElement>(builder, capabilities);
			while (driver.findElements(By.xpath("//*[@class='android.widget.Button'][2]")).size() != 0) {
				driver.findElement(By.xpath("//*[@class='android.widget.Button'][2]")).click();
			}
		} catch (NoSuchElementException e) {
			TestDriver.logger
					.error("AndroidDriver couldn’t locate the element, Name of current method: launchApplication");
		} catch (ConnectionClosedException e) {
			TestDriver.logger.error(
					"driver got disconnected / Required capabilities are not provided, Name of current method: launchApplication.");
		} catch (SessionNotCreatedException e) {
			TestDriver.logger.error("Any of the below issue may stop your App to Launch...");
			TestDriver.logger.error("Case 1: CSM Device offline.");
			TestDriver.logger.error("Case 2: Current APK maynot support your current Flash Build.");
			TestDriver.logger.error(
					"Case 3: In your previous action, Appium server process not terminated fully / Android Driver not terminated properly.");
			Thread.sleep(5000);
			closeApp(apkPath, apkPath);
			stopAppium();
			launchApplication(appActivity, appPackage, apkPath);
			TestDriver.logger.error("Restarting Appium Server...");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/***********************************************************************************************
	 * @function launchRequiredApplication() launch the Application using given
	 *           desired capabilities,Initialize the driver & start appium server
	 *           with out apk.
	 * @param String appActivity app(.apk) activity name
	 *
	 * @param String appPackage app(.apk) package name
	 *
	 ************************************************************************************************/

	public void launchRequiredApplication(String appActivity, String appPackage)
			throws MalformedURLException, InterruptedException {
		configuration = Configuration.getConfiguration();
		try {
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION,
					configuration.getProperty(MobileCapabilityType.PLATFORM_VERSION));
			capabilities.setCapability(MobileCapabilityType.UDID,
					configuration.getProperty(MobileCapabilityType.DEVICE_NAME));
			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME,
					configuration.getProperty(MobileCapabilityType.DEVICE_NAME));
			capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME,
					configuration.getProperty(MobileCapabilityType.PLATFORM_NAME));
			capabilities.setCapability("appPackage", appPackage);
			capabilities.setCapability("appActivity", appActivity);
			capabilities.setCapability("noReset", "true");
			capabilities.setCapability("automationName", "UiAutomator2");
			capabilities.setCapability("newCommandTimeout", 300);
			// Use the below code to start Appium server Explicitly.
			/*
			 * String urlString = new
			 * StringBuffer(configuration.getProperty("protocol")).append("://")
			 * .append(configuration.getProperty("hostname")).append(":").append(
			 * configuration.getProperty("port")) .append("/wd/hub").toString();
			 */
			startAppium();
			driver = new AndroidDriver<MobileElement>(builder, capabilities);
			while (driver.findElements(By.xpath("//*[@class='android.widget.Button'][2]")).size() != 0) {
				driver.findElement(By.xpath("//*[@class='android.widget.Button'][2]")).click();
			}
		} catch (NoSuchElementException e) {
			TestDriver.logger
					.error("AndroidDriver couldn’t locate the element, Name of current method: launchApplication");
		} catch (ConnectionClosedException e) {
			TestDriver.logger.error(
					"driver got disconnected / Required capabilities are not provided, Name of current method: launchApplication.");
		} catch (SessionNotCreatedException e) {
			TestDriver.logger.error("Any of the below issue may stop your App to Launch...");
			TestDriver.logger.error("Case 1: CSM Device offline.");
			TestDriver.logger.error("Case 2: Current APK maynot support your current Flash Build.");
			TestDriver.logger.error(
					"Case 3: In your previous action, Appium server process not terminated fully / Android Driver not terminated properly.");
			Thread.sleep(5000);
			stopAppium();
			launchRequiredApplication(appActivity, appPackage);
			TestDriver.logger.error("Restarting Appium Server...");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/***********************************************************
	 * @function findInchSize() finding devices sizes in inches
	 * @param String Object Null
	 * @param String data Null
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ***************************************************************/
	public String findInchSize(String object, String data) {
		String status = "Fail";
		try {

			int windowHeight = driver.manage().window().getSize().getHeight();
			System.out.println("13inch Height = " + windowHeight);
			int windowWidth = driver.manage().window().getSize().getWidth();
			System.out.println("13inch Width = " + windowWidth + "\n\n");
			status = "Pass";

		} catch (Exception e) {
			e.printStackTrace();
		}

		return status;
	}

	/***********************************************************
	 * @function sendCanMsg() Sends CAN signal to device
	 * @param String object path to batch file to send CAN signal
	 * @param String data Null
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ***************************************************************/
	public String sendCanMsg(String object, String data) {
		String status = "Fail";
		try {
			String path = System.getProperty("user.dir") + TestDriver.UIMap.getProperty(object);
			String[] command = { "cmd.exe", "/C", "Start/min", path };

			Process process = Runtime.getRuntime().exec(command);
			if (process != null && command != null)
				status = "Pass";
			Thread.sleep(3000);
			TestDriver.logger.info("CAN message Sent  ");
		} catch (NullPointerException e) {
			TestDriver.logger.error("Unable to send CAN Signal, Name of current method: sendCanMsg");
		} catch (IOException e) {
			TestDriver.logger.error("Related CAN file not found, Name of current method: sendCanMsg");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/***********************************************************************************
	 * @function isElementChecked() Find any element is checked (eg. Radio button,
	 *           check box, etc).
	 * @param String object Locator the element to validate using id /xpath.
	 * @param String data passing data as true/false
	 * @return [Flag] status Flag To indicate Pass or Fail
	 **************************************************************************************/
	public String isElementChecked(String object, String data) {
		String status = "Fail";

		try {
			if (TestDriver.UIMap.getProperty(object).startsWith("//")) {

				if (driver.findElementByXPath(TestDriver.UIMap.getProperty(object)).getAttribute("checked")
						.equalsIgnoreCase(data)) {

					status = "Pass";
				}
			} else {
				if (driver.findElementById(TestDriver.UIMap.getProperty(object)).getAttribute("checked")
						.equalsIgnoreCase(data)) {
					status = "Pass";
				}
			}
		} catch (NoSuchElementException e) {
			TestDriver.logger
					.error("AndroidDriver couldn’t locate the element, Name of current method: isElementChecked");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/**************************************************************************
	 * @function selectFromList() Randomly select an element from list of items.
	 * @param String object list of elements on screen
	 * @param String data Null
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ****************************************************************************/
	public String selectFromList(String object, String data) {
		String status = "Fail";
		Random rn = new Random();

		try {
			List<MobileElement> mList = driver.findElements(By.xpath(TestDriver.UIMap.getProperty(object)));

			if (mList.size() > 2) {
				int n = rn.nextInt((mList.size()));
				TestDriver.logger.info("Element Clicked is : " + mList.get(n).getText());
				mList.get(n).click();
				status = "Pass";

			} else if (mList.size() == 2) {
				TestDriver.logger.info("Element Clicked is : " + mList.get(1).getText());
				mList.get(1).click();
				status = "Pass";
			} else if (mList.size() == 1) {
				TestDriver.logger.info("Element Clicked is : " + mList.get(0).getText());
				mList.get(0).click();
				status = "Pass";
			}
		} catch (NoSuchElementException e) {
			TestDriver.logger
					.error("AndroidDriver couldn’t locate the element, Name of current method: selectFromList");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/**********************************************************************************
	 * @function clickBackKey() press Android device back/home button (hard key).
	 * @param String object Null
	 * @param String data Null
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ************************************************************************************/
	public String clickBackKey(String object, String data) {
		String status = "Fail";
		try {
			if (data != null) {

				if (data.equalsIgnoreCase("Back")) {
					driver.pressKey(new KeyEvent(AndroidKey.BACK));
					Thread.sleep(1500);
					status = "Pass"; // return "Pass";
				} else if (data.equalsIgnoreCase("Home")) {
					driver.pressKey(new KeyEvent(AndroidKey.HOME));
					// driver.executeScript("mobile:performEditorAction", ImmutableMap.of("action",
					// "done"));
					// driver.pressKeyCode(66);
					Thread.sleep(1500);
					status = "Pass";
				}
			} else {
				TestDriver.logger.error("Data is not avaialble , Name of current method: clickBackKey");
			}
		} catch (NullPointerException e) {
			TestDriver.logger.error("AndroidDriver is not initialised, Name of current method: clickBackKey");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/****************************************************************************
	 * @function verifyTextWithDynamicdata() get Text on an Element present in the
	 *           View.
	 * @param String object get the text of element using id/xpath
	 * @param String data Null
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ******************************************************************************/
	public String verifyTextWithDynamicdata(String object, String data) {
		String status = "Fail";
		String dynamic_data;
		try {
			if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
				dynamic_data = driver.findElementByXPath(TestDriver.UIMap.getProperty(object)).getText();
				if (dynamic_data.equalsIgnoreCase(saveText)) {
					status = "Pass";
				}
			} else {
				dynamic_data = driver.findElementById(TestDriver.UIMap.getProperty(object)).getText();
				if (dynamic_data.equalsIgnoreCase(saveText)) {
					status = "Pass";
				}
			}
		} catch (NoSuchElementException e) {
			TestDriver.logger.error(
					"AndroidDriver couldn’t locate the element, Name of current method: verifyTextWithDynamicdata");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/*******************************************************************************************
	 * @function verifyTextWithExistingdata() Compares the obtained text with
	 *           expected text.
	 * @param String object get the text from mobile element using id/ xpath.
	 * @param String data Null
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ********************************************************************************************/
	public String verifyTextWithExistingdata(String object, String data) {
		String status = "Pass";
		String dynamicdata;
		try {
			if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
				dynamicdata = driver.findElementByXPath(TestDriver.UIMap.getProperty(object)).getText();
				if (dynamicdata.equalsIgnoreCase(saveText)) {
					status = "Fail";
				}

			} else {
				dynamicdata = driver.findElementById(TestDriver.UIMap.getProperty(object)).getText();
				if (dynamicdata.equalsIgnoreCase(saveText)) {
					status = "Fail";
				}

			}
		} catch (NoSuchElementException e) {
			TestDriver.logger.error(
					"AndroidDriver couldn’t locate the element, Name of current method: verifyTextWithExistingdata");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/**************************************************
	 * @function verifyCharLimit() Verify the number of characters limit
	 * @param String object Locator of the entry field to accept the characters
	 * @param String data Mention the number of characters range
	 * @return [Flag] status Flag To indicate Pass or Fail
	 **************************************************/
	public String verifyCharLimit(String object, String data) {
		String status = "Fail";
		try {
			MobileElement element;
			if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
				element = driver.findElementByXPath(TestDriver.UIMap.getProperty(object));
				if (element.getText().length() <= Integer.parseInt(data)) {
					status = "Pass";
				} else {
					TestDriver.logger
							.error("getText Textlength is not mathing, Name of current method: verifyCharLimit");
				}
			} else {
				element = driver.findElementById(TestDriver.UIMap.getProperty(object));
				System.out.println("Length of Text on Element is : " + element.getText().length());
				if (element.getText().length() <= Integer.parseInt(data)) {
					status = "Pass";
				} else {
					TestDriver.logger
							.error("getText Textlength is not mathing, Name of current method: verifyCharLimit");
				}
			}
		} catch (NoSuchElementException e) {
			TestDriver.logger
					.error("AndroidDriver couldn’t locate the element, Name of current method: verifyCharLimit");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/***************************************************************
	 * @function startAppium() Start appium server
	 * @param String object Null
	 * @param String data null
	 * @return [Flag] Null
	 *******************************************************************/
	public void startAppium() {
		try {
			builder = new AppiumServiceBuilder();
			builder.withIPAddress(configuration.getProperty("hostname"));
			builder.usingPort(Integer.parseInt(configuration.getProperty("port")));
			// builder.withCapabilities(c);
			builder.withArgument(GeneralServerFlag.SESSION_OVERRIDE);
			builder.withArgument(GeneralServerFlag.LOG_LEVEL, "error");
			service = AppiumDriverLocalService.buildService(builder);
			// service.start();
		} catch (Exception e) {
			TestDriver.logger.error("Appium server not srated or Not properly terminated...");
			e.printStackTrace();
		}
	}

	/**********************************************************************
	 * @function stopAppium() Stop appium server
	 * @param String object Null
	 * @param String data null
	 * @return [Flag] Null
	 ************************************************************************/
	public void stopAppium() {
		try {
			service.stop();
			TestDriver.logger.info("Appium Server Stopped");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/************************************************************************
	 * @function longPress() Long press on particular element.
	 * @param String object Locator of the element to long press.
	 * @param String data null
	 * @return [Flag] status Flag To indicate Pass or Fail
	 *************************************************************************/
	public String longPress(String object, String data) throws InterruptedException {
		String status = "Fail";
		MobileElement element;
		try {
			if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
				element = (MobileElement) driver.findElementByXPath(TestDriver.UIMap.getProperty(object));
				new TouchAction(driver).longPress(longPressOptions().withElement((ElementOption.element(element))))
						.release().perform();
				status = "Pass";
			} else {
				element = (MobileElement) driver.findElementById(TestDriver.UIMap.getProperty(object));
				new TouchAction(driver).longPress(longPressOptions().withElement((ElementOption.element(element))))
						.release().perform();
				status = "Pass";
			}
		} catch (NoSuchElementException e) {
			TestDriver.logger.error("AndroidDriver couldn’t locate the element, Name of current method: longPress");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/******************************************************************************
	 * @function doubleClick() Double click on particular element.
	 * @param String object Locator of the element to double click.
	 * @param String data null
	 * @return [Flag] status Flag To indicate Pass or Fail
	 *******************************************************************************/
	public String doubleClick(String object, String data) throws InterruptedException {
		MobileElement element;
		String status = "Fail";
		try {
			if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
				element = driver.findElementByXPath(TestDriver.UIMap.getProperty(object));
				for (int index = 0; index < 2; index++) {
					element.click();
				}
				status = "Pass";
				TestDriver.logger.info("Test passed");
			} else {
				element = driver.findElementById(TestDriver.UIMap.getProperty(object));
				for (int index = 0; index < 2; index++) {
					element.click();
				}
				status = "Pass";
			}
		} catch (NoSuchElementException e) {
			TestDriver.logger.error("AndroidDriver couldn’t locate the element, Name of current method: doubleClick.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/******************************************************************************
	 * @function waitForElement() Waits until the element is visible/displayed
	 * @param String object Locator of the element
	 * @param String data null
	 * @return [Flag] status Flag To indicate Pass or Fail
	 *******************************************************************************/
	public String waitForElement(String object, String data) {
		String status = "Fail";
		try {
			if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
				(new WebDriverWait(driver, 40)).until(
						ExpectedConditions.visibilityOfElementLocated(By.xpath(TestDriver.UIMap.getProperty(object))));
				status = "Pass";
			} else {
				(new WebDriverWait(driver, 40)).until(
						ExpectedConditions.visibilityOfElementLocated(By.id(TestDriver.UIMap.getProperty(object))));
				status = "Pass";
			}
		} catch (NoSuchElementException e) {
			TestDriver.logger
					.error("AndroidDriver couldn’t locate the element, Name of current method: waitForElement.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/*********************************************************************************************************
	 * @function scrollToEndAndValidateAllListElements() Will scroll to end of the
	 *           screen and validates with static list data from UIMaP
	 * @param String object list of entries separated by ","
	 * @param String data null
	 * @return [Flag] status Flag To indicate Pass or Fail
	 *****************************************************************************************************/
	public String scrollToEndAndValidateAllListElements(String object, String data) throws InterruptedException {
		String str[] = object.split(",");
		TouchAction act = new TouchAction(driver);
		List dataSet = Arrays.asList(TestDriver.UIMap.getProperty(str[1]).split(";"));
		Dimension size = driver.manage().window().getSize();
		List<MobileElement> listElements;
		List<String> alllstElements = new ArrayList();
		List<MobileElement> listElements1;
		List newList;
		String listLastElement = "";
		String listFirstElement = "";
		String listSecondElement = "";
		String listThirdElement = "";
		int startVerticalY = 0;
		int endVerticalY = 0;
		int startVerticalX = 0;
		String status = "Fail";
		int scrollCount = 0;
		boolean isScroll = true;
		try {
			if (TestDriver.UIMap.getProperty(str[0]).startsWith("//")) {
				while (isScroll) {

					if (scrollCount == 0) {

						listElements = driver.findElementsByXPath(TestDriver.UIMap.getProperty(str[0]));
						for (MobileElement e : listElements) {
							alllstElements.add(e.getText());
						}
						listLastElement = listElements.get(listElements.size() - 1).getText();
						startVerticalY = (int) (size.height * 0.8);
						endVerticalY = (int) (size.height * 0.21);
						startVerticalX = (int) (size.width / 2.1);

						scrollCount = scrollCount + 1;
					}
					if (scrollCount != 0) {

						act.press(PointOption.point(startVerticalX, startVerticalY))
								.waitAction(waitOptions(ofMillis(6000)))
								.moveTo(PointOption.point(startVerticalX, endVerticalY)).release().perform();

						listElements1 = driver.findElementsByXPath(TestDriver.UIMap.getProperty(str[0]));
						for (MobileElement e : listElements1) {
							alllstElements.add(e.getText());
						}
						listFirstElement = listElements1.get(0).getText();
						listSecondElement = listElements1.get(1).getText();
						listThirdElement = listElements1.get(2).getText();
						if (listLastElement.equalsIgnoreCase(listFirstElement)
								|| listLastElement.equalsIgnoreCase(listSecondElement)
								|| listLastElement.equalsIgnoreCase(listThirdElement)) {
							listLastElement = listElements1.get(listElements1.size() - 1).getText();
							scrollCount = scrollCount + 1;
						} else {
							isScroll = false;
						}
					}
				}
				newList = alllstElements.stream().distinct().collect(Collectors.toList());
				for (Object actualData : newList) {
					TestDriver.logger.info("ActualData: " + actualData);
				}
				for (Object expectedData : dataSet) {
					TestDriver.logger.info("ExpectedData: " + expectedData);
				}
				if (newList.equals(dataSet)) {
					TestDriver.logger.info("Equal");
					status = "Pass";
				} else {
					TestDriver.logger.info("Not equal");
					status = "Fail";
				}
			} else {
				while (isScroll) {

					if (scrollCount == 0) {

						listElements = driver.findElementsById(TestDriver.UIMap.getProperty(str[0]));
						for (MobileElement e : listElements) {
							alllstElements.add(e.getText());
						}
						listLastElement = listElements.get(listElements.size() - 1).getText();
						startVerticalY = (int) (size.height * 0.8);
						endVerticalY = (int) (size.height * 0.21);
						startVerticalX = (int) (size.width / 2.1);
						// TestDriver.logger.info("Cor-dinates " + startVerticalY + "," + endVerticalY +
						// "," + startVerticalX);
						scrollCount = scrollCount + 1;
					}
					if (scrollCount != 0) {

						act.press(PointOption.point(startVerticalX, startVerticalY))
								.waitAction(waitOptions(ofMillis(6000)))
								.moveTo(PointOption.point(startVerticalX, endVerticalY)).release().perform();

						listElements1 = driver.findElementsById(TestDriver.UIMap.getProperty(str[0]));
						for (MobileElement e : listElements1) {
							alllstElements.add(e.getText());
						}
						listFirstElement = listElements1.get(0).getText();
						listSecondElement = listElements1.get(1).getText();
						listThirdElement = listElements1.get(2).getText();
						if (listLastElement.equalsIgnoreCase(listFirstElement)
								|| listLastElement.equalsIgnoreCase(listSecondElement)
								|| listLastElement.equalsIgnoreCase(listThirdElement)) {
							listLastElement = listElements1.get(listElements1.size() - 1).getText();
							scrollCount = scrollCount + 1;
						} else {
							isScroll = false;

						}
					}
				}
				newList = alllstElements.stream().distinct().collect(Collectors.toList());
				for (Object actualData : newList) {
					TestDriver.logger.info("ActualData: " + actualData);
				}
				for (Object expectedData : dataSet) {
					TestDriver.logger.info("ExpectedData: " + expectedData);
				}
				if (newList.equals(dataSet)) {
					TestDriver.logger.info("Equal");
					status = "Pass";
				} else {
					TestDriver.logger.info("Not equal");
					status = "Fail";
				}
			}
		} catch (NoSuchElementException e) {
			TestDriver.logger.error(
					"AndroidDriver couldn’t locate the element, Name of current method: scrollToEndAndValidateAllListElements.");
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/*********************************************************************************
	 * @function clickOnElementFromListItems() Click on the required element in the
	 *           list of elements
	 * @param String object List of element's id/xpath.
	 * @param String data Name of the element to click.
	 * @return [Flag] status Flag To indicate Pass or Fail
	 **********************************************************************************/
	public String clickOnElementFromListItems(String object, String data) {
		String status = "Fail";
		if (driver != null) {
			List<MobileElement> list;
			try {
				if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
					list = driver.findElementsByXPath(TestDriver.UIMap.getProperty(object));
					List<String> lst = new ArrayList<String>();
					for (MobileElement e : list) {
						lst.add(e.getText());
					}
					if (lst.size() != 0 && lst.contains(data)) {
						for (int index = 0; index < list.size(); index++) {
							if (list.get(index).getText().equalsIgnoreCase(data)) {
								list.get(index).click();
								break;
							}
						}
						TestDriver.logger.info("Validate List Items with Index: " + data + ":" + lst.indexOf(data));
						status = "Pass";
					} else if (!lst.contains(data) && lst.size() != 0 && list.size() > 6) {
						MobileElement element = (MobileElement) driver
								.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector())"
										+ ".scrollIntoView(" + "new UiSelector().text(\"" + data + "\"));");
						element.click();
						TestDriver.logger.info("Validate List Item" + data);
						status = "Pass";
					} else {
						status = "Fail";
					}
				} else {
					list = driver.findElementsById(TestDriver.UIMap.getProperty(object));
					List<String> lst = new ArrayList<String>();
					for (MobileElement e : list) {
						lst.add(e.getText());
					}

					if (lst.size() != 0 && lst.contains(data)) {
						for (int index = 0; index < list.size(); index++) {
							if (list.get(index).getText().equalsIgnoreCase(data)) {
								TestDriver.logger.info(
										"See if data matches: " + list.get(index).getText().equalsIgnoreCase(data));
								list.get(index).click();
								break;
							}
						}
						TestDriver.logger.info("Validate List Items with Index" + data + ":" + lst.indexOf(data));
						status = "Pass";
					} else if (!lst.contains(data) && lst.size() != 0 && list.size() > 6) {
						MobileElement element = (MobileElement) driver
								.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector())"
										+ ".scrollIntoView(" + "new UiSelector().text(\"" + data + "\"));");
						element.click();
						TestDriver.logger.info("Validate List Item" + data);
						status = "Pass";
					} else {
						status = "Fail";
					}
				}
			} catch (NoSuchElementException e) {
				TestDriver.logger.error(
						"AndroidDriver couldn’t locate the list elements, Name of current method: clickOnElementFromListItems.");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return status;
	}

	/*******************************************************************************
	 * @function verifyToastmsg() Check toast message is displayed or not.
	 * @param String object get Toast message id/xpath.
	 * @param String data Expected toast message text
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ********************************************************************************/
	public String verifyToastmsg(String object, String data) {
		String status = "Fail";
		String locator;
		try {
			if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
				locator = driver.findElementByXPath(TestDriver.UIMap.getProperty(object)).getText();
				if (locator.equalsIgnoreCase(data)) {
					TestDriver.logger.info(locator);
					status = "Pass";
				}
			} else {
				locator = driver.findElementById(TestDriver.UIMap.getProperty(object)).getText();
				if (locator.equalsIgnoreCase(data)) {
					TestDriver.logger.info(locator);
					status = "Pass";
				}
			}
		} catch (NoSuchElementException e) {
			TestDriver.logger
					.error("AndroidDriver couldn’t locate the list elements, Name of current method: verifyToastmsg.");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/*************************************************************************
	 * @function inputData() passing text to an edit-field/text-box
	 * @param String object mobileElement it can be a edit-field/text-box
	 * @param String data input text
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ******************************************************************************************/
	/*
	 * public String inputData(String object, String data) { String status = "Fail";
	 * MobileElement element; if (!data.isEmpty()) { try { if
	 * (TestDriver.UIMap.getProperty(object).startsWith("//")) { element =
	 * driver.findElementByXPath(TestDriver.UIMap.getProperty(object));
	 * element.clear(); element.sendKeys(data); ((AndroidDriver)
	 * driver).pressKey(new KeyEvent(AndroidKey.ENTER)); //
	 * driver.executeScript("mobile:performEditorAction", ImmutableMap.of("action",
	 * "done")); // driver.pressKeyCode(66); status = "Pass";
	 * 
	 * } else { element =
	 * driver.findElementById(TestDriver.UIMap.getProperty(object));
	 * element.clear(); element.sendKeys(data); ((AndroidDriver)
	 * driver).pressKey(new KeyEvent(AndroidKey.ENTER)); //
	 * driver.executeScript("mobile:performEditorAction", ImmutableMap.of("action",
	 * "done")); status = "Pass"; } } catch (NoSuchElementException e) {
	 * TestDriver.logger.
	 * error("AndroidDriver couldn’t locate the element, Name of current method: inputData"
	 * ); } catch (Exception e) { e.printStackTrace(); } } else { try { Random
	 * randomNumber = new Random(); int number = randomNumber.nextInt(10000); String
	 * text = "Testing" + number; if
	 * (TestDriver.UIMap.getProperty(object).startsWith("//")) { element =
	 * driver.findElementByXPath(TestDriver.UIMap.getProperty(object));
	 * element.clear(); System.out.println(text); element.sendKeys(text); status =
	 * "Pass"; } else { element =
	 * driver.findElementById(TestDriver.UIMap.getProperty(object));
	 * element.clear(); System.out.println(text); element.sendKeys(text); status =
	 * "Pass"; } } catch (NoSuchElementException e) { TestDriver.logger.
	 * error("AndroidDriver couldn’t locate the element, Name of current method: inputData"
	 * ); } catch (Exception e) { e.printStackTrace();
	 * 
	 * } } return status; }
	 */

	public String inputData(String object, String data) {
		String status = "Fail";
		MobileElement element;
		if (!data.isEmpty()) {
			String[] dataArray = data.split(",");
			try {
				// InputConnection ic = getCurrentInputConnection();
				if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
					element = driver.findElementByXPath(TestDriver.UIMap.getProperty(object));
					element.clear();
				} else {
					element = driver.findElementById(TestDriver.UIMap.getProperty(object));
					element.clear();
				} // normal, unspecified, none, go, search, send, next, done, previous
				element.sendKeys(dataArray[0]);

				switch (dataArray[1]) {
				case "go":
					driver.executeScript("mobile:performEditorAction", ImmutableMap.of("action", "Go"));
					status = "Pass";
					break;
				case "normal":
					driver.executeScript("mobile:performEditorAction", ImmutableMap.of("action", "normal"));
					status = "Pass";
					break;
				case "unspecified":
					driver.executeScript("mobile:performEditorAction", ImmutableMap.of("action", "unspecified"));
					status = "Pass";
					break;
				case "none":
					driver.executeScript("mobile:performEditorAction", ImmutableMap.of("action", "none"));
					status = "Pass";
					break;
				case "search":
					driver.executeScript("mobile:performEditorAction", ImmutableMap.of("action", "search"));
					status = "Pass";
					break;
				case "send":
					driver.executeScript("mobile:performEditorAction", ImmutableMap.of("action", "send"));
					status = "Pass";
					break;
				case "next":
					driver.executeScript("mobile:performEditorAction", ImmutableMap.of("action", "next"));
					status = "Pass";
					break;
				case "done":
					driver.executeScript("mobile:performEditorAction", ImmutableMap.of("action", "done"));
					status = "Pass";
					break;
				case "previous":
					driver.executeScript("mobile:performEditorAction", ImmutableMap.of("action", "previous"));
					status = "Pass";
					break;
				case "enter":

					((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.ENTER));
					/*
					 * Thread.sleep(100); ((AndroidDriver) driver).pressKey(new
					 * KeyEvent(AndroidKey.BACK));
					 */

					// Actions action = new Actions(driver);
					// action.sendKeys(Keys.ENTER).perform();
					status = "Pass";
					break;
				}
			} catch (NoSuchElementException e) {
				TestDriver.logger.error("AndroidDriver couldn’t locate the element, Name of current method: inputData");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				Random randomNumber = new Random();
				int number = randomNumber.nextInt(10000);
				String text = "Testing" + number;
				if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
					element = driver.findElementByXPath(TestDriver.UIMap.getProperty(object));
					element.clear();
					System.out.println(text);
					element.sendKeys(text);
					status = "Pass";
				} else {
					element = driver.findElementById(TestDriver.UIMap.getProperty(object));
					element.clear();
					System.out.println(text);
					element.sendKeys(text);
					status = "Pass";
				}
			} catch (NoSuchElementException e) {
				TestDriver.logger.error("AndroidDriver couldn’t locate the element, Name of current method: inputData");
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
		return status;
	}

	/*********************************************************************
	 * @function dragProgressBar() drags progress bar from one point to another
	 *           point. If ID will available then only this will work
	 * @param String object progress bar Id/Xpath
	 * @param String data drag percentage, example: "data" - 0.9 means drag till 90%
	 *               (data will in decimals)
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ************************************************************************/

	public String dragProgressBar(String object, String data) {
		String status = "Fail";
		TouchAction act = new TouchAction(driver);
		MobileElement progressBar;
		int startX, endX, startY, moveToXDirectionAt;
		float Data = Float.parseFloat(data);

		if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
			progressBar = driver.findElementByXPath(TestDriver.UIMap.getProperty(object));

			startX = progressBar.getLocation().getX();

			endX = progressBar.getSize().getWidth();

			startY = progressBar.getLocation().getY();

			moveToXDirectionAt = (int) (endX * Data);

			act.press(PointOption.point(startX, startY)).moveTo(PointOption.point(moveToXDirectionAt, startY)).release()
					.perform();

			status = "Pass";

		} else {
			progressBar = driver.findElementById(TestDriver.UIMap.getProperty(object));

			startX = progressBar.getLocation().getX();

			endX = progressBar.getSize().getWidth();

			startY = progressBar.getLocation().getY();

			moveToXDirectionAt = (int) (endX * Data);

			act.press(PointOption.point(startX, startY)).moveTo(PointOption.point(moveToXDirectionAt, startY)).release()
					.perform();

			status = "Pass";

		}
		return status;
	}

	/****************************************************************************************
	 * @function verifyClickableStatus( ) to click until the element should be
	 *           enable. (If clickable property is true
	 * @param String object Locator of Element
	 * @param String data Mention false
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ************************************************************************************/
	public String verifyClickableStatus(String object, String data) {
		String status = "Fail";
		String input = "true";
		try {
			if (TestDriver.UIMap.getProperty(object).startsWith("//")) {

				while (!(input.equalsIgnoreCase(data))) {
					input = driver.findElementByXPath(TestDriver.UIMap.getProperty(object)).getAttribute("clickable");
					driver.findElementByXPath(TestDriver.UIMap.getProperty(object)).click();
					if (input.equalsIgnoreCase(data)) {
						status = "Pass";
					}
				}

			} else {

				while (!(input.equalsIgnoreCase(data))) {
					input = driver.findElementById(TestDriver.UIMap.getProperty(object)).getAttribute("clickable");
					driver.findElementById(TestDriver.UIMap.getProperty(object)).click();
					if (input.equalsIgnoreCase(data)) {
						status = "Pass";
					}
				}

			}
		} catch (NoSuchElementException e) {
			TestDriver.logger
					.error("AndroidDriver couldn’t locate the element, Name of current method: verifyClickableStatus");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/****************************************************************************************
	 * Where ever you are using "screenShot" keyword make sure test step description
	 * should be unique, as the image is saved with description name. Example: Step
	 * description should be like "Search_Parr". Example: Step description should be
	 * like "Search_Perpendicular_Right_Screen".
	 **********************************************************************************/

	/*********************************************************************************
	 * @function screenShot() Capture the screen shot and saved with step
	 *           description This screen shot for Image comparision and textexitence
	 *           in Image
	 * @param String object null
	 * 
	 * @param String data cardview screens
	 * 
	 * @return [Flag] status Flag To indicate Pass or Fail
	 *************************************************************************************/
	public String screenShot(String object, String data) {
		String status = "Fail";
		if (TestDriver.stepDescription.contains("-")) {
			String[] description = TestDriver.stepDescription.split("-");
			TestDriver.stepDescription = description[0];
		}
		try {
			Thread.sleep(500);
			File scrFile;
			scrFile = (File) ((TakesScreenshot) this.driver).getScreenshotAs(OutputType.FILE);
			String path = System.getProperty("user.dir") + "/" + TestDriver.CONFIG.getProperty("screenGrabPath") + "/"
					+ ReportUtil.currentSuiteName + "/" + /* data+"/"+ */TestDriver.stepDescription + ".png";
			if (scrFile != null) {
				FileUtils.copyFile(scrFile, new File(path));
				if (data.equalsIgnoreCase("card view")) {
					BufferedImage crpImage = ImageIO.read(scrFile).getSubimage(
							Integer.parseInt(TestDriver.CONFIG.getProperty("cardView_x_value")),
							Integer.parseInt(TestDriver.CONFIG.getProperty("cardView_Y_value")),
							Integer.parseInt(TestDriver.CONFIG.getProperty("cardView_width")),
							Integer.parseInt(TestDriver.CONFIG.getProperty("cardView_height")));

					ImageIO.write(crpImage, "png", new File(path));

				}
				status = "Pass";
			} else {
				System.out.println("No capture image found check once in screenShot() methode");
				status = "Fail";
			}
		} catch (IOException e) {
			TestDriver.logger.error("Unable to copy the screenshot into folder : Methode Name : screenShot");
		} catch (Exception e) {
			TestDriver.logger.error("Problem at capture screenshot  : Methode Name : screenShot");
		}
		return status;
	}

	/*****************************************************************************************
	 * @function containsText() to identify the given text is available or not.
	 * @param String object Locator of Element
	 * @param String data Mention the axure side text
	 * @return [Flag] status Flag To indicate Pass or Fail
	 *******************************************************************************************/

	public String containsText(String object, String data) {
		String status = "Fail";
		if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
			MobileElement element = driver.findElementByXPath(TestDriver.UIMap.getProperty(object));
			if (element.getText().contains(data)) {
				return status = "Pass";
			}
		} else {
			MobileElement element = driver.findElementById(TestDriver.UIMap.getProperty(object));
			if (element.getText().contains(data)) {
				return status = "Pass";
			}
		}
		return status;
	}

	/*****************************************************************************************************
	 * @function adblaunchApplication( )
	 * @param String object Locator of Element
	 * @param String data Mention which direction to be move
	 * @return null
	 ******************************************************************************************************/
	public void adblaunchApplication(String appActivity, String appPackage)
			throws MalformedURLException, InterruptedException {
		configuration = Configuration.getConfiguration();
		try {
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION,
					configuration.getProperty(MobileCapabilityType.PLATFORM_VERSION));
			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME,
					configuration.getProperty(MobileCapabilityType.DEVICE_NAME));
			capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME,
					configuration.getProperty(MobileCapabilityType.PLATFORM_NAME));
			capabilities.setCapability("appPackage", appPackage);
			capabilities.setCapability("appActivity", appActivity);
			capabilities.setCapability("noReset", "true");
			capabilities.setCapability("automationName", "UiAutomator2");
			capabilities.setCapability("newCommandTimeout", 300);
			// Use the below code to start Appium server Explicitly.
			/*
			 * String urlString = new
			 * StringBuffer(configuration.getProperty("protocol")).append("://")
			 * .append(configuration.getProperty("hostname")).append(":").append(
			 * configuration.getProperty("port")) .append("/wd/hub").toString();
			 */
			startAppium();
			driver = new AndroidDriver<MobileElement>(builder, capabilities);
		} catch (Exception e) {

		}
	}

	/*******************************************************************************
	 * @function openRequiredApplication() opens required app.
	 * 
	 * @param String object AppActivity,AppPackage
	 * 
	 * @param String data Null
	 * 
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ********************************************************************************/
	public String openRequiredApplication(String object, String data) throws InterruptedException, Exception {
		String status = "Fail";
		String[] obj = object.split(",");

		System.out.println(obj[0] + "---------" + obj[1]);
		if (data != null) {
			System.out.println(" I am from relaunch");
			launchRequiredApplication(TestDriver.CONFIG.getProperty(obj[0]), TestDriver.CONFIG.getProperty(obj[1]));
			System.out.println("After launchapplication");
			status = "Pass";
		}
		return status;
	}

	/*******************************************************************************
	 * @function closeRequiredApplication() Close the required application
	 * 
	 * @param String object AppPackage
	 * 
	 * @param String data Null
	 * 
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ********************************************************************************/
	public String closeRequiredApplication(String object, String data) throws InterruptedException {
		String status = "Fail";

		try {
			if (driver != null && !object.isEmpty()) {
				String packageArray[] = object.split(",");
				for (int pkgIndex = 0; pkgIndex < packageArray.length; pkgIndex++) {
					driver.terminateApp(TestDriver.CONFIG.getProperty(packageArray[pkgIndex]));
					Thread.sleep(100);
				}

				status = "Pass";
			} else {
				TestDriver.logger.info("Object sholud not be empty and Driver should not be null");
			}
		} catch (NullPointerException e) {
			TestDriver.logger.error("Object should not be NULL : methode name : closeRequiredApplication ");
		} catch (WebDriverException wd) {

			// Email.send(TestDriver.CONFIG.getProperty("username"),TestDriver.CONFIG.getProperty("password"),TestDriver.CONFIG.getProperty("to_email_two"),TestDriver.CONFIG.getProperty("to_email_one"),
			// "Seems Automations has stopped in middle", "Server will start autometically
			// if you receive more then one email there might me a Hardware issues cable hub
			// etc-----------");
			String path = System.getProperty("user.dir") + TestDriver.UIMap.getProperty("adbStart");
			TestDriver.logger.info("Adb connection got restart!!...");
			String[] command = { "cmd.exe", "/C", "Start/min", path };

			try {
				Process process = Runtime.getRuntime().exec(command);
				Thread.sleep(3000);
				// String currentActivity = driver.currentActivity();
				// String CurrentPackage = driver.getCurrentPackage();
				// adblaunchApplication(currentActivity,CurrentPackage);
				adblaunchApplication((TestDriver.CONFIG.getProperty("AOSPAppActivity")),
						(TestDriver.CONFIG.getProperty("AOSPAppPackage")));
			} catch (IOException e1) { // TODO Auto-generated catch block e1.printStackTrace();
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/**********************************************************
	 * @function deleteAllCards() Delete the all Available cards.
	 * 
	 * @param String object --> List of locators 1)Options Xpath,2)Forget phone
	 *               Xpath 3)Forget phone Xpath
	 * 
	 * @param String data --> No phone connected Xpath
	 * 
	 * @return [Flag] status Flag to indicate Pass or Fail
	 ***************************************************************/

	public String deleteAllCards(String object, String data) throws InterruptedException {
		String status = "Fail";
		List<MobileElement> cardsSize;
		String[] obj = object.split(",");
		cardsSize = driver.findElementsByXPath(TestDriver.UIMap.getProperty(obj[0]));// options length
		System.out.println("options lenght " + cardsSize.size());
		int length = cardsSize.size();// 5
		System.out.println(length);// 5

		while (cardsSize.size() != 0) {
			System.out.println(cardsSize.size());
			for (int index = 0; index < obj.length; index++) {
				if (obj[index].equalsIgnoreCase("wait")) {
					Thread.sleep(1000);
				} else {
					Thread.sleep(1000);
					String res = clickElement(obj[index], data);
					if (res.equals("Pass")) {
						TestDriver.logger.info("Clicked on " + obj[index] + " Element");

					} else {
						status = "Fail";
						TestDriver.logger.error("Unable to click on " + obj[index] + " Element");
						break;
					}
				}
			}
			Thread.sleep(1000);
			cardsSize = driver.findElementsByXPath(TestDriver.UIMap.getProperty(obj[0]));
			System.out.println(cardsSize.size());

		}
		System.out.println("After while loop " + cardsSize.size());
		if (cardsSize.size() == 0) {// Current Cards size
			String noPhoneElement = data;
			String res = isElementExist(noPhoneElement, data);
			TestDriver.logger.info("No Phone Connected is Displayed");

			if (res.equalsIgnoreCase("Pass")) {
				status = "Pass";
			} else {
				TestDriver.logger.error("No Phone Connected screen is not Displayed : Methode Name : deleteAllCards");
			}
			TestDriver.logger.info("No Phone Connected is Displayed");
			status = "Pass";
		}

		return status;
	}

	/***********************************************************
	 * @function imageComparison() Comparing two images object--> Expected image and
	 *           Currnet image are same in @Object should be "Same" text for Similar
	 *           images comparisons, Expected image and Currnet image are
	 *           diffrent @object shold be null.
	 * @param String object Empty
	 * @param String data first image step description,second image step description
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ************************************************************************************/
	public String imageComparison(String object, String data) throws IOException {

		if (TestDriver.stepDescription.contains("-")) {
			String[] description = TestDriver.stepDescription.split("-");
			TestDriver.stepDescription = description[0];
		}
		String dataArray[] = data.split(",");
		String baseFile = System.getProperty("user.dir") + "/" + TestDriver.CONFIG.getProperty("screenGrabPath") + "/"
				+ ReportUtil.currentSuiteName + "/" + dataArray[0] + ".png";// First Image location
		String changeFile = System.getProperty("user.dir") + "/" + TestDriver.CONFIG.getProperty("screenGrabPath") + "/"
				+ ReportUtil.currentSuiteName + "/" + dataArray[1] + ".png";// Second Image location to compare with
		// First image
		String destination = System.getProperty("user.dir") + "/" + "ComparedScreens" + "/" + TestDriver.stepDescription
				+ "_" + today;// Compared image stored location
		return image(new File(baseFile), new File(changeFile), object, destination);
	}

	/***********************************************************
	 * @function image() Comparing two images --- This method will call intenally in
	 *           Image Comparison method object--> Expected image and Currnet image
	 *           are same in @Object should be "Same" text for Similar images
	 *           comparisons, Expected image and Currnet image are diffrent @object
	 *           shold be null.
	 * @param File   baseImage - Runtime caputured image before required
	 *               actions(e.g.CAN message)
	 * @param File   compareImage- Runtime caputured image after required
	 *               actions(e.g.CAN message)
	 * @param String destination- Result of base image and compare image.
	 * @return String status Flag To indicate Pass or Fail
	 ************************************************************************************/

	public static String image(File baseImage, File compareImage, String object, String destination)
			throws IOException {
		String status = "Fail";
		int cardView_Width = Integer.parseInt(TestDriver.CONFIG.getProperty("cardView_width"));
		int cardView_Height = Integer.parseInt(TestDriver.CONFIG.getProperty("cardView_height"));
		int window_Height = Integer.parseInt(TestDriver.CONFIG.getProperty("Window_Height"));
		int window_Width = Integer.parseInt(TestDriver.CONFIG.getProperty("Window_Width"));
		int window_13Inch_Height = Integer.parseInt(TestDriver.CONFIG.getProperty("window_13Inch_height"));
		int window_13Inch_Width = Integer.parseInt(TestDriver.CONFIG.getProperty("window_13Inch_width"));
		int imageStrating_x_value = Integer.parseInt(TestDriver.CONFIG.getProperty("Starting_X_value"));
		int imageStrating_y_value = Integer.parseInt(TestDriver.CONFIG.getProperty("Starting_Y_value"));
		int width_10Inch = Integer.parseInt(TestDriver.CONFIG.getProperty("10Inch_Width"));
		int height_10Inch = Integer.parseInt(TestDriver.CONFIG.getProperty("10Inch_Height"));

		int width_13Inch = Integer.parseInt(TestDriver.CONFIG.getProperty("13Inch_Width"));
		int height_13Inch = Integer.parseInt(TestDriver.CONFIG.getProperty("13Inch_Height"));
		List l = new ArrayList();
		// List l1=new ArrayList();
		int windowHeight = driver.manage().window().getSize().getHeight();
		int windowWidth = driver.manage().window().getSize().getWidth();
		BufferedImage bImage = null;
		BufferedImage cImage = null;
		if (ImageIO.read(baseImage).getHeight() == cardView_Height
				&& ImageIO.read(baseImage).getWidth() == cardView_Width
				&& ImageIO.read(compareImage).getHeight() == cardView_Height
				&& ImageIO.read(compareImage).getWidth() == cardView_Width) {
			TestDriver.logger.info("Currently you  are working on Card view screens");
			bImage = ImageIO.read(baseImage);
			cImage = ImageIO.read(compareImage);
		} else if (windowHeight == window_Height && windowWidth == window_Width) {
			// For 10 Inches screen
			TestDriver.logger.info("Currently you  are working in 10 Inch display screen");
			bImage = ImageIO.read(baseImage).getSubimage(imageStrating_x_value, imageStrating_y_value, width_10Inch,
					height_10Inch);
			cImage = ImageIO.read(compareImage).getSubimage(imageStrating_x_value, imageStrating_y_value, width_10Inch,
					height_10Inch);
		} else if (windowHeight == window_13Inch_Height && windowWidth == window_13Inch_Width) {
			// For 13 Inches Screen
			TestDriver.logger.info("Currently you  are working in 13 Inch display screen");
			bImage = ImageIO.read(baseImage).getSubimage(imageStrating_x_value, imageStrating_y_value, width_13Inch,
					height_13Inch);
			cImage = ImageIO.read(compareImage).getSubimage(imageStrating_x_value, imageStrating_y_value, width_13Inch,
					height_13Inch);
		} else {
			TestDriver.logger.error("The screen is not matched with 10 Inch or 13 Inch");
		}

		BufferedImage rImage = new BufferedImage(bImage.getWidth(), bImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		for (int imageY_value = 0; imageY_value < bImage.getHeight(); imageY_value++) {
			for (int imageX_value = 0; imageX_value < bImage.getWidth(); imageX_value++) {
				try {
					// int pixelC = cImage.getRGB(x, y);
					// int pixelB = bImage.getRGB(x, y);

					if (bImage.getRGB(imageX_value, imageY_value) == cImage.getRGB(imageX_value, imageY_value)) {
						rImage.setRGB(imageX_value, imageY_value, bImage.getRGB(imageX_value, imageY_value));
						// l1.add(cImage.getRGB(x, y));
					} else {
						int a = 0xff & bImage.getRGB(imageX_value, imageY_value) >> 24,
								r = 0xff & bImage.getRGB(imageX_value, imageY_value) >> 16,
								g = 0x00 & bImage.getRGB(imageX_value, imageY_value) >> 8,
								b = 0x00 & bImage.getRGB(imageX_value, imageY_value);
						int modifiedRGB = a << 24 | r << 16 | g << 8 | b;
						rImage.setRGB(imageX_value, imageY_value, modifiedRGB);
						l.add(bImage.getRGB(imageX_value, imageY_value));
					}
				} catch (Exception e) {
					// handled hieght or width mismatch
					rImage.setRGB(imageX_value, imageY_value, 0x80ff0000);
				}
			}
		}
		String filePath = baseImage.toPath().toString();
		String fileExtenstion = filePath.substring(filePath.lastIndexOf('.'), filePath.length());
		if (fileExtenstion.toUpperCase().contains("PNG")) {
			ImageIO.write(rImage, "png", new File(destination + fileExtenstion));
		} else {
			ImageIO.write(rImage, "jpg", new File(destination + fileExtenstion));
		}
		if (object.equalsIgnoreCase("Same")) {// For Same images
			if (l.isEmpty()) {// if l is empty means no difference images else images have difference
				status = "Pass";
				System.out.println("Pass");
			} else {
				// System.out.println("True");
				System.out.println("Fail");
				status = "Fail";
			}
			return status;
		} else {// For Different images
			if (l.isEmpty()) {// if l is empty means no difference images else images have difference
				// status="Pass";
				status = "Fail";
				System.out.println("Fail");
			} else {
				System.out.println("True");
				status = "Pass";
			}
			return status;
		}
	}

	/********************************************************************
	 * @function textExistenceInImage() To Verify the required text from image.
	 *
	 * @param String object --> Existence(to check avialabilty of text)/ nothing (to
	 *               check un availability of text)
	 *
	 * @param String data --> ScreenShot Location,text that you need to verify
	 *
	 * @return [Flag] status Flag to indicate Pass or Fail
	 ********************************************************************/
	/*
	 * public String textExistenceInImage(String object, String data) throws
	 * IOException { MobileElement element = null; String status = "Fail"; String
	 * dataArray[] = data.split(","); int d = dataArray.length; // ScreenShots
	 * Folder String image = System.getProperty("user.dir") + "/" +
	 * TestDriver.CONFIG.getProperty("screenGrabPath") + "/" +
	 * ReportUtil.currentSuiteName + "/" + dataArray[0] + ".png";
	 * if(TestDriver.stepDescription.contains("-")){ String[] description =
	 * TestDriver.stepDescription.split("-"); TestDriver.stepDescription =
	 * description[0]; } String output = System.getProperty("user.dir") + "/" +
	 * "OutPutTextFile" + "/" + TestDriver.stepDescription; try { ProcessBuilder
	 * builder = new ProcessBuilder("cmd.exe", "/c", "tesseract " + image + " " +
	 * output); builder.redirectErrorStream(true); Process p = builder.start();
	 * BufferedReader r = new BufferedReader(new
	 * InputStreamReader(p.getInputStream())); String line; Thread.sleep(4000); File
	 * file = new File(output + ".txt"); FileReader fr = new FileReader(file);
	 * BufferedReader br = new BufferedReader(fr); String text; while ((text =
	 * br.readLine()) != null) { // process the line
	 * System.out.println("frist file read" + text + text.length());
	 * System.out.println("inputdata" + dataArray[1] + dataArray[1].length()); if
	 * (object.equalsIgnoreCase("Existence")) {
	 * if(dataArray[1].equalsIgnoreCase("FM")||dataArray[1].equalsIgnoreCase("AM")||
	 * dataArray[1].equalsIgnoreCase("No Audio Source Selected")) { if
	 * (text.contains("FM")||text.equalsIgnoreCase("FM")||text.contains("AM")||text.
	 * equalsIgnoreCase("AM")||text.contains("No Audio Source Selected")||text.
	 * equalsIgnoreCase("No Audio Source Selected")) { System.out.println(text);
	 * status = "Pass";
	 * 
	 * } } else if
	 * (text.contains(dataArray[1])||text.equalsIgnoreCase(dataArray[1])) {
	 * System.out.println("Second file read"+ text);
	 * System.out.println("Test is passed.............."); status = "Pass";
	 * 
	 * } } else { System.out.println(text); for (int index = 1; index
	 * <dataArray.length; index++) { if (!text.contains(dataArray[index])) {
	 * System.out.println("Test is passed.............."); status = "Pass"; break; }
	 * } } } } catch (NullPointerException e) { TestDriver.logger.error(
	 * "Eigher OUTPUT text file or INPUT Image is not Available-------Methode Name--textExistenceInImage"
	 * ); } catch(FileNotFoundException e){ TestDriver.logger.
	 * error("OUTPUT location is not available or output text file is not Available-------Methode Name--textExistenceInImage"
	 * ); } catch (Exception e) { e.printStackTrace(); //
	 * TestDriver.logger.error("OUTPUT location is not available or output text file
	 * // is not Available-------Methode Name--textExistenceInImage"); }
	 * 
	 * return status;
	 * 
	 * }
	 */
	public String textExistenceInImage(String object, String data) throws IOException, InterruptedException {
		MobileElement element = null;
		String status = "Fail";
		String dataArray[] = data.split(",");
		String[] objectArray = null;
		int d = dataArray.length;
		String image =null;
		// ScreenShots Folder
		image = System.getProperty("user.dir") + "/" + TestDriver.CONFIG.getProperty("screenGrabPath") + "/"
				+ ReportUtil.currentSuiteName + "/" + dataArray[0] + ".png";
		if(TestDriver.stepDescription.contains("-")){            
			String[] description = TestDriver.stepDescription.split("-");    
			TestDriver.stepDescription = description[0];
		}
		String output = System.getProperty("user.dir") + "/" + "OutPutTextFile" + "/" + TestDriver.stepDescription;
		ProcessBuilder builder;
		try {
			if(object.contains(",")) {//crop for required values and store withname of dataArray[0] name.
				objectArray = object.split(",");
				int windowHeight = driver.manage().window().getSize().getHeight();
				int windowWidth = driver.manage().window().getSize().getWidth();
				int window_Height=Integer.parseInt(TestDriver.CONFIG.getProperty("Window_Height"));
				int window_Width=Integer.parseInt(TestDriver.CONFIG.getProperty("Window_Width"));
				int window_13Inch_Height=Integer.parseInt(TestDriver.CONFIG.getProperty("window_13Inch_height"));
				int window_13Inch_Width=Integer.parseInt(TestDriver.CONFIG.getProperty("window_13Inch_width"));

				if (windowHeight == window_Height  && windowWidth == window_Width) {
					// For 10 Inches screen
					TestDriver.logger.info("Currently you  are working in 10 Inch display screen");
					String bounds[]=TestDriver.UIMap.getProperty(objectArray[1]).split(",");
					int imageStrating_x_value=Integer.parseInt(bounds[0]);
					int imageStrating_y_value=Integer.parseInt(bounds[1]);
					int image_Height=Integer.parseInt(bounds[3]);
					int image_Width=Integer.parseInt(bounds[2]);
					BufferedImage    crpImage = ImageIO.read(new File(image)).getSubimage(imageStrating_x_value,imageStrating_y_value,image_Width,image_Height);
					String crpimage=System.getProperty("user.dir") + "/" + TestDriver.CONFIG.getProperty("screenGrabPath") + "/"
							+ ReportUtil.currentSuiteName + "/" + dataArray[0]+"_Cropped" + ".png";
					image=crpimage;
					ImageIO.write(crpImage, "png", new File(image));
					Thread.sleep(3000);

				} else if(windowHeight == window_13Inch_Height  && windowWidth == window_13Inch_Width){
					// For 13 Inches Screen
					TestDriver.logger.info("Currently you  are working in 13 Inch display screen");
					String bounds[]=TestDriver.UIMap.getProperty(objectArray[2]).split(",");
					int imageStrating_13Inch_x_value=Integer.parseInt(bounds[0]);
					int imageStrating_13Inch_y_value=Integer.parseInt(bounds[1]);
					int image_13Inch_Height=Integer.parseInt(bounds[3]);
					int image_13Inch_Width=Integer.parseInt(bounds[2]);
					//Thread.sleep(3000);

					//System.out.println(image+" path");
					BufferedImage crpImage = ImageIO.read(new File(image)).getSubimage(imageStrating_13Inch_x_value,imageStrating_13Inch_y_value,image_13Inch_Width,image_13Inch_Height);
					String crpimage=System.getProperty("user.dir") + "/" + TestDriver.CONFIG.getProperty("screenGrabPath") + "/"
							+ ReportUtil.currentSuiteName + "/" + dataArray[0]+"_Cropped" + ".png";
					image=crpimage;
					ImageIO.write(crpImage, "png", new File(image));
					Thread.sleep(3000);
				}

				//builder=new ProcessBuilder("cmd.exe", "/c", "tesseract " + crpimage + " " + output);
			}
			builder = new ProcessBuilder("cmd.exe", "/c", "tesseract " + image + " " + output);

			builder.redirectErrorStream(true);
			Process p = builder.start();
			BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			Thread.sleep(4000);
			File file = new File(output + ".txt");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String text;
			while ((text = br.readLine()) != null) {
				// process the line
				System.out.println("frist file read" + text + text.length());
				System.out.println("inputdata" + dataArray[1] + dataArray[1].length());
				if (object.equalsIgnoreCase("Existence")) {
					if(dataArray[1].equalsIgnoreCase("FM")||dataArray[1].equalsIgnoreCase("AM")||dataArray[1].equalsIgnoreCase("No Audio Source Selected")) {
						if (text.contains("FM")||text.equalsIgnoreCase("FM")||text.contains("AM")||text.equalsIgnoreCase("AM")||text.contains("No Audio Source Selected")||text.equalsIgnoreCase("No Audio Source Selected")) {
							System.out.println(text);
							status = "Pass";

						}
					}
					else if (text.contains(dataArray[1])||text.equalsIgnoreCase(dataArray[1])) {
						System.out.println("Second file read"+ text);
						System.out.println("Test is passed..............");
						status = "Pass";

					}				
				} else {
					System.out.println(text);
					for (int index = 1; index <dataArray.length; index++) {
						if (!text.contains(dataArray[index])) {
							System.out.println("Test is passed..............");
							status = "Pass";
							break;
						}
					}
				}
			}
		} catch (NullPointerException e) {
			TestDriver.logger.error(
					"Eigher OUTPUT text file or INPUT Image is not Available-------Methode Name--textExistenceInImage");
		} /*
		 * catch(FileNotFoundException e){ TestDriver.logger.
		 * error("OUTPUT location is not available or output text file is not Available-------Methode Name--textExistenceInImage"
		 * ); }
		 */catch (Exception e) {
			 e.printStackTrace();
			 // TestDriver.logger.error("OUTPUT location is not available or output text file
			 // is not Available-------Methode Name--textExistenceInImage");
		 }

		return status;

	}



	/*****************************************************************************************************
	 * @function horizontalSwipe() Swipe Left or Right
	 * @param String object Locator of Element
	 * @param String data Mention which direction to be move
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ******************************************************************************************************/
	public String horizontalSwipe(String object, String data) throws InterruptedException {

		String Status = "Fail";
		MobileElement progressBar;

		try {
			if (data != null) {
				TouchAction touchAction = new TouchAction(driver);
				// Dimension size = driver.manage().window().getSize();
				progressBar = driver.findElementById(TestDriver.UIMap.getProperty(object));
				int startX = progressBar.getLocation().getX();
				int startY = progressBar.getLocation().getY();
				int PBwidth = progressBar.getSize().getWidth();

				if (data.equalsIgnoreCase("Right")) {
					touchAction.press(PointOption.point(PBwidth, startY)).waitAction(waitOptions(ofMillis(2000)))
							.moveTo(PointOption.point(startX, startY)).release().perform();
					Status = "Pass";
				} else {
					touchAction.press(PointOption.point(startX, startY)).waitAction(waitOptions(ofMillis(2000)))
							.moveTo(PointOption.point(PBwidth, startY)).release().perform();
					Status = "Pass";

				}
			} else {
				TestDriver.logger.error("Data should be Pass as Eighter RIGHT or LEFT");
			}
		} catch (NoSuchElementException e) {
			TestDriver.logger
					.error("AndroidDriver couldn’t locate the element, Name of current method: horizontalSwipe");
		} catch (Exception e) {
			// TestDriver.logger.error("AndroidDriver couldn’t locate the
			// element, Name of
			// current method: scrollHorizental");
			e.printStackTrace();
		}
		return Status;

	}

	/**********************************************************************
	 * @function verifySentCanMsg() if already sent signal is not received by
	 *           device, will send OffSignal and then OnSignal again
	 * @param String object will send 3 parameters Separated by ","....
	 *               1)MobileElement 2)OFFSignal 3)ONSignal
	 * @param String data null
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ************************************************************************/
	public String verifySentCanMsg(String object, String data) throws InterruptedException, IOException {
		MobileElement element = null;
		String status = "Fail";
		String str[] = object.split(",");
		if (isElementExist(str[0], null).equalsIgnoreCase("Pass")) {
			status = "Pass";
			;
		} else {
			for (int index = 1; index < str.length; index++) {
				sendCanMsg(str[index], null);
				Thread.sleep(2000);
			}
			status = "Pass";
		}
		return status;
	}

	/********************************************************************
	 * @function verifybothDataAreNotSame() Verify the both data is same or not
	 *
	 * @param String object -->Element of Xpath/Id
	 *
	 * @param String data --> null
	 *
	 * @return String status Flag to indicate Pass or Fail
	 ********************************************************************/
	public String verifybothDataAreNotSame(String object, String data) {
		String status = "Fail";
		String dynamicdata;
		try {
			if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
				dynamicdata = driver.findElementByXPath(TestDriver.UIMap.getProperty(object)).getText();
				if (!dynamicdata.equalsIgnoreCase(saveText)) {
					status = "Pass";
				}
			} else {
				dynamicdata = driver.findElementById(TestDriver.UIMap.getProperty(object)).getText();
				if (!dynamicdata.equalsIgnoreCase(saveText)) {
					status = "Pass";
				}

			}
		} catch (NoSuchElementException e) {
			TestDriver.logger.error(
					"AndroidDriver couldn’t locate the element, Name of current method: verifybothDataAreNotSame");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/**************************************************************************************
	 * @function waitForInvisibilityOfElement() Waits until the element is disappear
	 * @param String object Locator of the element
	 * @param String data null
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ***************************************************************************************/
	public String waitForInvisibilityOfElement(String object, String data) {

		String status = "Fail";
		try {
			if (TestDriver.UIMap.getProperty(object).startsWith("//")) {
				(new WebDriverWait(driver, 20)).until(ExpectedConditions
						.invisibilityOfElementLocated(By.xpath(TestDriver.UIMap.getProperty(object))));
				status = "Pass";
			} else {
				(new WebDriverWait(driver, 20)).until(
						ExpectedConditions.invisibilityOfElementLocated(By.id(TestDriver.UIMap.getProperty(object))));
				status = "Pass";
			}
		} catch (NoSuchElementException e) {
			TestDriver.logger.error(
					"AndroidDriver couldn’t locate the element, Name of current method: waitForInvisibilityOfElement.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/**********************************************************
	 * @function swipeHorizentally()It will Swipe and click on device.
	 * 
	 * @param String object Locator of the Icon/Element to click with given
	 *               id/xpath.
	 * 
	 * @param String data Text of the Element.
	 * 
	 * @return [Flag] status Flag to indicate Pass or Fail
	 ***************************************************************/

	public String swipeHorizentally(String object, String data) throws InterruptedException {
		String status = "Fail";
		try {
			String locator = TestDriver.UIMap.getProperty(object);

			/*
			 * driver.findElement(MobileBy.
			 * AndroidUIAutomator("new UiScrollable(new UiSelector().resourceId(\"" +
			 * locator + "\")).setAsHorizontalList().scrollIntoView(" +
			 * "new UiSelector().text(\"" + data + "\"))")) .click();
			 */
			driver.findElement(MobileBy
					.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)).setAsHorizontalList()"
							+ ".scrollIntoView(new UiSelector().text(\"" + data + "\"))"))
					.click();

			status = "Pass";
		} catch (Exception e) {
			System.out.println(e);
		}
		return status;
	}

	/**************************************************************************************
	 * @function setRequiredToggleState() To verify and change the state of Toggle
	 *           Button.
	 * @param String object Locator of the element
	 * @param String data TRUE / FALSE
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ***************************************************************************************/

	public String setRequiredToggleState(String object, String data) {
		String status = "Fail";
		if (!object.isEmpty() && !data.isEmpty()) {
			try {
				String toggleStatus = isElementChecked(object, data);
				if (toggleStatus.equals("Pass")) {
					status = "Pass";
				} else {
					clickElement(object, data);
					Thread.sleep(1000);
					toggleStatus = isElementChecked(object, data);
					if (toggleStatus.equals("Pass")) {
						status = "Pass";
					} else {
						TestDriver.logger.info("Invalid Locator");
					}
				}

			} catch (NoSuchElementException e) {
				TestDriver.logger.error(
						"AndroidDriver couldn’t locate the element, Name of current method: setRequiredToggleState");
			} catch (Exception e) {
				e.printStackTrace();

			}
		} else {
			TestDriver.logger.info("Object and data shoud not be Null");
		}

		return status;
	}

	/**************************************************************************************
	 * @function isKeyboardPresent() Verify Keyboard is present or not in screen.
	 * @param String object Null
	 * @param String data TRUE / FALSE
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ***************************************************************************************/

	public String isKeyboardPresent(String object, String data) {
		String status = "Fail";
		if (driver != null) {
			boolean flag = driver.isKeyboardShown();
			if (data.equalsIgnoreCase("TRUE")) {
				if (flag == true) {
					status = "Pass";
				}

			} else if (data.equalsIgnoreCase("FALSE")) {
				if (flag == false) {
					status = "Pass";
				}

			} else {

				TestDriver.logger.info("Data should be TRUE or FALSE");
			}
		}

		return status;
	}

	/***********************************************************************************
	 * @function isElementFocusable() Find any element is
	 *           focusable/fadeout(eg.Select Time Zone check box, etc).
	 * @param String object Locator the element to validate using id /xpath.
	 * @param String data passing data as true/false
	 * @return [Flag] status Flag To indicate Pass or Fail
	 **************************************************************************************/
	public String isElementFocusable(String object, String data) {
		String status = "Fail";

		try {
			if (TestDriver.UIMap.getProperty(object).startsWith("//")) {

				if (driver.findElementByXPath(TestDriver.UIMap.getProperty(object)).getAttribute("focusable")
						.equalsIgnoreCase(data)) {

					status = "Pass";
				}
			} else {
				if (driver.findElementById(TestDriver.UIMap.getProperty(object)).getAttribute("focusable")
						.equalsIgnoreCase(data)) {
					status = "Pass";
				}
			}
		} catch (NoSuchElementException e) {
			TestDriver.logger
					.error("AndroidDriver couldn’t locate the element, Name of current method: isElementFocusable");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/*********************************************************************************
	 * @function clickByScroll() Scroll and Click the required element in the list
	 *           of elements
	 * @param String object null
	 * @param String data Name of the element to click.
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ***********************************************************************************/

	public String clickByScroll(String object, String data) {
		String status = "Fail";

		try {
			driver.findElementByAndroidUIAutomator(
					"new UiScrollable(" + "new UiSelector().scrollable(true)).scrollIntoView("
							+ "new UiSelector().text(\"" + data + "\"));")
					.click();
			status = "Pass";
		} catch (NoSuchElementException e) {
			TestDriver.logger
					.error("AndroidDriver couldn�t locate the element, Name of current method: clickByScroll ");
		} catch (Exception e) {
			e.printStackTrace();

		}

		return status;
	}

	/***********************************************************************************
	 * @function clickOnCoordinates() Click on the elments based on the x y
	 *           cordinades
	 * @param String object x cordinate value
	 * @param String data Y cordinate value
	 * @return [Flag] status Flag To indicate Pass or Fail
	 **************************************************************************************/
	public String clickOnCoordinates(String object, String data) throws InterruptedException {
		String status = "Fail";
		String[] coordinatepath = object.split(",");

		Thread.sleep(2000);
		try {
			TouchAction a2 = new TouchAction(driver);
			int window_Height = Integer.parseInt(TestDriver.CONFIG.getProperty("Window_Height"));
			int window_Width = Integer.parseInt(TestDriver.CONFIG.getProperty("Window_Width"));
			int window_13Inch_Height = Integer.parseInt(TestDriver.CONFIG.getProperty("window_13Inch_height"));
			int window_13Inch_Width = Integer.parseInt(TestDriver.CONFIG.getProperty("window_13Inch_width"));
			int windowHeight = driver.manage().window().getSize().getHeight();
			int windowWidth = driver.manage().window().getSize().getWidth();
			if (windowHeight == window_Height && windowWidth == window_Width) {
				// For 10 Inches screen
				System.out.println("From 10 inch loop");
				TestDriver.logger.info("Currently you are working on 10 Inch screen");
				sendCanMsg(coordinatepath[0], data);
				status = "Pass";
			} else if (windowHeight == window_13Inch_Height && windowWidth == window_13Inch_Width) {
				// For 13 Inches Screen
				TestDriver.logger.info("Currently you are working on 13 Inch screen");
				sendCanMsg(coordinatepath[1], data);
				status = "Pass";
			} else {
				TestDriver.logger.error(
						"The currentscreen is not matching with 10 or 13 inch screens : Methode Name : clickOnCoordinates()");
			}
		} catch (NumberFormatException nfe) {
			TestDriver.logger
					.error("Can't perform numerical operations on Strings :  Methode Name : clickOnCoordinates()");
		} catch (NullPointerException npe) {
			TestDriver.logger.error(
					"Values amy not available to keys in CONFIG.properties :  Methode Name : clickOnCoordinates()");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/*****************************************************************************************************
	 * @function horizentalSwipe() Swipe Left or Right
	 * @param String object Locator of Element
	 * @param String data Mention which direction to be move
	 * @return [Flag] status Flag To indicate Pass or Fail
	 ******************************************************************************************************/
	public String horizentalSwipe(String object, String data) {
		String Status = "Fail";
		MobileElement progressBar;
		try {
			if (data != null) {
				TouchAction touchAction = new TouchAction(driver);
				// Dimension size = driver.manage().window().getSize();
				progressBar = driver.findElementByXPath(TestDriver.UIMap.getProperty(object));
				int startX = progressBar.getLocation().getX();
				int startY = progressBar.getLocation().getY();
				int PBwidth = progressBar.getSize().getWidth();

				if (data.equalsIgnoreCase("Right")) {
					touchAction.press(PointOption.point(PBwidth, startY)).waitAction(waitOptions(ofMillis(2000)))
							.moveTo(PointOption.point(startX, startY)).release().perform();
					Status = "Pass";
				} else {
					touchAction.press(PointOption.point(startX, startY)).waitAction(waitOptions(ofMillis(2000)))
							.moveTo(PointOption.point(PBwidth, startY)).release().perform();
					Status = "Pass";

				}
			} else {
				TestDriver.logger.error("Data should be Pass as Eighter RIGHT or LEFT");
			}
		} catch (NoSuchElementException e) {
			TestDriver.logger
					.error("AndroidDriver couldn’t locate the element, Name of current method: horizentalSwipe");
		} catch (Exception e) {

			e.printStackTrace();
		}
		return Status;

	}

	public String verifyAndClickOnHomeElements(String object, String data) throws InterruptedException {
		String status = "Fail";
		MobileElement progressBar;
		clickBackKey(object, "Home");
		Thread.sleep(2000);
		clickBackKey(object, "Home");
		Thread.sleep(2000);
		for (int i = 0; i < 4; i++) {
			TouchAction touchAction = new TouchAction(driver);
			progressBar = driver.findElementById("com.gm.homescreen:id/rv_app_list");
			int startX = progressBar.getLocation().getX();
			int startY = progressBar.getLocation().getY();
			int PBwidth = progressBar.getSize().getWidth();
			String object1 = "//*[@resource-id='com.gm.homescreen:id/tv_app_label' and @text='" + object + "']";

			try {
				boolean flag = driver.findElementByXPath(object1).isDisplayed();
				if (flag == true && data.equalsIgnoreCase("click")) {
					driver.findElementByXPath(object1).click();

				}
				if (flag) {
					status = "Pass";
					break;
				}
			} catch (NoSuchElementException e) {
				touchAction.press(PointOption.point(PBwidth, startY)).waitAction(waitOptions(ofMillis(2000)))
						.moveTo(PointOption.point(startX, startY)).release().perform();
				Thread.sleep(2000);

			}
		}

		return status;
	}
}