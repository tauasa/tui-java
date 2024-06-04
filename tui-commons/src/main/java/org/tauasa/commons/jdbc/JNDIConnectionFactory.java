/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link IConnectionFactory} implementation backed by a JNDI {@link DataSource}
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class JNDIConnectionFactory implements IConnectionFactory {

	private static final Logger logger = LoggerFactory.getLogger(JNDIConnectionFactory.class);

	private DataSource dataSource;

	public JNDIConnectionFactory(DataSource dataSource){
		if(dataSource==null){
			throw new NullPointerException("DataSource is null");
		}
		this.dataSource=dataSource;
	}

	public JNDIConnectionFactory(String jndiName)throws NamingException{
		if(jndiName==null){
			throw new NullPointerException("dataSourceName is null");
		}
		Context ctx = new InitialContext();
		dataSource = (DataSource)ctx.lookup(jndiName);
	}

	public Connection getConnection() throws SQLException {
		if(logger.isDebugEnabled()){
			logger.debug("getConnection()");
		}
		return dataSource.getConnection();
	}

	public void release()throws SQLException{
		//nothing to do
	}

}
