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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Helper class for working with Servlet requests and responses. This class wraps and provides 
 * convenience methods the {@link ServletContext}, {@link HttpServletRequest} and 
 * {@link HttpServletResponse} objects 
 * 
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class ServletHelper extends HttpServletResponseHelper{

	public static final String ENCODING = "UTF-8";

	protected ServletContext context;
	private long startTime;
	/**
	 * List of {@link String}s that can be used to marshal simple messages
	 * during HTTP request processing
	 * */
	private List<String> errors, warnings, infos, successes, debugs;

	/**
	 * {@link Map} of arbitrary objects that can be used to transport named attributes
	 * around during an HTTP request. This map is functionally equivalent to managing
	 * named attributes in the {@link HttpServletRequest} in that they do not persist
	 * beyond the request.
	 * */
	private Map<String, Object> attributes;

	public ServletHelper(ServletContext context, HttpServletRequest req, HttpServletResponse res) {
		super(req, res);
		this.context = context;
		initStartTime();
	}

	@Override
	public void sendRedirect(String url, boolean encode) throws IOException {
		if(context!=null && url.startsWith("/")){
			String path = context.getContextPath();
			if(!url.startsWith(path) && !path.endsWith("/")){
				url = path + url;
			}
		}
		super.sendRedirect(url, encode);
	}

	@Override
	public Object getAttribute(String name){
		if(attributes==null){
			return null;
		}
		return attributes.get(name);
	}

	public <T> T getAttribute(String name, Class<T> type){
		Object obj = getAttribute(name);
		return obj==null ? null : type.cast(obj);
	}

	@Override
	public void setAttribute(String name, Object value){
		if(attributes==null){
			attributes = new HashMap<String, Object>();
		}
		attributes.put(name, value);
	}

	public Object removeAttribute(String name){
		if(attributes==null){
			return null;
		}
		return attributes.remove(name);
	}

	public Object getSessionAttribute(String name){
		HttpSession session = getSession();
		if(session==null){
			return null;
		}
		return session.getAttribute(name);
	}

	public <T> T getSessionAttribute(String name, Class<T> type, T defaultValue){
		T value = getSessionAttribute(name, type);
		if(value==null){
			setSessionAttribute(name, defaultValue);
		}
		return value==null ? defaultValue : value;
	}

	public <T> T getSessionAttribute(String name, Class<T> type){
		HttpSession session = getSession();
		if(session==null){
			return null;
		}
		Object obj = session.getAttribute(name);
		return obj==null ? null : type.cast(obj);
	}

	public void setSessionAttribute(String name, Object value){
		HttpSession session = getSession();
		if(session==null){
			return;
		}
		session.setAttribute(name, value);
	}

	public void removeSessionAttribute(String name){
		HttpSession session = getSession();
		if(session==null){
			return;
		}
		session.removeAttribute(name);
	}

	public Object getRequestAttribute(String name){
		return getRequest().getAttribute(name);
	}

	public <T> T getRequestAttribute(String name, Class<T> type){
		Object obj = getRequestAttribute(name);
		return obj==null ? null : type.cast(obj);
	}

	public void setRequestAttribute(String name, Object value){
		getRequest().setAttribute(name, value);
	}

	public void removeRequestAttribute(String name){
		getRequest().removeAttribute(name);
	}

	public Object getContextAttribute(String name){
		return context==null ? null : context.getAttribute(name);
	}

	public <T> T getContextAttribute(String name, Class<T> type){
		Object obj = getContextAttribute(name);
		return obj==null ? null : type.cast(obj);
	}

	public void setContextAttribute(String name, Object value){
		if(context==null){
			return;
		}
		context.setAttribute(name, value);
	}

	public void removeContextAttribute(String name){
		if(context==null){
			return;
		}
		context.removeAttribute(name);
	}

	public void initStartTime(){
		startTime = System.currentTimeMillis();
	}

	public void addInfo(String info){
		if(infos==null){
			infos = new ArrayList<String>();
		}
		infos.add(info);
	}

	public List<String> getInfos(){
		return infos;
	}

	public void addSuccess(String success){
		if(successes==null){
			successes = new ArrayList<String>();
		}
		successes.add(success);
	}

	public List<String> getSuccesses(){
		return successes;
	}

	public void addWarning(String warning){
		if(warnings==null){
			warnings = new ArrayList<String>();
		}
		warnings.add(warning);
	}

	public List<String> getWarnings(){
		return warnings;
	}

	public void addError(Throwable t){
		addError(t.getMessage());
	}

	public void addErrors(List<String> list){
		if(errors==null){
			errors = list;
		}else{
			errors.addAll(list);
		}
	}

	public void addWarnings(List<String> list){
		if(warnings==null){
			warnings = list;
		}else{
			warnings.addAll(list);
		}
	}

	public void addInfos(List<String> list){
		if(infos==null){
			infos = list;
		}else{
			infos.addAll(list);
		}
	}

	public void addSuccesses(List<String> list){
		if(successes==null){
			successes = list;
		}else{
			successes.addAll(list);
		}
	}

	public void addError(String error){
		if(errors==null){
			errors = new ArrayList<String>();
		}
		errors.add(error);
	}

	public List<String> getErrors(){
		return errors;
	}

	public void addDebug(Throwable t){
		addDebug(t.getMessage());
	}

	public void addDebug(String debug){
		if(debugs==null){
			debugs = new ArrayList<String>();
		}
		debugs.add(debug);
	}

	public List<String> getDebugs(){
		return debugs;
	}

	/**
	 * Returns the timestamp (in milliseconds) that this helper was created
	 * */
	public long getStartTime(){
		return startTime;
	}

	/**
	 * Returns the age of this helper in milliseconds
	 * */
	public long getElapsedTime(){
		return System.currentTimeMillis() - startTime;
	}

	public ServletContext getServletContext() {
		return context;
	}

	/**
	 * Using a {@link RequestDispatcher}, forwards the request to the specified path
	 * */
	public void forward(String path) throws IOException, ServletException {
		createRequestDispatcher(path).forward(req, res);
	}

	/**
	 * Using a {@link RequestDispatcher}, includes the resource at the specified path
	 * */
	public void include(String path) throws IOException, ServletException {
		createRequestDispatcher(path).include(req, res);
	}

	/**
	 * URL encodes the specified string
	 */
	public static String encode(String str) throws UnsupportedEncodingException {
		return URLEncoder.encode(str, ENCODING);
	}

	/**
	 * Decodes the specified URL-encoded string
	 */
	public static String decode(String str) throws UnsupportedEncodingException {
		return URLDecoder.decode(str, ENCODING);
	}

	public ServletContext getContext() {
		return context;
	}

	public void setContext(ServletContext context) {
		this.context = context;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public void setWarnings(List<String> warnings) {
		this.warnings = warnings;
	}

	public void setInfos(List<String> infos) {
		this.infos = infos;
	}

	public void setSuccesses(List<String> successes) {
		this.successes = successes;
	}



	public Map<String, Object> getAttributes() {
		return attributes;
	}



	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public void setDebugs(List<String> debugs) {
		this.debugs = debugs;
	}


}



