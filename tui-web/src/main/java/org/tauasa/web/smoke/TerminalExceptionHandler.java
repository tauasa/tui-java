/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.web.smoke;

import org.tauasa.web.ServletHelper;

import java.io.IOException;

import javax.servlet.ServletException;

import org.slf4j.Logger;

/**
 * Sets the response status to {@link HttpServletResponse@#SC_INTERNAL_SERVER_ERROR},
 * logs the exception at the {@link Level#ERROR} level, and throws the {@link SmoketestException} wrapped
 * in a {@link ServletException}
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class TerminalExceptionHandler extends DefaultExceptionHandler {

	public TerminalExceptionHandler() {

	}

	@Override
	public void handleException(SmoketestException e, ServletHelper helper, Logger logger) throws ServletException, IOException {

		super.handleException(e, helper, logger);

		//re-throw the exception
		throw new ServletException(e);
	}



}
