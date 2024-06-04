/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
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
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
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
