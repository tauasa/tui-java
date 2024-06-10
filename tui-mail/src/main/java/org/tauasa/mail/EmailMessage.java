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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
 * @author Tauasa Timoteo
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
		this.from=from;
		this.subject=subject;
		this.message=message;
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
			headers = new HashMap<>();
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
			attachments = new ArrayList<>();
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
			for (String key : headers.keySet()) {
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
			recipients = new ArrayList<>();
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
		for (String addresse : addresses) {
			addTo(addresse);
		}
	}

	public void addCC(String address)throws AddressException{
		addRecipient(new Recipient(address, Message.RecipientType.CC));
	}

	public void addCC(String[] addresses)throws AddressException{
		if(addresses==null){
			return;
		}
		for (String addresse : addresses) {
			addCC(addresse);
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
