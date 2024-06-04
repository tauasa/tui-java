/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.data;


/**
 * {@link Exception} subclass specific to operating with {@link IDataModel} implementations
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class DataModelException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public DataModelException() {
	}

	/**
	 * @param message the exception message
	 */
	public DataModelException(String message) {
		super(message);
	}

	/**
	 * @param cause the root cause
	 */
	public DataModelException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message the exception message
	 * @param cause the root cause
	 */
	public DataModelException(String message, Throwable cause) {
		super(message, cause);
	}

}
