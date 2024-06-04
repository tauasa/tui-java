/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.mail;

import java.util.List;

import javax.mail.MessagingException;

/**
 * Mail service contract
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public interface IMailService {

	/**
	 * Closes any open resources
	 * */
	public void close();

	/**
	 * Sends the specified EmailMessage
	 * @param message the message to send
	 * */
	public void sendMail(EmailMessage message)throws MessagingException;
	
	/**
	 * Sends the specified EmailMessage
	 * @param message the message to send
	 * */
	public void sendMail(List<String> recipients, EmailMessage template)throws MessagingException;

	/**
	 * Sends the specified array of EmailMessage
	 * @param messages an array of messages to send
	 * */
	public void sendMail(EmailMessage[] messages)throws MessagingException;

	/**
	 * Sends the specified collection of EmailMessage
	 * @param messages a {@link List} of messages to send
	 * */
	public void sendMail(List<EmailMessage> messages)throws MessagingException;

}
