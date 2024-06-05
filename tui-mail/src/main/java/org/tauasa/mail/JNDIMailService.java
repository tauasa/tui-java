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
package org.tauasa.mail;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * {@link IMailService} implementation backed by a JNDI JavaMail {@link Session}
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class JNDIMailService extends AbstractMailService {

	private static final Logger logger = LoggerFactory.getLogger(JNDIMailService.class);

	public static final String DEFAULT_JNDI_NAME = "java:/Mail";
	public static final String SYSTEM_PROPERTY_KEY = "mail.jndi.name";

	protected String jndiName;

	public JNDIMailService()throws Exception{
		jndiName = System.getProperty(SYSTEM_PROPERTY_KEY, DEFAULT_JNDI_NAME);
	}

	public JNDIMailService(String jndiName)throws Exception{
		this.jndiName=jndiName;
	}

	/**
	 * Creates a new {@link InitialContext} and returns the {@link Session} 
	 * located at {@code jndiName}
	 * */
	@Override
	public Session createSession()throws MessagingException{
		if(logger.isDebugEnabled()){
			logger.debug(String.format("Using JNDI mail session %s", jndiName));
		}
		try{
			InitialContext c = new InitialContext();
			return (Session)c.lookup(jndiName);
		}catch(Exception e){
			throw new MessagingException(e.getMessage());
		}

	}

}
