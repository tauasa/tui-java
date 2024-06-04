/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.err;

/**
 * UncheckedException is a subclass of {@link RuntimeException} and 
 * is used as the base class for creating domain-specific unchecked 
 * {@link Exception} classes (especially useful if you're working with 
 * or implementing a fluent syntax or if you just hate handling checked 
 * exceptions) 
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class UncheckedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UncheckedException() {
	}

	public UncheckedException(String arg0) {
		super(arg0);
	}

	public UncheckedException(Throwable arg0) {
		super(arg0);
	}

	public UncheckedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
