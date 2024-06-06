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
package org.tauasa.commons.jdbc;

import org.tauasa.commons.util.XProperties;

import java.util.Properties;


/**
 * {@link XProperties} subclass used for basic JDBC properties (Driver, URL, user credentials, etc)
 *
 * @author Tauasa Timoteo
 * 
 */
public class JDBCProperties extends XProperties {

	private static final long serialVersionUID = 1L;

	public static final String DRIVER    	= "jdbc.driver";
	public static final String URL       	= "jdbc.url";
	public static final String USER      	= "jdbc.user";
	public static final String PASS      	= "jdbc.pass";
	public static final String INIT      	= "jdbc.init";
	public static final String MAX       	= "jdbc.max";
	public static final String TIMEOUT   	= "jdbc.timeout";
	public static final String AUTO_COMMIT  = "jdbc.auto.commit";
	public static final String TRACE     	= "jdbc.trace";

	public JDBCProperties() {
		super();
	}

	public JDBCProperties(Properties defaults) {
		super(defaults);
	}

	public String getPassword() {return getProperty(PASS);}
	public void setPassword(String pass){setProperty(PASS, pass);}

	public String getUser() {return getProperty(USER);}
	public void setUser(String dbUser) {setProperty(USER, dbUser);}

	public String getDriver() {return getProperty(DRIVER);}
	public void setDriver(String driver) {setProperty(DRIVER, driver);}

	public String getUrl(){return getProperty(URL);}
	public void setUrl(String url){setProperty(URL, url);}

	public int getMaxConnections(){return getIntProperty(MAX);}
	public void setMaxConnections(int i){setIntProperty(MAX, i);}

	public int getInitConnections(){return getIntProperty(INIT);}
	public void setInitConnections(int i){setIntProperty(INIT, i);}

	public int getTimeout(){return getIntProperty(TIMEOUT);}
	public void setTimeout(int i){setIntProperty(TIMEOUT, i);}

	public boolean isAutoCommit(){return getBooleanProperty(AUTO_COMMIT, true);}
	public void setAutoCommit(boolean b){setBooleanProperty(AUTO_COMMIT, b);}

	public boolean isTrace(){return getBooleanProperty(TRACE, false);}
	public void setTrace(boolean b){setBooleanProperty(TRACE, b);}

}
