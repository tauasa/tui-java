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
package org.tauasa.web.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;


/**
 * Abstract {@link Filter} implementation that provides informative logging
 * several convenience methods
 * 
 * @author Tauasa Timoteo
 */
public abstract class AbstractFilter implements Filter{

	protected FilterConfig config;
	protected Map<String, String> parameterMap;

	public AbstractFilter() {

	}

	/**
	 * Logger created by the implementing filter (allows accurate logging)
	 * */
	public abstract Logger getLogger();

	public abstract void doFilter(final FilterHelper helper) throws IOException, ServletException;

	@Override
	public final void doFilter(final ServletRequest req, final ServletResponse res,
			final FilterChain chain) throws IOException, ServletException {

		if (getLogger().isDebugEnabled()) {
			getLogger().debug("Delegating control with a new ServletHelper");
		}

		// create a ServletHelper
		final FilterHelper helper = new FilterHelper(config, chain, (HttpServletRequest) req,
				(HttpServletResponse) res);

		doFilter(helper);

		if (getLogger().isDebugEnabled()) {
			getLogger().debug(String.format("Filter completed in %dms", helper.getElapsedTime()));
		}

	}

	/**
	 * Subclasses requiring additional initialization should override this no-op
	 * init method (calls to <code>super.init()</code> are unnecessary). The
	 * original {@link FilterConfig} is available via the protected member
	 * {@link #config}. Initialization parameters are available in the both the
	 * {@link FilterConfig} member and {@link #parameterMap}, and several
	 * overloaded convenience methods (<code>getXXXParam(String)</code>) for
	 * accessing typed initialization parameters are available.
	 * 
	 * @see {@link #getBooleanParam(String, boolean)},
	 *      {@link #getIntParam(String, int)},
	 *      {@link #getLongParam(String, long)}, etc
	 * */
	public void init() throws ServletException {
		if (getLogger().isDebugEnabled()) {
			getLogger()
					.debug("NO-OP initialization. Override the AbstractFilter.init() method to get rid of this message... or change your logging configuration :)");
		}

	}

	/**
	 * Logs container and API details, stores initialization parameters in a
	 * {@link Map}, and invokes the no-arg, no-op {@link #init()} method
	 * */
	@Override
	public final void init(final FilterConfig config) throws ServletException {
		this.config = config;
		final ServletContext context = config.getServletContext();
		getLogger().info(
				String.format(
						"Initializing filter %s for %s at %s on %s running Servlet API v%d.%d",
						config.getFilterName(),
						context.getServletContextName(),
						context.getContextPath(),
						context.getServerInfo(),
						context.getMajorVersion(),
						context.getMinorVersion()));

		final HashMap<String, String> map = new HashMap<>();

		if (getLogger().isInfoEnabled()) {
			getLogger().info("Mapping initialization parameters");
		}

		final Enumeration<String> names = config.getInitParameterNames();

		while (names.hasMoreElements()) {
			final String name = names.nextElement();
			final String value = config.getInitParameter(name);
			map.put(name, value);
			if (getLogger().isInfoEnabled()) {
				getLogger().info(String.format("%s=%s", name, value));
			}
		}

		// make our map of parameters immutable
		parameterMap = Collections.unmodifiableMap(map);

		// delegate to simple init
		if (getLogger().isInfoEnabled()) {
			getLogger().info("Delegating secondary initialization");
		}
		init();
	}

	@Override
	public void destroy() {
		final ServletContext context = config.getServletContext();
		if (getLogger().isInfoEnabled()) {
			getLogger().info(String.format("Destroying filter %s for %s at %s",
					config.getFilterName(),
					context.getServletContextName(),
					context.getContextPath()));
		}
	}

	/*
	 * protected void trace(String msg){ if(getLogger().isTraceEnabled()){
	 * getLogger().trace(msg); } }
	 * 
	 * protected void debug(String msg){ if(getLogger().isDebugEnabled()){
	 * getLogger().debug(msg); } }
	 * 
	 * protected void info(String msg){ if(getLogger().isInfoEnabled()){
	 * getLogger().info(msg); } }
	 * 
	 * protected void warn(String msg){ if(getLogger().isWarnEnabled()){
	 * getLogger().warn(msg); } }
	 * 
	 * protected void warn(String msg, Throwable t){
	 * if(getLogger().isWarnEnabled()){ getLogger().warn(msg, t); } }
	 * 
	 * protected void error(String msg){ if(getLogger().isErrorEnabled()){
	 * getLogger().error(msg); } }
	 * 
	 * protected void error(String msg, Throwable t){
	 * if(getLogger().isErrorEnabled()){ getLogger().error(msg, t); } }//
	 */

	protected String getStringParam(final String name) {
		return getStringParam(name, null);
	}

	protected String getStringParam(final String name, final String def) {
		final String val = parameterMap.get(name);
		return val == null ? def : val;
	}

	protected int getIntParam(final String name) {
		return getIntParam(name, 0);
	}

	protected int getIntParam(final String name, final int def) {
		final String val = getStringParam(name);
		return val == null ? def : Integer.parseInt(val);
	}

	protected long getLongParam(final String name) {
		return getLongParam(name, 0);
	}

	protected long getLongParam(final String name, final long def) {
		final String val = getStringParam(name);
		return val == null ? def : Long.parseLong(val);
	}

	protected boolean getBooleanParam(final String name) {
		return getBooleanParam(name, false);
	}

	protected boolean getBooleanParam(final String name, final boolean def) {
		final String val = getStringParam(name);
		return val == null ? def : Boolean.parseBoolean(val);
	}

	public FilterConfig getConfig() {
		return config;
	}

	public void setConfig(final FilterConfig config) {
		this.config = config;
	}

	public Map<String, String> getParameterMap() {
		return parameterMap;
	}

	public void setParameterMap(final Map<String, String> parameterMap) {
		this.parameterMap = parameterMap;
	}
}
