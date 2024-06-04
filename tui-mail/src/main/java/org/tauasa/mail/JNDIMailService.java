/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
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
