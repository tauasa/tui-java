/**
 * Copyright 2014 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.data;

import java.io.Serializable;
import java.util.Date;

/**
 * DataModel contract
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public interface IDataModel extends Serializable {

	/**
	 * Returns the number of columns in DataModel implementation
	 * */
	public int getColumnCount()throws DataModelException;

	/**
	 * Returns the column names as an array of String objects
	 * */
	public String[] getColumnNames()throws DataModelException;

	/**
	 * Moves to the specified row of data
	 * */
	public boolean moveTo(int rowNum)throws DataModelException;

	/**
	 * Moves to the first row of data
	 * */
	public boolean reset()throws DataModelException;

	/**
	 * Returns true if there is another row of data and moves to the next
	 * */
	public boolean next()throws DataModelException;

	/**
	 * Returns true if there is a previous row of data and moves to the previous row
	 * */
	public boolean previous()throws DataModelException;

	/**
	 * Moves to the last row of data and returns true if the operation was completed
	 * */
	public boolean last()throws DataModelException;

	/**
	 * Moves to the first row of data and returns true if the operation was completed
	 * */
	public boolean first()throws DataModelException;

	/**
	 * Returns the size of this data iterator
	 * */
	public int size()throws DataModelException;

	public String getString(String name)throws DataModelException;

	public String getString(int column)throws DataModelException;

	public int getInt(String name)throws DataModelException;

	public int getInt(int column)throws DataModelException;

	public long getLong(String name)throws DataModelException;

	public long getLong(int column)throws DataModelException;

	public float getFloat(String name)throws DataModelException;

	public float getFloat(int column)throws DataModelException;

	public double getDouble(String name)throws DataModelException;

	public double getDouble(int column)throws DataModelException;

	public Date getDate(String name)throws DataModelException;

	public Date getDate(int column)throws DataModelException;

	public Object getObject(String name)throws DataModelException;

	public Object getObject(int column)throws DataModelException;

}
