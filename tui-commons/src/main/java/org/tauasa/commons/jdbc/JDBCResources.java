/*
 * Copyright 2012 Tauasa Timoteo
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation 
 * files (the “Software”), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-
 * INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN 
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF 
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 */
package org.tauasa.commons.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/**
 * A named wrapper class for a {@link Connection} and its associated resources (i.e.
 * {@link Statement}, {@link PreparedStatement}, {@link ResultSet}).
 *
 * @author Tauasa Timoteo
 * 
 */
public final class JDBCResources {

	protected String name;
	protected Connection conn;
	protected HashMap<String, Statement> statements = new HashMap<>();
	protected HashMap<String, ResultSet> resultSets = new HashMap<>();
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

				for (ResultSet rs : resultSets.values()) {
					JDBCUtils.closeIgnoringException(rs);
				}

				for (Statement stmt : statements.values()) {
					JDBCUtils.closeIgnoringException(stmt);
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
