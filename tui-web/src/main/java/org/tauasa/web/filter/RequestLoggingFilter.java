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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tauasa.commons.util.Utils;
import org.tauasa.web.RequestBean;


/**
 * {@link Filter} implementation that logs HTTP requests. Requests are logged
 * at the lowest possible level/severity (according to configuration) unless
 * the elapsed time of the entire request-response loop (in milliseconds) exceeds
 * the {@link #warningThreshold} or {@link #errorThreshold} (which results in the
 * request event being logged at an ERROR or WARN level,
 * respectively).
 * <p/>
 * The calculated/elapsed time is the number of milliseconds it takes between
 * invocation of the {@link FilterChain#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse)}
 * and when the method returns control.
 *
 * @author Tauasa Timoteo
 * 
 */
public class RequestLoggingFilter extends AbstractFilter {

	private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
	public static final long DEFAULT_ERROR_THRESHOLD = 5000L;
	public static final long DEFAULT_WARN_THRESHOLD = 2000L;
	/**
	 * Name of the logger to use. If not specified the logger for this class is used
	 * <code>LoggerFactory.getLogger(RequestLoggingFilter.class)</code>
	 * */
	private String loggerName;
	/**
	 * Number of milliseconds that will log the request at ERROR level (Default: {@value #DEFAULT_ERROR_THRESHOLD})
	 * */
	private long errorThreshold;
	/**
	 * Number of milliseconds that will log the request at WARN level (Default: {@value #DEFAULT_WARN_THRESHOLD})
	 * */
	private long warningThreshold;
	/**
	 * Boolean flag that enables MDC
	 * */
	//private boolean mdcEnabled = true;

	public RequestLoggingFilter() {
		super();
	}

	@Override
	public Logger getLogger(){
		if(!Utils.isEmpty(loggerName)){
			return LoggerFactory.getLogger(loggerName);
		}
		return log;
	}

	@Override
	public void doFilter(FilterHelper helper) throws IOException, ServletException{

		//set the mapped diagnostic context

		//pass the request along
		try{
			helper.doFilter();
			long elapsed = helper.getElapsedTime();
			if(errorThreshold > 0 && elapsed >= errorThreshold){
				getLogger().error(createMessage(helper, elapsed));
			}else if(warningThreshold > 0 && elapsed >= warningThreshold){
				getLogger().warn(createMessage(helper, elapsed));
			}else if(log.isTraceEnabled()){
				getLogger().trace(createMessage(helper, elapsed));
			}else if(log.isDebugEnabled()){
				getLogger().debug(createMessage(helper, elapsed));
			}else if(log.isInfoEnabled()){
				getLogger().info(createMessage(helper, elapsed));
			}
		}finally{
			//clean up MDC
			//MDC.remove(key)

		}
	}

	private static String createMessage(FilterHelper helper, long elapsed){
		return new StringBuilder(new RequestBean(helper).toString())
		.append(" - [").append(elapsed).append("ms]").toString();
	}

	@Override
	public void init() throws ServletException {
		errorThreshold = getLongParam("errorThreshold", DEFAULT_ERROR_THRESHOLD);
		warningThreshold = getLongParam("warningThreshold", DEFAULT_WARN_THRESHOLD);
		loggerName = getStringParam("loggerName");
	}

}
