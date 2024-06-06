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

import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tauasa.commons.util.XProperties;

/** 
 * SMTP {@link IMailService} implementation backed by a JavaMail {@link Session}.
 * <p/>
 * The SMTP session can be configured via following system properties (the complete list of 
 * properties is available in the 
 * <a href="https://javamail.java.net/nonav/docs/api/index.html?com/sun/mail/smtp/package-summary.html">com.sun.mail.smtp JavaDoc package summary</a>): 
 * <ul>
 * <ol><b>mail.smtp.user</b> - Default username for SMTP</ol>
 * <ol><b>mail.smtp.from</b> - Email address to use for the {@code SMTP MAIL} command</ol>
 * <ol><b>mail.smtp.host</b> - The SMTP server to connect to</ol>
 * <ol><b>mail.smtp.port</b> - The SMTP server port (defaults to 25)</ol>
 * <ol><b>mail.smtp.connectiontimeout</b> - Socket connection timeout value in milliseconds (default is infinite)</ol>
 * <ol><b>mail.smtp.timeout</b> - Socket read timeout value in milliseconds (default is infinite)</ol>
 * <ol><b>mail.smtp.writetimeout</b> - Socket write timeout value in milliseconds (default is infinite)</ol>
 * </ul>
 *
 * @author Tauasa Timoteo
 * 
 */
public final class SMTPMailService extends AbstractMailService{

	private static final Logger logger = LoggerFactory.getLogger(SMTPMailService.class);
	
	public static enum Property{
		USER("mail.smtp.user","no-reply@tauasa.org"),
		FROM("mail.smtp.from","no-reply@tauasa.org"),
		HOST("mail.smtp.host","smtp.tauasa.org"),
		PORT("mail.smtp.port","25"),
		CONNECTION_TIMEOUT("mail.smtp.connectiontimeout","2000"),
		READ_TIMEOUT("mail.smtp.timeout","1000"),
		WRITE_TIMEOUT("mail.smtp.writetimeout","1000");
		String key, def;
		Property(String key, String def){
			this.key=key;
			this.def=def;
		}
		public String key(){
			return key;
		}
		public String def(){
			return def;
		}
		public int defInt(){
			return Integer.parseInt(def);
		}
		public String asString(){
			return System.getProperty(key, def);
		}
		public int asInt(){
			return Integer.parseInt(System.getProperty(key, def));
		}
	}
	
	private String user;
	private String from;
	private String host;
	private int port;
	private int connectionTimeoutMs;
	private int readTimeoutMs;
	private int writeTimeoutMs;

	public SMTPMailService() {
		
	}
	
	public SMTPMailService(String host) {
		setHost(host);
	}

	/**
	 * Creates a {@link Properties} object from {@link System#getProperties()}, puts  
	 * this service's attributes keyed under the appropriate {@link Property} and ultimately 
	 * invokes {@link Session#getDefaultInstance(Properties)}
	 * */
	@Override
	public Session createSession()throws MessagingException{
		if(logger.isDebugEnabled()){
			logger.debug(String.format("Creating JavaMail Session on host %s", host));
		}
		//get system properties
		Properties props = System.getProperties();
		
		//decorate with our attributes or reasonable default
		props.put(Property.USER.key(), user!=null?user:Property.USER.def());
		props.put(Property.FROM.key(), from!=null?from:Property.FROM.def());
		props.put(Property.HOST.key(), host!=null?host:Property.HOST.def());
		props.put(Property.PORT.key(), port>0?port:Property.PORT.defInt());
		props.put(Property.CONNECTION_TIMEOUT.key(), connectionTimeoutMs>0?connectionTimeoutMs:Property.CONNECTION_TIMEOUT.defInt());
		props.put(Property.READ_TIMEOUT.key(), readTimeoutMs>0?readTimeoutMs:Property.READ_TIMEOUT.defInt());
		props.put(Property.WRITE_TIMEOUT.key(), writeTimeoutMs>0?writeTimeoutMs:Property.WRITE_TIMEOUT.defInt());
		
		if(logger.isDebugEnabled()){
			List<String> mailProps = XProperties.getMatchingKeys(props, "mail.smtp{1}[\\.\\w]*");
			logger.debug("Foud "+mailProps.size()+" smtp properties");
			for (String prop : mailProps) {
				logger.debug(prop+"="+props.getProperty(prop));
			}
		}
		
		return Session.getDefaultInstance(props);
	}

	
	public String getUser() {
		return user;
	}

	
	public void setUser(String user) {
		this.user = user;
	}

	
	public String getFrom() {
		return from;
	}

	
	public void setFrom(String from) {
		this.from = from;
	}

	
	public int getPort() {
		return port;
	}

	
	public void setPort(int port) {
		this.port = port;
	}

	
	public int getConnectionTimeoutMs() {
		return connectionTimeoutMs;
	}

	
	public void setConnectionTimeoutMs(int connectionTimeoutMs) {
		this.connectionTimeoutMs = connectionTimeoutMs;
	}

	
	public int getReadTimeoutMs() {
		return readTimeoutMs;
	}

	
	public void setReadTimeoutMs(int readTimeoutMs) {
		this.readTimeoutMs = readTimeoutMs;
	}

	
	public int getWriteTimeoutMs() {
		return writeTimeoutMs;
	}

	
	public void setWriteTimeoutMs(int writeTimeoutMs) {
		this.writeTimeoutMs = writeTimeoutMs;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String string) {
		host = string;
	}

}
