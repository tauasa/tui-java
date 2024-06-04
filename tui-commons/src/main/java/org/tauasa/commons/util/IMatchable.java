/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.util;


/**
 * Matches arbitrary objects to this object
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public interface IMatchable<T> {

	/**
	 * Returns true if the specified object matches this one
	 * @return boolean if the specified instance matches this instance
	 * */
	public boolean matches(T that);

}
