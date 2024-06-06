/*
 * Copyright 2012 Tauasa Timoteo
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this 
 * software and associated documentation files (the “Software”), to deal in 
 * the Software without restriction, including without limitation the rights to use, 
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of 
 * the Software, and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE 
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT 
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR 
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package org.tauasa.web.smoke;

import org.tauasa.web.ServletHelper;

import java.util.Properties;

import javax.servlet.ServletConfig;

import org.slf4j.Logger;

/**
 * Smoke test contract
 *
 * @author Tauasa Timoteo
 * 
 */
public interface ISmoketest {

	/**
	 * Returns a description of this smoketest or null there is none
	 * */
	public String getDescription();

	/**
	 * Identifies a Point-of-Contact for this smoketest (typically the name or email address
	 * of the author) so s/he may be verbally assaulted when the implementation
	 * blows up and annoys innocent by-standers. This method may return null
	 * if the author wishes to maintain anonymity, avoid ridicule from his peers,
	 * or remain employed. T
	 * */
	public String getPointOfContact();

	/**
	 * Returns the log4j {@link Logger} for this smoketest
	 * */
	public Logger getLogger();

	/**
	 * Returns true if this smoketest is currently active
	 * */
	public boolean isActive();

	/**
	 * Initializes this smoketest from the specified {@link ServletConfig}
	 * */
	public void init(Properties properties)throws SmoketestConfigException;

	/**
	 * Executes this smoketest and populates the list of results
	 * */
	public void execute(ServletHelper helper)throws SmoketestException;

	/**
	 * Returns the results of this smoketest or null if there are no results.
	 * THIS METHOD MUST BE RECURRENT.
	 * */
	public SmoketestResult getResult();

	/**
	 * Performs any clean up required by this smoketest
	 * */
	public void cleanUp();


}
