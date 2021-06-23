/**************************************************
*File Name:-TestResults.java
*This class is implemented is POJO class(Plain old java object) 
*which has the properties to hold the values of the test results.
********************************************************/
package com.gm.core;

public class TestResults {
	private String testSuite;
	private String testCaseName;
	private String testCaseDesc;
	private String userStory;
	private String testStartTime;
	private String testEndTime;
	private String testStatus;
	private String errorMsg;
	private String isAuto;
	private String priority;
	private int manualExeTime;

	public TestResults(String suite, String name, String desc, String us, String startTime, String endTime,
			String status, String errorMsg, String isAuto, String priority, int manualExeTime) {
		this.testSuite = suite;
		this.testCaseName = name;
		this.testCaseDesc = desc;
		this.userStory = us;
		this.testStartTime = startTime;
		this.testEndTime = endTime;
		this.testStatus = status;
		this.errorMsg = errorMsg;
		this.isAuto = isAuto;
		this.priority = priority;
		this.manualExeTime = manualExeTime;
	}

	public String getSuite() {
		return this.testSuite;
	}

	public String getTCName() {
		return this.testCaseName;
	}

	public String getTCDesc() {
		return this.testCaseDesc;
	}

	public String getUS() {
		return this.userStory;
	}

	public String getStartTime() {
		return this.testStartTime;
	}

	public String getEndTime() {
		return this.testEndTime;
	}

	public String getStatus() {
		return this.testStatus;
	}

	public String isAutomated() {
		return this.isAuto;
	}

	public String getPriority() {
		return this.priority;
	}

	public String getErrorMsg() {
		return this.errorMsg;
	}

	public int getManualExeTime() {
		return this.manualExeTime;
	}
}
