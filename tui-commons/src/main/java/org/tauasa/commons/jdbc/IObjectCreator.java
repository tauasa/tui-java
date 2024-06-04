/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Creates/hydrates and object from the current row in a {@link ResultSet}
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 *
 */
public interface IObjectCreator<T> {

	/**
	 * Creates/hydrates an object &lt;T&gt; from the current row in the specified ResultSet
	 * */
	public T createObject(ResultSet rs)throws SQLException;

}
