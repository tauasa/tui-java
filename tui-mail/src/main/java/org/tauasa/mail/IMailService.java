/*
 * Copyright 2012 Tauasa Timoteo
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation 
 * files (the “Software”), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-
 * INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN 
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF 
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 */
package org.tauasa.mail;

import java.util.List;

import javax.mail.MessagingException;

/**
 * Mail service contract
 *
 * @author Tauasa Timoteo
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
