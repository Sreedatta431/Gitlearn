/**************************************************
*File Name:-Testcase.java
*This class is implemented is POJO (Plain old java object)class 
*which has the properties of the column headings of testcases sheet of excel to get the one testcase at a time.
********************************************************/
package com.gm.core;

public class TestCase {
	private String testCaseName;
	private String usName;
	private String testCaseDesc;
	private String runMode;
	private String dependency;
	private String status;
	private String priority;
	private int manualExeTime;
	private String zehyrId;

	public TestCase(String name, String us, String desc, String runMode, String dependency, String status,
			String priority, int manualExeTime, String zehyrId) {
		this.testCaseName = name;
		this.usName = us;
		this.testCaseDesc = desc;
		this.runMode = runMode;
		this.dependency = dependency;
		this.status = status;
		this.priority = priority;
		this.manualExeTime = manualExeTime;
		this.zehyrId = zehyrId;
	}

	public String getTCName() {
		return this.testCaseName;
	}

	public String getUSName() {
		return this.usName;
	}

	public String getTCDesc() {
		return this.testCaseDesc;
	}

	public String getRunMode() {
		return this.runMode;
	}

	public String getDependency() {
		return this.dependency;
	}

	public String getStatus() {
		return this.status;
	}

	public String getPriority() {
		return this.priority;
	}

	public int getManualExeTime() {
		return this.manualExeTime;
	}

	public String getZephyrId() {
		return this.zehyrId;
	}
}
