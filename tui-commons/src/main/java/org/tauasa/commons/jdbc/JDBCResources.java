/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A named wrapper class for a {@link Connection} and its associated resources (i.e.
 * {@link Statement}, {@link PreparedStatement}, {@link ResultSet}).
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class JDBCResources {

	protected String name;
	protected Connection conn;
	protected HashMap<String, Statement> statements = new HashMap<String, Statement>();
	protected HashMap<String, ResultSet> resultSets = new HashMap<String, ResultSet>();
	private boolean closed = false;

	public JDBCResources(Connection conn){
		this(null, conn);
	}

	public JDBCResources(Connection conn, String key, Statement stmt, ResultSet rs){
		this(null, conn, key, stmt, rs);
	}

	public JDBCResources(String name, Connection conn){
		setName(name);
		setConnection(conn);
	}

	public JDBCResources(String name, Connection conn, String key, Statement stmt, ResultSet rs){
		setName(name);
		setConnection(conn);
		if(stmt!=null){
			setStatement(key, stmt);
		}
		if(rs!=null){
			setResultSet(key, rs);
		}
	}

	public boolean isClosed()throws SQLException{

		return closed || conn.isClosed();

	}

	public Connection getConnection(){
		return conn;
	}

	public void setConnection(Connection conn){
		this.conn = conn;
	}

	public void setStatement(String key, Statement stmt){
		statements.put(key, stmt);
	}

	public void setResultSet(String key, ResultSet rs){
		resultSets.put(key, rs);
	}

	public Statement getStatement(String key){
		return statements.get(key);
	}

	public ResultSet getResultSet(String key){
		return resultSets.get(key);
	}

	public void close()throws SQLException{
		if(closed || (conn!=null && conn.isClosed())){
			return;
		}
		closed = true;
		try{

			Iterator<ResultSet> rsIt = resultSets.values().iterator();
			while(rsIt.hasNext()){
				ResultSet rs = rsIt.next();
				JDBCUtils.closeIgnoringException(rs);
				rs=null;
			}

			Iterator<Statement> stmtIt = statements.values().iterator();
			while(stmtIt.hasNext()){
				Statement stmt = stmtIt.next();
				JDBCUtils.closeIgnoringException(stmt);
				stmt=null;
			}

		}finally{

			if(conn!=null && !conn.isClosed()){
				conn.close();
			}

		}
	}
	public String getName() {
		return name;
	}

	public void setName(String string) {
		name = string;
	}

}
