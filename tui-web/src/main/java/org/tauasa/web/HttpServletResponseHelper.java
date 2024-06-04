/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.web;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Helper class for working with an {@link HttpServletResponse}
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class HttpServletResponseHelper extends HttpServletRequestHelper{

	protected HttpServletResponse res;

	public HttpServletResponseHelper(HttpServletRequest req, HttpServletResponse res){
		super(req);
		this.res=res;
	}

	public HttpServletResponse getResponse(){
		return res;
	}

	public void setHeader(String key, String value) {
		res.setHeader(key, value);
	}

	public void setContentDisposition(String value) {
		setHeader("content-disposition", value);
	}

	public void sendRedirect(String url) throws IOException {
		sendRedirect(url, true);
	}

	public void sendRedirect(String url, boolean encode) throws IOException {
		res.sendRedirect(encode ? encodeRedirectURL(url) : url);
	}

	public String encodeRedirectURL(String url) {
		return res.encodeRedirectURL(url);
	}

	public void setStatus(int status){
		res.setStatus(status);
	}

	public void sendError(int sc)throws IOException{
		res.sendError(sc);
	}

	public void sendError(int sc, String msg)throws IOException{
		res.sendError(sc, msg);
	}

	public ServletOutputStream getOutputStream() throws IOException {
		return res.getOutputStream();
	}

	public void setContentType(String contentType) {
		res.setContentType(contentType);
	}

}
