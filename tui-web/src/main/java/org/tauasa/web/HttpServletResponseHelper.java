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
