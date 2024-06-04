/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
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
