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

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * An {@link EmailMessage} recipient
 *
 * @author Tauasa Timoteo
 * 
 */
public class Recipient implements Serializable {

	private static final long serialVersionUID = 1L;
	protected Message.RecipientType type;
	protected Address address;

	public Recipient(Address address) {
		setAddress(address);
		setType(Message.RecipientType.TO);
	}

	public Recipient(Address address, Message.RecipientType type) {
		this(address);
		setType(type);
	}

	public Recipient(String emailAddress, Message.RecipientType type)throws AddressException{
		this(new InternetAddress(emailAddress), type);
	}

	public Recipient(String emailAddress, String personal, Message.RecipientType type)throws AddressException, UnsupportedEncodingException{
		this(new InternetAddress(emailAddress, singleLine(personal)), type);
	}

	public Recipient(String emailAddress)throws AddressException{
		this(new InternetAddress(emailAddress));
	}

	public Recipient(String emailAddress, String personal)throws AddressException, UnsupportedEncodingException{
		this(new InternetAddress(emailAddress, singleLine(personal)));
	}

	/**
	 * Refuses a display name with a carriage return or line feed in it, which would
	 * let the rest of the name add mail headers of its own, such as a Bcc
	 * */
	static String singleLine(String personal)throws AddressException{
		if(personal!=null && (personal.indexOf('\r')>=0 || personal.indexOf('\n')>=0)){
			throw new AddressException("The recipient name contains a line break, which could inject mail headers");
		}
		return personal;
	}

	public Address getAddress() {
		return address;
	}

	public Message.RecipientType getType() {
		return type;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public void setType(Message.RecipientType type) {
		this.type = type;
	}

}
