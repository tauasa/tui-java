/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.mail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**
 * Simple POJO representing an email message
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class EmailMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String TEXT = "plain";
	public static final String HTML = "html";

	protected String from;
	protected String subject;
	protected String message;
	protected List<Recipient> recipients;
	protected String contentType = TEXT;
	protected List<EmailAttachment> attachments;
	protected String charEncoding;
	protected Map<String, String> headers;

	public EmailMessage() {
		super();
	}
	
	public EmailMessage(EmailMessage template, String to)throws AddressException{
		this.setFrom(template.getFrom());
		this.setSubject(template.getSubject());
		this.setMessage(template.getMessage());
		this.setContentType(template.getContentType());
		this.setAttachments(template.getAttachments());
		this.setCharEncoding(template.getCharEncoding());
		this.setHeaders(template.getHeaders());
		this.setRecipients(template.getRecipients());
		this.addTo(to);
	}

	protected EmailMessage(String from, String subject, String message) {
		setFrom(from);
		setSubject(subject);
		setMessage(message);
	}

	public EmailMessage(String to, String from, String subject, String message)throws AddressException{
		this(from, subject, message);
		if(to!=null){
			addTo(to);
		}
	}

	public EmailMessage(String[] to, String[] cc, String[] bcc, String from, String subject, String message)throws AddressException{
		this(from, subject, message);
		addTo(to);
		addCC(cc);
		addBCC(bcc);
	}

	public void addHeader(String name, String value){
		if(headers==null){
			headers = new HashMap<String, String>();
		}
		headers.put(name, value);
	}

	public List<EmailAttachment> getAttachments(){
		return attachments;
	}

	public void setAttachments(List<EmailAttachment> attachments){
		this.attachments=attachments;
	}

	public void addAttachment(EmailAttachment attachment){
		if(attachments==null){
			attachments = new ArrayList<EmailAttachment>();
		}
		attachments.add(attachment);
	}

	/**
	 * Using the specified JavaMail {@link Session}, creates a JavaMail {@link Message}
	 * from this object's attributes
	 * */
	public Message toMessage(Session session)throws MessagingException{
		Message msg = new MimeMessage(session);
		//set the subject
		msg.setSubject(subject);
		msg.setSentDate(new Date());
		msg.setFrom(new InternetAddress(from));

		if(headers!=null){
			Iterator<String> keys = headers.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				String value = headers.get(key);
				msg.setHeader(key, value);
			}
		}

		for(Recipient rcpt : recipients){
			msg.addRecipient(rcpt.getType(), rcpt.getAddress());
		}

		//create a body part to hold the message
		MimeBodyPart mbp = new MimeBodyPart();
		mbp.setText(message, charEncoding==null?MimeUtility.getDefaultJavaCharset():charEncoding, contentType);

		Multipart mp = new MimeMultipart();
		mp.addBodyPart(mbp);

		//add any attachments
		if(attachments!=null){
			for (EmailAttachment a : attachments) {
				mp.addBodyPart(a.getMimeBodyPart());
			}
		}

		msg.setContent(mp);

		return msg;
	}

	public void addRecipient(Recipient rcpt){
		if(recipients==null){
			recipients = new ArrayList<Recipient>();
		}
		recipients.add(rcpt);
	}

	public void addTo(String address)throws AddressException{
		addRecipient(new Recipient(address));
	}

	public void addTo(String[] addresses)throws AddressException{
		if(addresses==null){
			return;
		}
		for(int i=0;i<addresses.length;i++){
			addTo(addresses[i]);
		}
	}

	public void addCC(String address)throws AddressException{
		addRecipient(new Recipient(address, Message.RecipientType.CC));
	}

	public void addCC(String[] addresses)throws AddressException{
		if(addresses==null){
			return;
		}
		for(int i=0;i<addresses.length;i++){
			addCC(addresses[i]);
		}
	}

	public void addBCC(String address)throws AddressException{
		addRecipient(new Recipient(address, Message.RecipientType.BCC));
	}

	public void addBCC(String[] addresses)throws AddressException{
		if(addresses==null){
			return;
		}
		for(int i=0;i<addresses.length;i++){
			addBCC(addresses[i]);
		}
	}

	public List<Recipient> getRecipients(){
		return recipients;
	}

	public void setRecipients(List<Recipient> list) {
		recipients = list;
	}
	
	public Map<String, String> getHeaders(){
		return headers;
	}
	
	public void setHeaders(Map<String, String> headers){
		this.headers=headers;
	}

	public String getFrom() {
		return from;
	}

	public String getMessage() {
		return message;
	}

	public String getSubject() {
		return subject;
	}

	public void setFrom(String string) {
		from = string;
	}

	public void setMessage(String string) {
		message = string;
	}

	public void setSubject(String string) {
		subject = string;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String string) {
		contentType = string;
	}

	public String getCharEncoding() {
		return charEncoding;
	}

	public void setCharEncoding(String charEncoding) {
		this.charEncoding = charEncoding;
	}

}
