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

import org.tauasa.commons.util.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Thread-safe DAO implementation that utilizes a {@link ThreadLocal} to store/manage
 * a {@link JDBCResources} object.
 *
 * @author Tauasa Timoteo
 * 
 */
public class ThreadLocalDAO {

	private static final Logger logger = LoggerFactory.getLogger(ThreadLocalDAO.class);

	protected static final ThreadLocal<JDBCResources> localResources = new ThreadLocal<JDBCResources>();

	protected IConnectionFactory connectionFactory;

	protected int resultSetType = ResultSet.TYPE_FORWARD_ONLY;
	protected int resultSetConcurrency = ResultSet.CONCUR_READ_ONLY;

	protected int queryTimeout = -1;

	public ThreadLocalDAO(IConnectionFactory connectionFactory) {
		this.connectionFactory=connectionFactory;
	}

	public IConnectionFactory getConnectionFactory(){
		return connectionFactory;
	}

	protected JDBCResources getResources()throws SQLException{

		if(connectionFactory==null){
			throw new NullPointerException("ConnectionFactory is null");
		}

		JDBCResources resources = localResources.get();

		if(resources==null){
			if(logger.isDebugEnabled()){
				logger.debug("Creating JDBCResources");
			}
			Connection conn = connectionFactory.getConnection();
			resources = new JDBCResources(conn);
			localResources.set(resources);
		}

		return resources;

	}

	public final Connection getConnection()throws SQLException{
		Connection conn = getResources().getConnection();
		if(conn==null){
			throw new SQLException("Could not open connection");
		}
		return conn;
	}

	/**
	 * Executes the specified query and returns a list of objects of type <code>T</code>
	 * created by the specified {@link IObjectCreator}
	 * */
	public <T> List<T> executeQuery(String sqlQuery, IObjectCreator<T> creator)throws SQLException{
		ResultSet rs = executeQuery(sqlQuery);
		ArrayList<T> list = null;
		while(rs.next()){
			if(list==null){
				list = new ArrayList<T>();
			}
			list.add(creator.createObject(rs));
		}
		return list;
	}

	/**
	 * Executes the specified query using the specified arguments and returns a list of
	 * objects of type <code>T</code> created by the specified {@link IObjectCreator}
	 * */
	public <T> List<T> executeQuery(String sqlQuery, Object[] args, IObjectCreator<T> creator)throws SQLException{
		ResultSet rs = executeQuery(sqlQuery, args);
		ArrayList<T> list = null;
		while(rs.next()){
			if(list==null){
				list = new ArrayList<T>();
			}
			list.add(creator.createObject(rs));
		}
		return list;
	}

	public ResultSet executeQuery(String sqlQuery)throws SQLException{

		if(logger.isDebugEnabled()){
			logger.debug(String.format("executeQuery(\"%s\")", sqlQuery));
		}

		//get or create statement
		Statement stmt = getResources().getStatement(sqlQuery);
		if(stmt==null){
			stmt = getConnection().createStatement(resultSetType, resultSetConcurrency);
			if(queryTimeout > 0){
				stmt.setQueryTimeout(queryTimeout);
			}
			getResources().setStatement(sqlQuery, stmt);
		}

		ResultSet rs = stmt.executeQuery(sqlQuery);

		getResources().setResultSet(sqlQuery, rs);

		return rs;
	}

	public ResultSet executeQuery(String sqlQuery, Object[] args) throws SQLException{

		if(logger.isDebugEnabled()){
			logger.debug(String.format("executeQuery(\"%s\", [%s])", sqlQuery, args!=null?Utils.join(args, ", "):"null"));
		}

		//get or prepare statement
		PreparedStatement stmt = (PreparedStatement)getResources().getStatement(sqlQuery);
		if(stmt==null){
			stmt = getConnection().prepareStatement(sqlQuery, resultSetType, resultSetConcurrency);
			getResources().setStatement(sqlQuery, stmt);
		}else{
			//clear existing parameters
			stmt.clearParameters();
		}

		//set parameters
		JDBCUtils.bind(stmt, args);

		ResultSet rs = stmt.executeQuery();

		getResources().setResultSet(sqlQuery, rs);

		return rs;
	}
	
	public int executeCountQuery(String sqlQuery)throws SQLException{
		if(logger.isDebugEnabled()){
			logger.debug(String.format("executeCountQuery(\"%s\")", sqlQuery));
		}
		ResultSet rs = executeQuery(sqlQuery);
		rs.next();
		return rs.getInt(1);
	}
	
	public int executeCountQuery(String sqlQuery, Object[] args)throws SQLException{
		ResultSet rs = executeQuery(sqlQuery, args);
		rs.next();
		return rs.getInt(1);
	}

	public int executeUpdate(String sqlUpdate) throws SQLException{

		if(logger.isDebugEnabled()){
			logger.debug(String.format("executeUpdate(\"%s\")", sqlUpdate));
		}

		//get or create statement
		Statement stmt = getResources().getStatement(sqlUpdate);
		if(stmt==null){
			stmt = getConnection().createStatement(resultSetType, resultSetConcurrency);
			if(queryTimeout > 0){
				stmt.setQueryTimeout(queryTimeout);
			}
			getResources().setStatement(sqlUpdate, stmt);
		}

		return stmt.executeUpdate(sqlUpdate);
	}

	public int executeUpdate(String sqlUpdate, Object[] args) throws SQLException{

		if(logger.isDebugEnabled()){
			logger.debug(String.format("executeUpdate(\"%s\", [%s])", sqlUpdate, args!=null?Utils.join(args, ", "):"null"));
		}

		//prepare a statement
		PreparedStatement stmt = (PreparedStatement)getResources().getStatement(sqlUpdate);
		if(stmt==null){
			stmt = getConnection().prepareStatement(sqlUpdate, resultSetType, resultSetConcurrency);
			getResources().setStatement(sqlUpdate, stmt);
		}else{
			//clear existing parameters
			stmt.clearParameters();
		}

		//set parameters
		JDBCUtils.bind(stmt, args);

		return stmt.executeUpdate();
	}

	/**
	 * Invokes closeAll() and swallows any exceptions
	 * */
	public void closeAllQuietly(){
		try{
			this.closeAll();
		}catch(SQLException e){
			logger.warn("An error occurred closing resources", e);
		}
	}

	public void closeAll() throws SQLException{

		if(logger.isDebugEnabled()){
			logger.debug("closeAll");
		}

		JDBCResources resources = getResources();

		if(resources!=null){
			resources.close();
			//remove the JDBCResources from the ThreadLocal instance
			localResources.set(null);
			resources = null;
		}
	}

	public void commit()throws SQLException{
		if(logger.isDebugEnabled()){
			logger.debug("commit");
		}
		getResources().getConnection().commit();
	}

	public void rollback()throws SQLException{
		if(logger.isDebugEnabled()){
			logger.debug("rollback");
		}
		getResources().getConnection().rollback();
	}

	public int getResultSetConcurrency() {
		return resultSetConcurrency;
	}

	public int getResultSetType() {
		return resultSetType;
	}

	public void setResultSetConcurrency(int i) {
		resultSetConcurrency = i;
	}

	public void setResultSetType(int i) {
		resultSetType = i;
	}

	public int getQueryTimeout() {
		return queryTimeout;
	}

	public void setQueryTimeout(int queryTimeout) {
		this.queryTimeout = queryTimeout;
	}

}
