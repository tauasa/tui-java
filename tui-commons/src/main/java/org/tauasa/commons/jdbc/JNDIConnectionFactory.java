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
 * @author Tauasa Timoteo
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

	@Override
	public Connection getConnection() throws SQLException {
		if(logger.isDebugEnabled()){
			logger.debug("getConnection()");
		}
		return dataSource.getConnection();
	}

	@Override
	public void release()throws SQLException{
		//nothing to do
	}

}
