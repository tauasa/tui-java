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

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tauasa.commons.net.Localhost;
import org.tauasa.commons.util.PatternUtils;
import org.tauasa.commons.util.Utils;
import org.tauasa.mail.EmailMessage;
import org.tauasa.mail.JNDIMailService;
import org.tauasa.web.ServletHelper;

/** 
 * Sends a test email using the {@link JNDIMailService}
 *
 * @author Tauasa Timoteo
 * 
 */
public class JNDIMailServiceSmoketest extends AbstractJNDISmoketest {

	private static final Logger logger = LoggerFactory.getLogger(JNDIMailServiceSmoketest.class);

	public static final String SENDER_KEY = "sender";
	public static final String RECIPIENTS_KEY = "recipients";
	public static final String SUBJECT_KEY = "subject";

	protected String sender;
	protected String[] recipients;
	protected String subject;

	public JNDIMailServiceSmoketest() {
	}

	/**
	 * @param delegate
	 */
	public JNDIMailServiceSmoketest(LookupDelegate delegate) {
		super(delegate);
	}

	@Override
	public void doExecute(ServletHelper helper) throws Exception {

		String appName = helper.getServletContext().getContextPath().replace("/", "");

		if(Boolean.getBoolean(appName+".JNDIMailServiceSmoketest.disabled")){
			addWarningMessage("This smoke test was disabled by the system property \""+appName+".JNDIMailServiceSmoketest.disabled=true\"");
			return;
		}

		if(Utils.isEmpty(recipients)){
			throw new Exception("No recipients specified in "+getClass().getSimpleName()+"."+RECIPIENTS_KEY);
		}

		if(!PatternUtils.isEmail(sender)){
			throw new Exception(sender+" is not a valid email. A valid email must be specified in "+getClass().getSimpleName()+"."+SENDER_KEY);
		}

		addInfoMessage("Recipients: "+Utils.join(recipients, ", "));
		addInfoMessage("Sender: "+sender);
		addInfoMessage("Subject: "+subject);

		EmailMessage msg = new EmailMessage();
		msg.addTo(recipients);
		msg.setSubject(subject);
		msg.setFrom(sender);
		msg.setMessage(String.format("This is a test message from %s / %s", Localhost.getHostName(), Localhost.getHostAddress()));

		addInfoMessage("Sending test email to "+recipients.length+" recipients");

		for (String jndiName : jndiNames) {
			JNDIMailService service = null;
			try{
				addInfoMessage("Creating JNDIMailService using "+jndiName);
				service = new JNDIMailService(jndiName);
				service.sendMail(msg);
				addInfoMessage("Mail sent through "+jndiName);
			}catch(Exception e){
				addErrorMessage(jndiName, new Exception("Test message failed for "+jndiName+": "+e.getMessage()));
			}finally{
				if(service!=null)
					service.close();
			}
		}


	}

	@Override
	public void init(Properties smoketestProperties)throws SmoketestConfigException{
		super.init(smoketestProperties);

		getLogger().info("Getting recipients...");

		recipients = getArrayProperty(smoketestProperties,
				RECIPIENTS_KEY,
				DEFAULT_DELIMITER,
				null);

		if(recipients==null){
			getLogger().info("Recipients: NONE");
		}else{
			getLogger().info("Recipients:\r\n"+Utils.join(recipients, "\r\n"));
		}

		sender = getProperty(smoketestProperties, SENDER_KEY);
		getLogger().info("Sender: "+sender);

		subject = getProperty(smoketestProperties, SUBJECT_KEY);
		//TODO - replace ${x} with corresponding System.getProperty(x)
		if(Utils.isEmpty(subject)){
			subject = getClass().getSimpleName();
		}
		subject += String.format("[%s/%s]", Localhost.getHostName(), Localhost.getHostAddress());
		getLogger().info("Subject: "+subject);

	}

	@Override
	public String getDescription() {
		if(description!=null){//default to value defined in smoketest.properties
			return description;
		}
		//jndiNames
		return "Performs a JNDI lookup on the " + Utils.join(jndiNames, ", ") + " JavaMail Session" + (jndiNames.length > 1 ? "s" : "") + " and sends a test email";
	}

	@Override
	public Logger getLogger() {
		return logger;
	}



}
