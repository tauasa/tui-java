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
	private final IMailService service;

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
