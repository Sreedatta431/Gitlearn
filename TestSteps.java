/**************************************************
*File Name:-TestSteps.java
*This class is implemented is POJO(Plain old java object)class 
*which has the properties of the column headings of test steps sheet of excel to get the one test step at a time
********************************************************/
package com.gm.core;

import java.util.ArrayList;
import java.util.List;

public class TestSteps {
	private String tcName;
	private List<String> tcDesc = new ArrayList();
	private List<String> keywords = new ArrayList();
	private List<String> objects = new ArrayList();
	private List<String> data = new ArrayList();
	private List<String> execFlag = new ArrayList();
	private List<String> expectedResult = new ArrayList();
	
	

	public TestSteps() {
	}

	public TestSteps(String name) {
		this.tcName = name;
	}

	public void addTCDesc(String desc) {
		this.tcDesc.add(desc);
	}

	public void addKeyWord(String keyword) {
		this.keywords.add(keyword);
	}

	public void addObject(String object) {
		this.objects.add(object);
	}

	public void addData(String data) {
		this.data.add(data);
	}

	public void addExecFlag(String flag) {
		this.execFlag.add(flag);
	}
	
	public void addExpectedResult(String expResult) {
		this.expectedResult.add(expResult);
	}

	public String getTCName() {
		return this.tcName;
	}

	public List<String> getTCDesc() {
		return this.tcDesc;
	}

	public List<String> getKeywords() {
		return this.keywords;
	}

	public List<String> getObjects() {
		return this.objects;
	}

	public List<String> getData() {
		return this.data;
	}

	public List<String> getExecFlag() {
		return this.execFlag;
	}
	
	public List<String> getExpectedResult() {
		return this.expectedResult;
	}
}
