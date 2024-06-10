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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;

/**
 * An email attachment backed by a {@link MimeBodyPart}
 *
 * @author Tauasa Timoteo
 * 
 */
public class EmailAttachment implements Serializable {

	private static final long serialVersionUID = 1L;
	private MimeBodyPart mbp;

	/**
	 * Create an EmailAttachment from the specified File
	 * */
	public EmailAttachment(File file)throws MessagingException{
		this(file, file.getName());
	}

	/**
	 * Create an EmailAttachment from the specified File that is attached as the specified name
	 * */
	public EmailAttachment(File file, String attachmentName)throws MessagingException{
		mbp = new MimeBodyPart();
		DataSource source = new FileDataSource(file);
		mbp.setDataHandler(new DataHandler(source));
		mbp.setFileName(attachmentName);
	}

	/**
	 * Create an EmailAttachment from the specified String that is attached as the specified
	 * name and MIME type
	 * */
	public EmailAttachment(String content, String attachmentName, String mimeType)throws IOException, MessagingException{
		this(content.getBytes(), attachmentName, mimeType);
	}

	/**
	 * Create an EmailAttachment from the specified byte array that is attached as the specified
	 * name and MIME type
	 * */
	public EmailAttachment(byte[] content, String attachmentName, String mimeType)throws IOException, MessagingException{
		this(new ByteArrayInputStream(content), attachmentName, mimeType);
	}

	/**
	 * Create an EmailAttachment from the specified InputStream that is attached as the specified
	 * name and MIME type
	 * */
	public EmailAttachment(InputStream in, String attachmentName, String mimeType)throws IOException, MessagingException{
		mbp = new MimeBodyPart();
		DataSource source = new ByteArrayDataSource(in, mimeType);
		mbp.setDataHandler(new DataHandler(source));
		mbp.setFileName(attachmentName);
	}

	/**
	 * Return MimeBodyPart wrapped by the EmailAttachment instance
	 * */
	public MimeBodyPart getMimeBodyPart(){
		return mbp;
	}
}
