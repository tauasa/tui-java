/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.web.smoke;
/**
 * {@link Exception} thrown if a {@link ISmoketest} is misconfigured
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class SmoketestConfigException extends SmoketestException {

	private static final long serialVersionUID = 1L;

	public SmoketestConfigException(String message, Throwable cause, SmoketestResult result) {
		super(message, cause, result);
	}



}
