/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
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
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
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
		this(new InternetAddress(emailAddress, personal), type);
	}

	public Recipient(String emailAddress)throws AddressException{
		this(new InternetAddress(emailAddress));
	}

	public Recipient(String emailAddress, String personal)throws AddressException, UnsupportedEncodingException{
		this(new InternetAddress(emailAddress, personal));
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
