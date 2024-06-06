/*
 * Copyright 2012 Tauasa Timoteo
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this 
 * software and associated documentation files (the “Software”), to deal in 
 * the Software without restriction, including without limitation the rights to use, 
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of 
 * the Software, and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE 
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT 
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR 
 * OTHER DEALINGS IN THE SOFTWARE.
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
 * @author Tauasa Timoteo
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
