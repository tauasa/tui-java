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

import org.tauasa.commons.util.Utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * {@link IObjectCreator} impl for creating {@link DTO} objects from a {@link ResultSet}.
 * Column names available in the {@link ResultSetMetaData} are mapped to the {@link DTO} property names. If column
 * names are unavailable the column index is used instead (starting at 1)
 *
 * @author Tauasa Timoteo
 * 
 */
public class DTOCreator implements IObjectCreator<DTO> {

	private static final Logger logger = LoggerFactory.getLogger(DTOCreator.class);

	private String[] columnNames;

	public DTOCreator(){

	}

	private void loadColumnNames(ResultSetMetaData metadata)throws SQLException{
		if(logger.isDebugEnabled()){
			logger.debug("loadColumnNames");
		}
		columnNames = new String[metadata.getColumnCount()];
		for(int i=0;i<columnNames.length;i++){
			columnNames[i] = metadata.getColumnName(i+1);
		}
	}

        @Override
	public DTO createObject(ResultSet rs) throws SQLException {

		if(columnNames==null){
			//load the column names
			loadColumnNames(rs.getMetaData());
			if(logger.isDebugEnabled()){
				logger.debug("columnNames: "+Utils.join(columnNames, ", "));
			}
		}

		DTO dto = new DTO();

		//load the DTO
		for(int i=0;i<columnNames.length;i++){
			dto.setProperty(columnNames[i], rs.getObject(i+1));
		}

		return dto;
	}



}
