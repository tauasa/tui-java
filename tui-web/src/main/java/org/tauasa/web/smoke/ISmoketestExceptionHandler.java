/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.web.smoke;

import org.tauasa.web.ServletHelper;

import java.io.IOException;

import javax.servlet.ServletException;

import org.slf4j.Logger;

/**
 * Handles a {@link SmoketestException}
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public interface ISmoketestExceptionHandler {

	/**
	 * Handles the specified {@link SmoketestException}
	 * @throws javax.servlet.ServletException which may wrap the specified SmoketestException
	 * @throws java.io.IOException if an I/O error occurs
	 * */
	public void handleException(final SmoketestException e, ServletHelper helper, final Logger logger)throws ServletException, IOException;


}
