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

import java.util.Arrays;
import java.util.List;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tauasa.commons.util.Utils;

/** 
 * Abstract and thread-safe {@link IMailService} implementation that stores a JavaMail {@link Session}
 * in a {@link ThreadLocal} attribute.
 *
 * @author Tauasa Timoteo
 * 
 */
public abstract class AbstractMailService implements IMailService {

	private static final Logger logger = LoggerFactory.getLogger(AbstractMailService.class);

	private static final ThreadLocal<Session> sessionLocal = new ThreadLocal<>();

	public AbstractMailService() {

	}

	
    @Override
	public void close(){
		if(logger.isDebugEnabled()){
			logger.debug("Releasing ThreadLocal resources");
		}
		sessionLocal.remove();
	}

	protected Session getLocalSession()throws MessagingException{
		Session session = sessionLocal.get();
		if(session==null){
			if(logger.isDebugEnabled()){
				logger.debug("Creating new mail session");
			}
			session = createSession();
			sessionLocal.set(session);
		}
		return session;
	}

	/**
	 * Creates a JavaMail {@link Session}
	 * */
	public abstract Session createSession()throws MessagingException;
	
	static class EmailResultListener implements TransportListener{

		@Override
		public void messageDelivered(TransportEvent event) {
			//Message has been successfully delivered to all recipients by the transport firing this event. 
			//validSent[] contains all the addresses this transport sent to successfully. 
			//validUnsent[] and invalid[] should be null
			
			//TODO - flag the email as delivered
			//TODO - flag validSent[] addresses as valid
		}

		@Override
		public void messageNotDelivered(TransportEvent event) {
			//Message was not sent for some reason. validSent[] should be null. 
			//validUnsent[] may have addresses that are valid (but the message wasn't sent to them). 
			//invalid[] should likely contain invalid addresses.
			
			//TODO - flag the email as not delivered for any validUnsent[]
			//TODO - flag invalid[] as bogus email addresses
		}

		@Override
		public void messagePartiallyDelivered(TransportEvent event) {
			//Message was successfully sent to some recipients but not to all. 
			//validSent[] holds addresses of recipients to whom the message was sent. 
			//validUnsent[] holds valid addresses to which the message was not sent. 
			//invalid[] holds invalid addresses, if any.
			
			//TODO - flag the email as delivered for validSent[]
			//TODO - flag the email as not delivered for validUnsent[]
			//TODO - flag invalid[] as bogus email addresses
		}
		
	}

	@Override
	public void sendMail(EmailMessage email) throws MessagingException {
		if(logger.isDebugEnabled()){
			logger.debug(String.format("Sending email from %s [Subject: %s] ", email.getFrom(), email.getSubject()));
		}
		
		//TODO - perform send in a separate thread
		
		Session session = getLocalSession();
		Transport trans = session.getTransport();
		trans.addTransportListener(new EmailResultListener());
		Message msg = email.toMessage(session);
		Transport.send(msg);
	}
	
	@Override
	public void sendMail(List<String> recipients, EmailMessage template)throws MessagingException{
		if(Utils.isEmpty(recipients)){
			throw new IllegalArgumentException("No recipients specified");
		}else if(template==null){
			throw new IllegalArgumentException("Email template is null");
		}
		if(logger.isDebugEnabled()){
			logger.debug(String.format("Sending email to %d recipients", recipients.size()));
		}
		
		Message msg = template.toMessage(getLocalSession());
		Transport.send(msg, toAddressArray(recipients));
	}
	
	static Address[] toAddressArray(List<String> rcpts)throws AddressException{
		Address[] arr = new Address[rcpts.size()];
		for(int i=0;i<rcpts.size();i++){
			arr[i]= new InternetAddress(rcpts.get(i));
		}
		return arr;
	}

	@Override
	public void sendMail(EmailMessage[] emails) throws MessagingException {
		if(emails==null){
			return;
		}
		sendMail(Arrays.asList(emails));
	}

	@Override
	public void sendMail(List<EmailMessage> emails) throws MessagingException {
		if(emails==null){
			return;
		}

		if(logger.isDebugEnabled()){
			logger.debug(String.format("Sending %d emails", emails.size()));
		}

		for (EmailMessage e : emails) {
			sendMail(e);
		}

	}

}







