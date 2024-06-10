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
 * @author <ahref="mailto:tauasa@gmail dot com?subject=org.tauasa.commons.jdbi.JDBIUtils">tauasa@gmail dot com</a>
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
		for (Object arg : args) {
			stmt.bind(0, arg);
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
