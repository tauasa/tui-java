/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.err;


/**
 * TODO - document DuplicateException
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class DuplicateException extends Exception {

	private static final long serialVersionUID = 1L;

	public DuplicateException() {
	}

	public DuplicateException(String arg0) {
		super(arg0);
	}

	public DuplicateException(Throwable arg0) {
		super(arg0);
	}

	public DuplicateException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
