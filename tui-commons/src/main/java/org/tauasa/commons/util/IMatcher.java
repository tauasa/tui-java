/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.util;


/**
 * Contract for checking if two objects match
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public interface IMatcher<T> {

	/**
	 * Returns true if the specified objects are a match
	 * */
	public boolean isMatch(T t1, T t2);


}
