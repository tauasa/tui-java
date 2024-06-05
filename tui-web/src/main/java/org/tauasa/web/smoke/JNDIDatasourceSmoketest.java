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
package org.tauasa.web.smoke;

import org.tauasa.commons.data.TableDataModel;
import org.tauasa.commons.jdbc.JDBCUtils;
import org.tauasa.commons.util.Utils;
import org.tauasa.web.ServletHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;
/**
 * Performs a lookup on one or more JNDI {@link DataSource}s and, optionally, executes a SQL
 * query against it
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class JNDIDatasourceSmoketest extends AbstractJNDISmoketest {

	public static final String SQL_DELIMITER_KEY = "sql.delimiter";
	public static final String SQL_KEY = "sql";

	protected String sqlDelimiter;
	protected String[] sql;

	public JNDIDatasourceSmoketest() {
	}

	public JNDIDatasourceSmoketest(LookupDelegate delegate) {
		super(delegate);
	}

	@Override
	public String getDescription() {
		if(description!=null){//default to value defined in smoketest.properties
			return description;
		}
		//jndiNames
		StringBuilder b = new StringBuilder()
		.append("Performs a JNDI lookup on the ")
		.append(Utils.join(jndiNames, ", "))
		.append(" datasource").append(jndiNames.length > 1 ? "s" : "");
		if(!Utils.isEmpty(sql)){
			b.append(" and executes one or more SQL commands");
		}
		b.append(".");
		return b.toString();
	}

	@Override
	public void init(Properties smoketestProperties)throws SmoketestConfigException{
		super.init(smoketestProperties);

		getLogger().info("Getting SQL...");

		sql = getArrayProperty(smoketestProperties,
				SQL_KEY,
				getProperty(smoketestProperties, SQL_DELIMITER_KEY, DEFAULT_DELIMITER),
				null);

		if(sql==null){
			getLogger().info("SQL: none");
		}else{
			getLogger().info("SQL:\r\n"+Utils.join(sql, "\r\n"));
		}
	}

	@Override
	public void doExecute(ServletHelper helper) throws Exception {

		//perform lookups only
		if(Utils.isEmpty(sql)){
			getLogger().warn("No SQL defined to test datasources - performing lookups only");

			for (String jndiName : jndiNames) {
				try{
					addInfoMessage("Looking up "+jndiName);
					super.lookup(jndiName);
				}catch(Exception e){
					addErrorMessage(e);
					continue;
				}
				addInfoMessage("Lookup succeeded");
			}
			if(result.hasErrorMessage()){
				//throw an exception
				throw new Exception("One or more errors occurred performing datasource lookup");
			}
			return;
		}

		if(jndiNames.length != sql.length){
			throw new Exception("If SQL statements are specified, "+
					getClass().getSimpleName()+"."+JNDI_NAMES_KEY+" and "+
					getClass().getSimpleName()+"."+SQL_KEY+" must have the same number of arguments");
		}

		for (int i=0;i<jndiNames.length;i++) {
			DataSource ds = null;
			try{
				addInfoMessage("Looking up "+jndiNames[i]);
				ds = (DataSource)super.lookup(jndiNames[i]);
			}catch(Exception e){
				addErrorMessage(jndiNames[i], e);
				continue;
			}
			if(ds==null){
				addErrorMessage(jndiNames[i], new Exception("Lookup failed for datasource "+jndiNames[i]));
				continue;
			}
			addInfoMessage("Lookup succeeded");

			Connection conn = ds.getConnection();
			Statement stmt = null;
			ResultSet rs = null;
			try{
				addInfoMessage("Creating java.sql.Statement for \""+sql[i]+"\"");
				//make sure the result set type is not ResultSet.TYPE_SCROLL_INSENSITIVE
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				addInfoMessage("Executing \""+sql[i]+"\" against "+jndiNames[i]);

				rs = stmt.executeQuery(sql[i]);
				if(verbose){
					iterate(rs, jndiNames[i], sql[i]);
				}else{
					int rowCount = 0;
					while(rs.next()){
						rowCount++;
					}
					addInfoMessage("\""+sql[i]+"\" succeeded and returned "+rowCount+" row(s)");
				}
			}catch(SQLException e){

				addErrorMessage(jndiNames[i], e);

			}finally{
				getLogger().info("Closing datasource resources");
				JDBCUtils.closeIgnoringException(rs);
				JDBCUtils.closeIgnoringException(stmt);
				JDBCUtils.closeIgnoringException(conn);
			}
		}

		//one or more datasources failed
		if(result.hasErrorMessage()){
			//throw an exception
			throw new Exception("One or more errors occurred testing datasources");
		}

	}

	/**
	 * Iterates over the result set
	 * */
	protected void iterate(ResultSet rs, String jndiName, String sql)throws SQLException{
		getLogger().info("Fetching ResultSetMetaData");
		ResultSetMetaData md = rs.getMetaData();
		int numCols = md.getColumnCount();
		getLogger().info("Columns: "+numCols);

		//create a TableDataModel from the result set
		try{
			result.add(new SmoketestMessage(new TableDataModel(rs), new SmoketestMessage.DataModelFormatter("SQL: "+sql)));
		}catch(Exception e){
			addWarningMessage("DataModelFormatter failed for "+jndiName+" ("+sql+")");
			result.add(new SmoketestMessage(sql));
			rs.beforeFirst();
			int rowCount = 0;
			while(rs.next()){
				rowCount++;
				for(int i=0;i<numCols;i++){
					int colIndex = i+1;
					String colName = md.getColumnName(colIndex);
					if(colName==null){
						colName = String.valueOf(colIndex);
					}
					Object value = rs.getObject(colIndex);
					result.add(new SmoketestMessage("Row: "+rowCount+", Col: "+colName+": "+(value==null?"NULL":value.toString())));
				}
			}
			getLogger().info("Rows: "+rowCount);

			if(rowCount < 1){
				addWarningMessage(jndiName+" -&gt; "+sql+" returned 0 rows");
			}
		}
	}

	public String[] getSql() {
		return sql;
	}

	public String getSqlDelimiter() {
		return sqlDelimiter;
	}

}
