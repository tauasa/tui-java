/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Singleton factory for creating a {@link IMailService}. The implementation used can be defined
 * via the {@code mail.service.class} system property
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class MailServiceFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(MailServiceFactory.class);

	public static final String SYSTEM_PROPERTY_KEY = "mail.service.class";
	public static final String DEFAULT_MAIL_SERVICE_CLASS = SMTPMailService.class.getName();

	private static MailServiceFactory instance;
	private IMailService service;

	private MailServiceFactory()throws Exception{
		//load the appropriate mail service
		//defined as a system property named "mail.service.class"
		String clazz = System.getProperty(SYSTEM_PROPERTY_KEY, DEFAULT_MAIL_SERVICE_CLASS);
		if(logger.isDebugEnabled()){
			logger.debug("Creating new instance of "+clazz);
		}
		service = (IMailService)Class.forName(clazz).newInstance();
	}

	public static MailServiceFactory getInstance()throws Exception{
		if(instance==null){
			if(logger.isDebugEnabled()){
				logger.debug("Creating new MailServiceFactory");
			}
			instance = new MailServiceFactory();
		}
		return instance;
	}

	public IMailService getMailService(){
		return service;
	}

}
