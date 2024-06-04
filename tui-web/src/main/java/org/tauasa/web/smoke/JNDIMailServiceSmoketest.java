/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.web.smoke;

import org.tauasa.commons.net.Localhost;
import org.tauasa.commons.util.PatternUtils;
import org.tauasa.commons.util.Utils;
import org.tauasa.mail.EmailMessage;
import org.tauasa.mail.JNDIMailService;
import org.tauasa.web.ServletHelper;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * Sends a test email using the {@link JNDIMailService}
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
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
		subject += " ["+Localhost.getHostName() + "/" + Localhost.getHostAddress()+"]";
		getLogger().info("Subject: "+subject);


	}

	@Override
	public String getDescription() {
		if(description!=null){//default to value defined in smoketest.properties
			return description;
		}
		//jndiNames
		StringBuilder b = new StringBuilder()
		.append("Performs a JNDI lookup on the ")
		.append(Utils.join(jndiNames, ", "))
		.append(" JavaMail Session").append(jndiNames.length > 1 ? "s" : "")
		.append(" and sends a test email");
		return b.toString();
	}

	@Override
	public Logger getLogger() {
		return logger;
	}



}
