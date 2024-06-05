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

import org.tauasa.commons.util.Utils;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;

/**
 * Abstract {@link ISmoketest} impl for testing JNDI resources
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public abstract class AbstractJNDISmoketest extends AbstractSmoketest {

	protected static final String DEFAULT_DELIMITER = ",";

	public static final String JNDI_NAMES_DELIMITER_KEY = "jndi.names.delimiter";
	public static final String JNDI_NAMES_KEY = "jndi.names";

	protected String[] jndiNames;


	protected LookupDelegate mLookupDelegate = new LookupDelegate(){
		private Context mContext;
		Logger log = getLogger();
		public Object lookup(String jndiName) throws NamingException {
			if(log.isInfoEnabled()){
				log.info(String.format("Performing lookup on %s", jndiName));
			}

			return getContext().lookup(jndiName);
		}
		public Context getContext()throws NamingException{
			if(mContext==null){
				if(log.isInfoEnabled()){
					log.info("Creating new InitialContext");
				}
				mContext = new InitialContext();
			}
			return mContext;
		}
	};

	public AbstractJNDISmoketest() {

	}

	public AbstractJNDISmoketest(LookupDelegate delegate) {
		setLookupDelegate(delegate);
	}

	/**
	 * Initializes the local jndiNames attribute
	 * */
	@Override
	public void init(Properties smoketestProperties)throws SmoketestConfigException{
		super.init(smoketestProperties);
		if(getLogger().isInfoEnabled()){
			getLogger().info("Getting JNDI Names...");
		}

		jndiNames = getArrayProperty(smoketestProperties,
				JNDI_NAMES_KEY,
				getProperty(smoketestProperties, JNDI_NAMES_DELIMITER_KEY, DEFAULT_DELIMITER),
				null);
		if(getLogger().isInfoEnabled()){
			getLogger().info(String.format("JNDI Names:\r\n%s", Utils.join(jndiNames, "\r\n")));
		}
	}

	public String[] getJndiNames(){
		return jndiNames;
	}

	public boolean isVerbose(){
		return verbose;
	}

	public LookupDelegate getLookupDelegate() {
		return mLookupDelegate;
	}

	public void setLookupDelegate(LookupDelegate lookupDelegate) {
		this.mLookupDelegate = lookupDelegate;
	}

	/**
	 * Performs a JNDI lookup for the given name using this class' LookupDelegate
	 * */
	protected Object lookup(String jndiName)throws NamingException{
		if(getLogger().isInfoEnabled()){
			getLogger().info("Looking up "+jndiName);
		}
		return mLookupDelegate.lookup(jndiName);
	}

	public static interface LookupDelegate{

		public Context getContext()throws NamingException;

		public Object lookup(String jndiName)throws NamingException;

	}

}
