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
 * @author Tauasa Timoteo
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
