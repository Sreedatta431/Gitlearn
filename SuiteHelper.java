/**************************************************
*File NameSuiteHelper.java
*This class is implemented as to get the “Test Steps” and “Testcases” from excel into java list.
********************************************************/
package com.gm.core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.gm.utils.XlHelper;
import com.google.common.base.Strings;

public class SuiteHelper {
	public List<TestSteps> tsList;

	public SuiteHelper() {
	}

	public SuiteHelper(XlHelper suiteXLS) {
		this.tsList = this.getSteps(suiteXLS);
	}
	
	/****************************************************************
	 * @function getSuite()--> Get the information of Module and Run mode from Suite.xlsx
	 * 
	 * @param XlHelper suiteXLS -->Suite.xlsx sheet
	 * 
	 * @return LinkedHashMap<String, String> ---> List of Modules and run modes in Suite.xlsx
	 * ***************************************************************/
												//Suite sheet as a object
	public LinkedHashMap<String, String> getSuite(XlHelper suiteXLS) {
		LinkedHashMap<String, String> suiteMap = new LinkedHashMap();

		for (int currentSuiteID = 2; currentSuiteID <= suiteXLS
				.getRowCount(Constants.TEST_SUITE_SHEET); ++currentSuiteID) {
			String currentTestSuite = suiteXLS.getCellData(Constants.TEST_SUITE_SHEET, Constants.Test_Suite_ID,
					currentSuiteID);
			String runMode = suiteXLS.getCellData(Constants.TEST_SUITE_SHEET, Constants.RUNMODE, currentSuiteID);
			suiteMap.put(currentTestSuite, runMode);
		}

		return suiteMap;
	}

	
	/****************************************************************
	 * @function getTC()--> Get the information of test cases from Test case sheet
	 * 
	 * @param XlHelper suiteXLS -->Test case sheet
	 * 
	 * @return List<TestCase> ---> List of test cases in Test case sheet
	 * ***************************************************************/
	public List<TestCase> getTC(XlHelper currentTestSuiteXLS) {
		List<TestCase> tcList = new ArrayList();
		String zephyrId = "0";

		for (int currentTestCaseID = 2; currentTestCaseID <= currentTestSuiteXLS
				.getRowCount("Test Cases"); ++currentTestCaseID) {
			String currentTestCaseName = currentTestSuiteXLS
					.getCellData(Constants.TEST_CASES_SHEET, Constants.TCID, currentTestCaseID).trim();
			String currentUS = currentTestSuiteXLS.getCellData(Constants.TEST_CASES_SHEET, "USID", currentTestCaseID)
					.trim();
			String currentTestCaseDesc = currentTestSuiteXLS
					.getCellData(Constants.TEST_CASES_SHEET, "Description", currentTestCaseID).trim();
			String runMode = currentTestSuiteXLS
					.getCellData(Constants.TEST_CASES_SHEET, Constants.RUNMODE, currentTestCaseID).trim();
			String dependency = currentTestSuiteXLS
					.getCellData(Constants.TEST_CASES_SHEET, "Dependency", currentTestCaseID).trim();
			String status = currentTestSuiteXLS.getCellData(Constants.TEST_CASES_SHEET, "Status", currentTestCaseID)
					.trim();
			String priority = currentTestSuiteXLS.getCellData(Constants.TEST_CASES_SHEET, "Priority", currentTestCaseID)
					.trim();
			String manualExeTimeS = currentTestSuiteXLS
					.getCellData(Constants.TEST_CASES_SHEET, "ManualExeTime", currentTestCaseID).trim();
			int manualExeTime;
			if (!manualExeTimeS.equals("") && !manualExeTimeS.equals((Object) null)) {
				manualExeTime = Integer.parseInt(manualExeTimeS);
			} else {
				manualExeTime = 5;
			}

			if (Strings.isNullOrEmpty(priority)) {
				priority = "P1";
			}

			zephyrId = currentTestSuiteXLS.getCellData(Constants.TEST_CASES_SHEET, "zephyrId", currentTestCaseID)
					.trim();
			if (Strings.isNullOrEmpty(zephyrId)) {
				zephyrId = "0";
			}

			TestCase tc = new TestCase(currentTestCaseName, currentUS, currentTestCaseDesc, runMode, dependency, status,
					priority, manualExeTime, zephyrId);
			tcList.add(tc);
		}

		return tcList;
	}

	public List<String> getDataSet(XlHelper currentTestSuiteXLS, String currentTestCaseName) {
		List<String> dataSet = new ArrayList();

		for (int currentTestDataSetID = 2; currentTestDataSetID <= currentTestSuiteXLS
				.getRowCount(currentTestCaseName); ++currentTestDataSetID) {
			String runMode = currentTestSuiteXLS
					.getCellData(currentTestCaseName, Constants.RUNMODE, currentTestDataSetID).trim();
			dataSet.add(runMode);
		}

		return dataSet;
	}
	
	/****************************************************************
	 * @function getTestSteps()--> Get test steps of current running test case
	 * 
	 * @param XlHelper currentTestSuiteXLS -->Current running module sheet name(Ex:TrailerTestCases sheet when trailer is executing)
	 * @param String currentTestCaseName -->Current running test case(TSID)
	 * 
	 * @return ArrayList<ArrayList<String>> ---> List of test steps of current running test case
	 * ***************************************************************/

