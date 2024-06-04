/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.web;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Convenience wrapper for various {@link HttpServletRequest} attributes
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class RequestBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private HttpServletRequest request;
	private String remoteUser;
	private String contextPath;
	private StringBuffer url;
	private String uri;
	private String queryString;
	private String remoteAddr, remoteHost;
	private int remotePort;
	private String userAgent;
	private String referer;
	private String xff;

	public RequestBean() {

	}

	public RequestBean(HttpServletRequestHelper helper) {
		this(helper.getRequest());
	}

	/**
	 * Constructs this bean from the specified {@link HttpServletRequest}
	 * */
	public RequestBean(HttpServletRequest request) {
		setRequest(request);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE, false);
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		HttpServletRequestHelper helper = new HttpServletRequestHelper(request);
		this.request = request;
		setRemoteUser(request.getRemoteUser());
		setContextPath(request.getContextPath());
		setUrl(request.getRequestURL());
		setUri(request.getRequestURI());
		setQueryString(request.getQueryString());
		setRemoteAddr(request.getRemoteAddr());
		setRemoteHost(request.getRemoteHost());
		setRemotePort(request.getRemotePort());
		setUserAgent(helper.getUserAgent());
		setReferer(helper.getReferer());
		setXff(helper.getXff());

	}

	public String getRemoteUser() {
		return remoteUser;
	}

	public void setRemoteUser(String remoteUser) {
		this.remoteUser = remoteUser;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public StringBuffer getUrl() {
		return url;
	}

	public void setUrl(StringBuffer url) {
		this.url = url;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	public String getXff() {
		return xff;
	}

	public void setXff(String xff) {
		this.xff = xff;
	}




}
