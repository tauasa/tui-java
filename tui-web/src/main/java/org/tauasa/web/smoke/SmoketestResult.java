/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.web.smoke;

import org.tauasa.web.smoke.SmoketestMessage.Type;

import java.util.ArrayList;
import java.util.Date;

/**
 * Typed list containing the {@link SmoketestMessage} results of the {@link ISmoketest} as well as the start and end time
 * of the test
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class SmoketestResult extends ArrayList<SmoketestMessage> {

	private static final long serialVersionUID = 1L;
	private ISmoketest smokeTest;
	private Date testStartTime = new Date();
	private Date testEndTime;

	public SmoketestResult(ISmoketest _smokeTest) {
		if(_smokeTest==null){
			throw new NullPointerException("ISmoketest argument cannot be null");
		}
		setSmokeTest(_smokeTest);
	}

	/**
	 * Initializes this result with the specified message
	 * */
	public SmoketestResult(ISmoketest smokeTest, SmoketestMessage item) {
		this(smokeTest);
		add(item);
	}

	public int getNumMessagesForType(Type type){
		int count = 0;
		for (SmoketestMessage m : this) {
			if(m.getType().equals(type)){
				count++;
			}
		}
		return count;
	}

	/**
	 * Returns true if this result has any messages that:
	 * <ol>
	 * <li>Have a type attribute of Type.ERROR</li>
	 * <li>Have a non-null value that is a java.lang.Throwable</li>
	 * </ol>
	 * */
	public boolean hasErrorMessage(){
		for (SmoketestMessage m : this) {
			if(m.isErrorMessage()){
				return true;
			}
		}
		return false;
	}

	public long getElapedTime(){
		if(testEndTime==null){
			testEndTime = new Date();
		}
		return testEndTime.getTime() - testStartTime.getTime();
	}

	public Date getTestEndTime() {
		return testEndTime;
	}

	public void setTestEndTime(Date testEndTime) {
		this.testEndTime = testEndTime;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Date getTestStartTime() {
		return testStartTime;
	}

	public ISmoketest getSmokeTest() {
		return smokeTest;
	}

	public void setSmokeTest(ISmoketest smokeTest) {
		this.smokeTest = smokeTest;
	}

}
