/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.jdbi;

import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.SQLStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tauasa.commons.io.IOUtils;


/**
 * <a href="http://www.jdbi.org/">JDBI</a> utility methods
 *
 * @author <ahref="mailto:tauasa@gmail.com?subject=org.tauasa.commons.jdbi.JDBIUtils">tauasa@gmail.com</a>
 * 
 */
public class JDBIUtils extends IOUtils{

	private static final Logger logger = LoggerFactory.getLogger(JDBIUtils.class);
	
	/**
	 * Binds the specified objects to the specified {@link SQLStatement} using
	 * {@link PreparedStatement#setObject(int, Object)}
	 * */
	public static void bind(SQLStatement<? extends SQLStatement<?>> stmt, Object[] args){
		if(isEmpty(args)){
			return;
		}
		if(logger.isDebugEnabled()){
			logger.debug("binding "+args.length+" args: "+join(args, ", "));
		}
		for(int i=0;i<args.length;i++){
			stmt.bind(0, args[i]);
		}
	}

	/**
	 * Extracts column names from the specified {@link List} and returns them as
	 * a {@link String} array
	 * */
	public static String[] getColumnNames(final List<Map<String, Object>> rs) {
		if (isEmpty(rs)) { throw new IllegalArgumentException(); }
		// get the first row in the result set
		Map<String, Object> row1 = rs.get(0);

		// create an array to hold the column names
		String[] cols = new String[row1.size()];

		// keys represent column name - cram them into the array
		Iterator<String> it = row1.keySet().iterator();

		int i = 0;
		while (it.hasNext()) {
			cols[i] = it.next();
			i++;
		}

		return cols;
	}


}
