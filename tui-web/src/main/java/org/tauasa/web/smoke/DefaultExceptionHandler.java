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
package org.tauasa.web.smoke;

import org.tauasa.web.ServletHelper;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

/**
 * Sets the response status to {@link HttpServletResponse#SC_INTERNAL_SERVER_ERROR} and logs the
 * exception at {@link Level#ERROR} level.
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class DefaultExceptionHandler implements ISmoketestExceptionHandler {

	public DefaultExceptionHandler() {

	}

	/**
	 * Sets the response status to {@link HttpServletResponse#SC_INTERNAL_SERVER_ERROR} and logs the exception at ERROR level
	 */
	public void handleException(SmoketestException e, ServletHelper helper, Logger logger) throws ServletException, IOException {
		//set the response status
		helper.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

		//build the error message
		String msg = buildDetailedErrorMessage(e, helper);

		//log the exception as an error
		logger.error(msg, e);
	}

	public static String buildDetailedErrorMessage(SmoketestException e, ServletHelper helper){
		//build a message
		StringBuilder b = new StringBuilder()
		.append("Smoke Test: ").append(e.getResult().getSmokeTest().getClass().getName()).append("\r\n")
		.append(SmoketestServlet.buildClientServerInfo(helper))
		.append("===========================================\r\n")
		.append(SmoketestServlet.buildErrorAndResultsMessage(e, helper));
		return b.toString();
	}

}
