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
package org.tauasa.commons.data;

import java.io.Serializable;
import java.util.Date;

/**
 * DataModel contract
 *
 * @author Tauasa Timoteo
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
