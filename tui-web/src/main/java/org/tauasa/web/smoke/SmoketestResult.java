/*
 * Copyright 2012 Tauasa Timoteo
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation 
 * files (the “Software”), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-
 * INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN 
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF 
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 */
package org.tauasa.web.smoke;

import org.tauasa.web.smoke.SmoketestMessage.Type;

import java.util.ArrayList;
import java.util.Date;

/**
 * Typed list containing the {@link SmoketestMessage} results of the {@link ISmoketest} as well as the start and end time
 * of the test
 *
 * @author Tauasa Timoteo
 * 
 */
public class SmoketestResult extends ArrayList<SmoketestMessage> {

	private static final long serialVersionUID = 1L;
	private ISmoketest smokeTest;
	private final Date testStartTime = new Date();
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
