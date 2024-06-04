/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Factory for obtaining {@link Connection} objects
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public interface IConnectionFactory {

	/**
	 * Returns a java.sql.Connection object
	 * */
	public Connection getConnection()throws SQLException;

	/**
	 * Releases any Connections or Connection-related resources
	 * */
	public void release()throws SQLException;

}
