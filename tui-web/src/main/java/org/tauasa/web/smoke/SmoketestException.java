/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.web.smoke;


/**
 * Exception raised during a {@link ISmoketest}. This class may contain a {@link SmoketestResult}
 * representing the results of the smoketest up until this exception was thrown
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class SmoketestException extends Exception {

	private static final long serialVersionUID = 1L;

	private SmoketestResult mResult;

	public SmoketestException(String message, Throwable cause, SmoketestResult result) {
		//this(message, cause);
		super(message, cause);
		if(result==null){
			throw new NullPointerException("SmoktestResult cannot be null");
		}
		this.mResult=result;
	}

	public SmoketestResult getResult(){
		return mResult;
	}

}
