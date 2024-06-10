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

import org.tauasa.commons.util.DateUtils;
import org.tauasa.commons.util.Utils;
import org.tauasa.web.ServletHelper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract {@link ISmoketest} implementation. This class takes care of setting the start and end
 * times of of the test and provides a reference to an appropriate {@link Logger} via the {@link #getLogger()}
 * method. Concrete implementations just need to implement the {@link #doExecute(ServletHelper)} method.
 *
 * @author Tauasa Timoteo
 * 
 */
public abstract class AbstractSmoketest implements ISmoketest {

	private static final String DEFAULT_DATE_FORMAT = "MM/dd/yyyy";

	public static final String VERBOSE_KEY = "verbose";
	public static final String STARTS_KEY = "starts";
	public static final String STARTS_FORMAT_KEY = "starts.format";
	public static final String EXPIRES_KEY = "expires";
	public static final String EXPIRES_FORMAT_KEY = "expires.format";
	public static final String POC_KEY = "poc";
	public static final String DESCRIPTION_KEY = "description";
	private static final String UNKNOWN_POC = "N/A";

	protected SmoketestResult result = new SmoketestResult(this);
	protected boolean verbose = false;
	protected Date starts, expires;
	protected String pointOfContact, description;

	public AbstractSmoketest() {

	}

	/**
	 * Returns the point-of-contact (usually an email) for the application being smoke tested. If this
	 * method is not overridden progrmatically then the system property <code>smoketest.poc</code> is used.
	 * If that system property is not defined then the string literal {@value #UNKNOWN_POC} is returned
	 * */
        @Override
	public String getPointOfContact(){
		return pointOfContact!=null ? pointOfContact : System.getProperty("smoketest.poc", UNKNOWN_POC);
	}

        @Override
	public String getDescription(){
		return description;
	}

	/**
	 * Returns the logger for the implementing test
	 * */
        @Override
	public Logger getLogger(){
		return LoggerFactory.getLogger(getClass());
	}

        @Override
	public void cleanUp() {
		if(getLogger().isInfoEnabled()){
			getLogger().info("Cleaning up");
		}
	}

        @Override
	public SmoketestResult getResult() {
		return result;
	}

	/**
	 * Returns true if the current date/time is falls between starts/expires
	 * */
        @Override
	public boolean isActive(){
		Date now = new Date();
		boolean goodStart = true;
		boolean goodEnd = true;
		if(starts!=null){
			goodStart = starts.before(now);//starts in the past
		}
		if(expires!=null){
			goodEnd = expires.after(now);//expires in the future
		}
		return goodStart && goodEnd;
	}

	public Date getStarts() {
		return starts;
	}

	public void setStarts(Date starts) {
		this.starts=starts;
	}

	public Date getExpires() {
		return expires;
	}

	public void setExpires(Date expires) {
		this.expires=expires;
	}

	/**
	 * Adds the smoketest-specific config values to this instance's results member
	 * */
        @Override
	public void init(Properties smoketestProperties) throws SmoketestConfigException {
		getLogger().info("Initializing");

		//set POC and description
		pointOfContact = getProperty(smoketestProperties, POC_KEY, null);
		getLogger().info("POC: "+pointOfContact);

		description = getProperty(smoketestProperties, DESCRIPTION_KEY, null);
		getLogger().info("Description: "+description);

		//set the verbose attribute
		verbose = getBooleanProperty(smoketestProperties, VERBOSE_KEY, true);
		getLogger().info("Verbose: "+verbose);

		try{
			getLogger().info("Getting start date...");
			starts = getDateProperty(smoketestProperties, STARTS_KEY,
					getProperty(smoketestProperties, STARTS_FORMAT_KEY, DEFAULT_DATE_FORMAT),
					DateUtils.adjustDate(new Date(), -1));//default to yesterday
			getLogger().info("Start date: "+starts);
		}catch(ParseException e){
			throw new SmoketestConfigException("Could not parse start date", e, result);
		}

		try{
			getLogger().info("Getting expires date...");
			expires = getDateProperty(smoketestProperties, EXPIRES_KEY,
					getProperty(smoketestProperties, EXPIRES_FORMAT_KEY, DEFAULT_DATE_FORMAT),
					DateUtils.adjustDate(new Date(), 1));//default to tomorrow
			getLogger().info("Expires: "+starts);
		}catch(ParseException e){
			throw new SmoketestConfigException("Could not parse expiration date", e, result);
		}

		//if verbose we need to add test-specific configuration params (from
		//smoktest.properties) to our results
		if(verbose){
			List<String> propertyNames = getPropertyNames(smoketestProperties);

			if(!Utils.isEmpty(propertyNames)){

				StringBuilder b = new StringBuilder();
				for (String name : propertyNames) {
					String val = smoketestProperties.getProperty(name);
					b.append(name).append("=").append(val==null?"":val).append("\r\n");
				}

				//give this message a special key so it sticks out to the renderer
				//result.add(new SmoketestMessage("config", b.toString(), new SmoketestMessage.PreFormattedTextFormatter()));
				result.add(new SmoketestMessage(b.toString(), new SmoketestMessage.HTMLPropertiesFormatter(getClass().getSimpleName()+" Properties")));
			}
		}

	}

	/**
	 * Override this method. An invocation of this smoketest will be considered a failure
	 * if, and only if, this method throws an exception
	 * */
	public abstract void doExecute(ServletHelper helper)throws Exception;

	/**
	 * Invokes <code>doExecute(ServletHelper)</code> and
	 * marks the end of the test. Any Throwable thrown from <code>doExecute(ServletHelper)</code>
	 * is re-thrown as a SmoketestException containing any results
	 * @throws org.tauasa.web.smoke.SmoketestException which wraps the root cause and contains the results
	 * */
        @Override
	public final void execute(ServletHelper helper)throws SmoketestException{
		getLogger().info("Executing smoketest");
		try{
			doExecute(helper);
		}catch(Exception t){
			getLogger().warn("Smoketest failed", t);
			throw new SmoketestException(getClass().getSimpleName()+" failed", t, result);
		}finally{
			//mark the end of the test
			getLogger().info("Marking smoketest end time");
			result.setTestEndTime(new Date());
		}
	}

	////////////////////////////////////////
	// Convenience methods
	////////////////////////////////////////
	/**
	 * Convenience method for adding a new message to the results. This method also
	 * logs the message at the {@link Level#DEBUG} if debug logging is enabled
	 * */
	protected void addMessage(SmoketestMessage msg){
		result.add(msg);
		if(msg.isErrorMessage() || msg.isWarningMessage()){
			if(msg.getError()!=null){
				getLogger().warn(msg.getStringValue(), msg.getError());
			}else{
				getLogger().warn(msg.getStringValue());
			}
		}else if(getLogger().isDebugEnabled()){
			getLogger().debug(msg.getStringValue());
		}
	}

	protected void addInfoMessage(String msg){
		addMessage(new SmoketestMessage(msg, SmoketestMessage.Type.INFO));
	}

	protected void addWarningMessage(String msg){
		addMessage(new SmoketestMessage(msg, SmoketestMessage.Type.WARN));
	}

	protected void addWarningMessage(String msg, Throwable error){
		addMessage(new SmoketestMessage(msg, SmoketestMessage.Type.WARN, error));
	}

	protected void addWarningMessage(Throwable error){
		addMessage(new SmoketestMessage(SmoketestMessage.Type.WARN, error));
	}

	protected void addErrorMessage(String msg){
		addMessage(new SmoketestMessage(msg, SmoketestMessage.Type.ERROR));
	}

	protected void addErrorMessage(String msg, Throwable error){
		addMessage(new SmoketestMessage(msg, SmoketestMessage.Type.ERROR, error));
	}

	protected void addErrorMessage(Throwable error){
		addMessage(new SmoketestMessage(SmoketestMessage.Type.ERROR, error));
	}

	protected void addMessage(String msg, SmoketestMessage.Type type){
		addMessage(new SmoketestMessage(msg, type));
	}

	protected String getProperty(Properties props, String key){
		return getProperty(props, key, null);
	}

	protected int getIntProperty(Properties props, String key, int defaultValue){
		return Integer.parseInt(getProperty(props, key, String.valueOf(defaultValue)));
	}

	protected boolean getBooleanProperty(Properties props, String key, boolean defaultValue){
		return Boolean.parseBoolean(getProperty(props, key, String.valueOf(defaultValue)));
	}

	protected Date getDateProperty(Properties props, String key, String format, Date defaultValue)throws ParseException{
		String strDate = getProperty(props, key, null);
		if(strDate==null){
			return defaultValue;
		}
		return DateUtils.parseDate(strDate, format);
	}

	protected String[] getArrayProperty(Properties props, String key, String delimiter, String defaultValue){
		String s = getProperty(props, key, defaultValue);
		if(s==null){
			return null;
		}
		return Utils.split(s, delimiter);
	}

	protected String getProperty(Properties props, String key, String defaultValue){
		return props.getProperty(getClass().getSimpleName()+"."+key, defaultValue);
	}

	/**
	 * Returns the property names specific to this smoketest (names that start with
	 * the simple class name and a ".")
	 * */
	protected List<String> getPropertyNames(Properties props){
		String pattern = getClass().getSimpleName()+".";

		Iterator<Object> keys = props.keySet().iterator();

		ArrayList<String> names = new ArrayList<>();

		while(keys.hasNext()){
			String key = keys.next().toString();
			if(key.startsWith(pattern)){
				names.add(key);
			}
		}
		//sort the names
		Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
		return names;
	}


}
