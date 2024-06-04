/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.web.smoke;

import org.tauasa.web.ServletHelper;

import java.util.Properties;

import javax.servlet.ServletConfig;

import org.slf4j.Logger;

/**
 * Smoke test contract
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
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
