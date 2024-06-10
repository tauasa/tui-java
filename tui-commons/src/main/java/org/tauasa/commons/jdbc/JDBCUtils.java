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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tauasa.commons.io.IOUtils;


/**
 * Generic JDBC utility methods
 *
 * @author Tauasa Timoteo
 * 
 */
public class JDBCUtils extends IOUtils{

	private static final Logger logger = LoggerFactory.getLogger(JDBCUtils.class);

	/**
	 * Extracts column names from the specified {@link ResultSet} and returns 
	 * them as a {@link String} array
	 * */
	public static String[] getColumnNames(ResultSet rs)throws SQLException{
		return getColumnNames(rs.getMetaData());
	}

	/**
	 * Extracts column names from the specified {@link ResultSetMetaData} and returns 
	 * them as a {@link String} array
	 * */
	public static String[] getColumnNames(ResultSetMetaData metaData)throws SQLException{
		int count = metaData.getColumnCount();
		String[] columnNames = new String[count];
		for(int i=0;i<count;i++){
			columnNames[i] = metaData.getColumnName(i+1);
		}
		return columnNames;
	}

	/**
	 * Binds the specified objects to the specified {@link PreparedStatement} using
	 * {@link PreparedStatement#setObject(int, Object)}
	 * */
	public static void bind(PreparedStatement stmt, Object[] args)throws SQLException{
		if(isEmpty(args)){
			return;
		}
		if(logger.isDebugEnabled()){
			logger.debug("binding "+args.length+" args: "+join(args, ", "));
		}
		for(int i=0;i<args.length;i++){
			if(args[i] instanceof XArg){
				((XArg)args[i]).bind(stmt, (i+1));
			}else if(args[i] instanceof java.util.Date){

				if(args[i]==null){
					stmt.setDate(i+1, null);
				}else{
					stmt.setTimestamp(i+1, new java.sql.Timestamp(((java.util.Date)args[i]).getTime()));
					//stmt.setDate(i+1, new java.sql.Date(((java.util.Date)args[i]).getTime()));
				}
			}else if(args[i] instanceof String){

				stmt.setString(i+1, args[i]==null ? null : args[i].toString());
			}else{

				stmt.setObject(i+1, args[i]);
			}

		}
	}

	/**
	 * Returns a string description for the specified integer value from java.sql.Types
	 * */
	public static final String getTypeName(final int type){
		switch (type) {

		case Types.VARCHAR:
			return "VARCHAR";
		case Types.DATE:
			return "DATE";
		case Types.DECIMAL:
			return "DECIMAL";
		case Types.TINYINT:
			return "TINYINT";
		case Types.BIGINT:
			return "BIGINT";
		case Types.NUMERIC:
			return "NUMERIC";
		//case Types.NVARCHAR:
		//	return "NVARCHAR";
		case Types.TIMESTAMP:
			return "TIMESTAMP";
		case Types.BOOLEAN:
			return "BOOLEAN";
		case Types.CHAR:
			return "CHAR";
		case Types.TIME:
			return "TIME";
		case Types.DOUBLE:
			return "DOUBLE";
		case Types.FLOAT:
			return "FLOAT";
		case Types.INTEGER:
			return "INTEGER";
		case Types.REAL:
			return "REAL";

		case Types.VARBINARY:
			return "VARBINARY";

		case Types.ARRAY:
			return "ARRAY";

		case Types.BINARY:
			return "BINARY";
		case Types.BIT:
			return "BIT";
		case Types.BLOB:
			return "BLOB";


		case Types.CLOB:
			return "CLOB";
		case Types.DATALINK:
			return "DATALINK";

		case Types.DISTINCT:
			return "DISTINCT";

		case Types.JAVA_OBJECT:
			return "JAVA_OBJECT";
		//case Types.LONGNVARCHAR:
		//	return "LONGNVARCHAR";
		case Types.LONGVARBINARY:
			return "LONGVARBINARY";
		case Types.LONGVARCHAR:
			return "LONGVARCHAR";
		//case Types.NCHAR:
		//	return "NCHAR";
		//case Types.NCLOB:
		//	return "NCLOB";
		case Types.NULL:
			return "NULL";

		case Types.OTHER:
			return "OTHER";

		case Types.REF:
			return "REF";
		//case Types.ROWID:
		//	return "ROWID";
		case Types.SMALLINT:
			return "SMALLINT";
		//case Types.SQLXML:
		//	return "SQLXML";
		case Types.STRUCT:
			return "STRUCT";

		}
		return "UNKNOWN";
	}



}
