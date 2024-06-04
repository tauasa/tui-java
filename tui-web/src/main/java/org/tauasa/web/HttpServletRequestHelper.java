/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.web;

import java.math.BigDecimal;
import java.security.Principal;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.tauasa.commons.util.DateUtils;
import org.tauasa.commons.util.Utils;


/**
 * Helper class for working with an {@link HttpServletRequest}
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class HttpServletRequestHelper {

	public static final String XFF = "X-Forwarded-For";
	public static final String USER_AGENT = "user-agent";
	public static final String REFERER = "referer";
	public static final String GET = "GET";
	public static final String PUT = "PUT";
	public static final String POST = "POST";
	public static final String DELETE = "DELETE";
	public static final String SLASH = "/";
	public static final String HTTPS = "https";

	protected HttpServletRequest req;

	public HttpServletRequestHelper(HttpServletRequest req){
		this.req=req;
	}

	public static boolean isUserInRole(HttpServletRequest req, String...roles){
		if(req==null){
			throw new NullPointerException("HttpServletRequest is null");
		}
		if(Utils.isEmpty(roles)){
			//throw new IllegalArgumentException("No roles specified");
			return true;
		}
		for(String r : roles){
			if(!req.isUserInRole(r)){
				continue;
			}
			return true;
		}
		return false;
	}

	public RequestDispatcher getRequestDispatcher(String path){
		return req.getRequestDispatcher(path);
	}

	public HttpServletRequest getRequest(){
		return req;
	}

	public boolean paramExists(String name){
		return !Utils.isEmpty(req.getParameter(name));
	}

	public boolean paramsExists(String[] names, boolean all){
		for (String name : names) {
			boolean exists = paramExists(name);
			if(!all && exists){
				return true;
			}else if(all && !exists){
				return false;
			}
		}
		return all;
	}

	public boolean isUserInRole(String...roles){
		return isUserInRole(req, roles);
	}

	public boolean isUserInRole(String role){
		return req.isUserInRole(role);
	}

	public static interface Param<T>{
		public T get(HttpServletRequest req);
	}

	public <T> T getObjectParam(String p, Param<T> param){
		return param.get(req);
	}

	public Date getDateParam(String p){
		return getDateParam(p, null);
	}

	public Date getDateParam(final String p, Date def){
		if(!paramExists(p)){
			return def;
		}
		return getObjectParam(p, new Param<Date>(){
			@Override
			public Date get(HttpServletRequest req){
				try{
					return DateUtils.parseMMDDYYYY(req.getParameter(p));
				}catch(ParseException e){
					throw new RuntimeException(e);
				}
			}
		});
	}

	public String getParam(String name){
		String s = req.getParameter(name);
		return s!=null ? s.trim() : null;
	}

	public String getParam(String name, String def){
		String value = req.getParameter(name);
		if(value==null){
			return def;
		}
		return value.trim();
	}

	public int getIntParam(String name){
		return getIntParam(name, 0);
	}

	public int getIntParam(String name, int def){
		String value = getParam(name);
		if(value==null){
			return def;
		}
		return Integer.parseInt(value);
	}

	public long getLongParam(String p){
		return getLongParam(p, 0);
	}

	public long getLongParam(String name, long def){
		String value = getParam(name);
		if(value==null){
			return def;
		}
		return Long.parseLong(value);
	}

	public float getFloatParam(String p){
		return getFloatParam(p, 0F);
	}

	public float getFloatParam(String p, float def){
		String value = getParam(p);
		if(value==null){
			return def;
		}
		return Float.parseFloat(value);
	}

	public double getDoubleParam(String p){
		return getDoubleParam(getParam(p));
	}

	public double getDoubleParam(String p, double def){
		String value = getParam(p);
		if(value==null){
			return def;
		}
		return Double.parseDouble(value);
	}

	public BigDecimal getBigDecimalParam(String p){
		return getBigDecimalParam(p, null);
	}

	public BigDecimal getBigDecimalParam(String p, BigDecimal def){
		String value = getParam(p);
		if(value==null){
			return def;
		}
		return new BigDecimal(Utils.replace(value, ",", ""));
	}

	public String[] getParams(String p) {
		return req.getParameterValues(p);
	}

	public boolean[] getBooleanParams(String p) {
		return Utils.toBooleanArray(getParams(p));
	}

	public int[] getIntParams(String p) {
		return Utils.toIntArray(getParams(p));
	}

	public long[] getLongParams(String p) {
		return Utils.toLongArray(getParams(p));
	}

	public float[] getFloatParams(String p) {
		return Utils.toFloatArray(getParams(p));
	}

	public double[] getDoubleParams(String p) {
		return Utils.toDoubleArray(getParams(p));
	}

	public BigDecimal[] getBigDecimalParams(String p) {
		return Utils.toBigDecimalArray(getParams(p));
	}

	public String getHeader(String name){
		return req.getHeader(name);
	}

	public String getContentType(){
		return req.getContentType();
	}

	/**
	 * Returns the "best" remote address: returns the X-Forwarded-For header if it exists,
	 * otherwise returns the {@link HttpServletRequest#getRemoteAddr()}
	 * 
	 * */
	public String getBestRemoteAddr(){
		String xff = getXff();
		return xff==null ? req.getRemoteAddr() : xff;
	}

	/**
	 * Returns the {@code X-Forwarded-For} header. This header identifies the originating IP
	 * address of a client connecting to a web server through an HTTP proxy or load balancer.
	 * */
	public String getXff(){
		return getHeader(XFF);
	}

	public String getRemoteUser(){
		return req.getRemoteUser();
	}

	public Principal getUserPrincipal(){
		return req.getUserPrincipal();
	}

	//////////////////////////

	public Host getHostRemote(){
		return new Host(getRemoteHost(), getBestRemoteAddr(), getRemotePort());
	}

	public String getRemoteAddr(){
		return req.getRemoteAddr();
	}

	public int getRemotePort(){
		return req.getRemotePort();
	}

	public String getRemoteHost(){
		return req.getRemoteHost();
	}

	///////////////////////////

	public Host getLocalHost(){
		return new Host(getLocalName(), getLocalAddr(), getLocalPort());
	}

	public String getLocalAddr(){
		return req.getLocalAddr();
	}

	public int getLocalPort(){
		return req.getLocalPort();
	}

	public String getLocalName(){
		return req.getLocalName();
	}

	//////////////////////////////

	public Host getServer(){
		return new Host(getServerName(), getServerPort());
	}

	public int getServerPort(){
		return req.getServerPort();
	}

	public String getServerName(){
		return req.getServerName();
	}


	public boolean isSsl(){
		return req.isSecure();
		//return HTTPS.equalsIgnoreCase(req.getScheme());
	}

	public String getUserAgent(){
		return getHeader(USER_AGENT);
	}

	public String getReferer(){
		return getHeader(REFERER);
	}

	public boolean isPost(){
		return POST.equalsIgnoreCase(getMethod());
	}

	public boolean isPut(){
		return PUT.equalsIgnoreCase(getMethod());
	}

	public boolean isGet(){
		return GET.equalsIgnoreCase(getMethod());
	}

	public boolean isDelete(){
		return DELETE.equalsIgnoreCase(getMethod());
	}

	public String getMethod(){
		return req.getMethod();
	}

	public String getContextPath(){
		return req.getContextPath();
	}

	public Object getAttribute(String name){
		return req.getAttribute(name);
	}

	public void setAttribute(String name, Object value){
		req.setAttribute(name, value);
	}

	public List<String> getParameterNames() {
		return Collections.unmodifiableList(Collections.list(req.getParameterNames()));
	}

	public RequestDispatcher createRequestDispatcher(String s) {
		return req.getRequestDispatcher(s);
	}

	public String getAdditionalPathInfo() {
		String srvUrl = getServletPath();
		String url = getRequestURL();
		try {
			return url.substring(url.indexOf(srvUrl) + srvUrl.length() + 1);
		} catch (StringIndexOutOfBoundsException e) {
			return null;
		}
	}

	public String[] getAdditionalPathTokens() {
		String addl = getAdditionalPathInfo();
		if (addl == null) {
			return null;
		}
		return Utils.split(addl, SLASH);
	}

	public String getRequestURI() {
		return req.getRequestURI();
	}

	public String getServletPath() {
		return req.getServletPath();
	}

	public String getRequestURL() {
		return req.getRequestURL().toString();
	}

	public String getQueryString() {
		return req.getQueryString();
	}

	public HttpSession getSession(boolean bln) {
		return req.getSession(bln);
	}

	public String getSessionId() {
		return getSession().getId();
	}

	public HttpSession getSession() {
		return getSession(true);
	}

	public boolean hasSession() {
		return getSession() != null;
	}

	public void invalidateSession() {
		if (hasSession()) {
			getSession().invalidate();
		}
	}

}