	public ArrayList<ArrayList<String>> getTestSteps(XlHelper currentTestSuiteXLS, String currentTestCaseName) {
		ArrayList<ArrayList<String>> testSteps = new ArrayList();

		for (int currentTestStepID = 2; currentTestStepID <= currentTestSuiteXLS
				.getRowCount(Constants.TEST_STEPS_SHEET); ++currentTestStepID) {
			ArrayList<String> data = new ArrayList();
			if (currentTestCaseName.equals(
					currentTestSuiteXLS.getCellData(Constants.TEST_STEPS_SHEET, Constants.TCID, currentTestStepID))) {
				data.add(currentTestSuiteXLS
						.getCellData(Constants.TEST_STEPS_SHEET, Constants.KEYWORD, currentTestStepID).trim());
				data.add(currentTestSuiteXLS
						.getCellData(Constants.TEST_STEPS_SHEET, Constants.OBJECT, currentTestStepID).trim());
				data.add(
						currentTestSuiteXLS.getCellData(Constants.TEST_STEPS_SHEET, Constants.DATA, currentTestStepID));
			}

			testSteps.add(data);
		}

		return testSteps;
	}

	/****************************************************************
	 * @function readTestSteps()--> Reads all steps of a test case
	 * 
	 * @param XlHelper currentTestSuiteXLS -->Current running module sheet name(Ex:TrailerTestCases sheet when trailer is executing)
	 * 
	 * @return Null
	 * ***************************************************************/
	public void readTestSteps(XlHelper currentTestSuiteXLS) {
		this.tsList = this.getSteps(currentTestSuiteXLS);
	}

	/****************************************************************
	 * @function getSteps()-->Get every step data of a test case
	 * 
	 * @param XlHelper currentTestSuiteXLS -->Current running module sheet name(Ex:TrailerTestCases sheet when trailer is executing)
	 * 
	 * @return Null
	 * ***************************************************************/
	public List<TestSteps> getSteps(XlHelper currentTestSuiteXLS) {
		List<TestSteps> tsList = new ArrayList();
		TestSteps ts = null;
		String tc = "";

		for (int i = 2; i <= currentTestSuiteXLS.getRowCount(Constants.TEST_STEPS_SHEET); ++i) {
			if (currentTestSuiteXLS.getCellData(Constants.TEST_STEPS_SHEET, Constants.TCID, i).equalsIgnoreCase(tc)) {
				ts.addTCDesc(currentTestSuiteXLS.getCellData(Constants.TEST_STEPS_SHEET, Constants.DESC, i).trim());
				ts.addKeyWord(currentTestSuiteXLS.getCellData(Constants.TEST_STEPS_SHEET, Constants.KEYWORD, i).trim());
				ts.addObject(currentTestSuiteXLS.getCellData(Constants.TEST_STEPS_SHEET, Constants.OBJECT, i).trim());
				ts.addData(currentTestSuiteXLS.getCellData(Constants.TEST_STEPS_SHEET, Constants.DATA, i));
				ts.addExecFlag(
						currentTestSuiteXLS.getCellData(Constants.TEST_STEPS_SHEET, "Proceed_on_Fail", i).trim());
				ts.addExpectedResult(currentTestSuiteXLS.getCellData(Constants.TEST_STEPS_SHEET, "ExpectedResult", i).trim());
			} else {
				if (i != 2) {
					tsList.add(ts);
				}

				tc = currentTestSuiteXLS.getCellData(Constants.TEST_STEPS_SHEET, Constants.TCID, i);
				ts = new TestSteps(tc);
				ts.addTCDesc(currentTestSuiteXLS.getCellData(Constants.TEST_STEPS_SHEET, Constants.DESC, i).trim());
				ts.addKeyWord(currentTestSuiteXLS.getCellData(Constants.TEST_STEPS_SHEET, Constants.KEYWORD, i).trim());
				ts.addObject(currentTestSuiteXLS.getCellData(Constants.TEST_STEPS_SHEET, Constants.OBJECT, i).trim());
				ts.addData(currentTestSuiteXLS.getCellData(Constants.TEST_STEPS_SHEET, Constants.DATA, i));
				ts.addExecFlag(
						currentTestSuiteXLS.getCellData(Constants.TEST_STEPS_SHEET, "Proceed_on_Fail", i).trim());
				ts.addExpectedResult(currentTestSuiteXLS.getCellData(Constants.TEST_STEPS_SHEET, "ExpectedResult", i).trim());
			}
		}

		tsList.add(ts);
		return tsList;
	}

	/****************************************************************
	 * @function getSteps()-->Get test steps of test case
	 * 
	 * @param String tcName-->test case name
	 * 
	 * @return TestSteps-->list of test steps.
	 * ***************************************************************/
	public TestSteps getTCSteps(String tcName) {
		TestSteps ts = null;
		Iterator<TestSteps> var3 = this.tsList.iterator();

		TestSteps t;
		do {
			if (!var3.hasNext()) {
				return (TestSteps) ts;
			}

			t = (TestSteps) var3.next();
		} while (!t.getTCName().equals(tcName));

		return t;
	}
}
