/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple {@link IConnectionFactory} implementation that opens {@link Connection}
 * objects using a specified URL, username and password. <strong>This class is only suitable
 * for rapid prototyping and should not be used in a production environment.</strong>
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class SimpleConnectionFactory implements IConnectionFactory {

	private static final Logger logger = LoggerFactory.getLogger(SimpleConnectionFactory.class);

	private JDBCProperties props;

	public SimpleConnectionFactory(String url, String userName, String password, boolean autoCommit) {
		props = new JDBCProperties();
		props.setUrl(url);
		props.setUser(userName);
		props.setPassword(password);
		props.setAutoCommit(autoCommit);
	}

	public SimpleConnectionFactory(JDBCProperties props) {
		this.props=props;
	}

	public Connection getConnection() throws SQLException {
		if(logger.isDebugEnabled()){
			logger.debug("getConnection()");
		}
		Connection conn = DriverManager.getConnection(props.getUrl(), props.getUser(), props.getPassword());
		conn.setAutoCommit(props.isAutoCommit());
		return conn;
	}

	public void release() throws SQLException {


	}

}
