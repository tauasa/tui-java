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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;

/**
 * {@link IDataModel} implementation that provides data from an underlying {@link ResultSet}. The
 * underlying ResultSet must remain open while using instances of this class.  Also, this
 * DataModel allows a ResultSet to be traversed as a zero-based array (instead of one-based).
 * <P>
 * <b>NOTE:</b> To enable traversal, the target {@link ResultSet} must be of type {@link ResultSet#TYPE_SCROLL_INSENSITIVE}
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class ResultSetDataModel implements IDataModel {

	private static final long serialVersionUID = 1L;
	protected ResultSet resultSet;
	protected ResultSetMetaData metaData;
	private int size = -1;

	public ResultSetDataModel(ResultSet rs)throws DataModelException{
		resultSet=rs;
		moveTo(-1);
	}

	/**
	 * Returns the column names from the ResultSetMetaData
	 * */
	public String[] getColumnNames()throws DataModelException{

		try{

			int count = getColumnCount();

			String[] names = new String[count];

			for(int i=0;i<count;i++){
				names[i] = getMetaData().getColumnName(i+1);
			}

			return names;

		}catch(SQLException e){
			throw new DataModelException(e);
		}
	}

	public int getColumnCount()throws DataModelException{
		try{
			return getMetaData().getColumnCount();
		}catch(SQLException e){
			throw new DataModelException(e);
		}
	}

	public ResultSetMetaData getMetaData()throws DataModelException{
		try{
			if(metaData==null){
				metaData = resultSet.getMetaData();
			}
		}catch(SQLException e){
			throw new DataModelException(e);
		}
		return metaData;
	}

	public boolean moveTo(int rowNum)throws DataModelException{
		try{
			if(rowNum < 0){
				resultSet.beforeFirst();
				return true;
			}
			return resultSet.absolute(rowNum+1);
		}catch(SQLException e){
			throw new DataModelException(e);
		}
	}

	public boolean reset()throws DataModelException{
		metaData = null;
		size = -1;
		return moveTo(-1);
	}

	public boolean next() throws DataModelException {
		try{
			return resultSet.next();
		}catch(SQLException e){
			throw new DataModelException(e);
		}
	}

	public boolean previous() throws DataModelException {
		try{
			return resultSet.previous();
		}catch(SQLException e){
			throw new DataModelException(e);
		}
	}

	public boolean last() throws DataModelException {
		try{
			return resultSet.last();
		}catch(SQLException e){
			throw new DataModelException(e);
		}
	}

	public boolean first() throws DataModelException {
		try{
			return resultSet.first();
		}catch(SQLException e){
			throw new DataModelException(e);
		}
	}

	public int size() throws DataModelException {
		if(size!=-1){
			return size;
		}
		try{
			//store the current row number (remember
			//we are operating with a zero-based index)
			int current = resultSet.getRow() - 1;

			//move to the last row and return the row number
			last();

			//store the row number
			size = resultSet.getRow();

			//move the cursor back to where we were
			moveTo(current);

			//return the count
			return size;

		}catch(SQLException e){
			throw new DataModelException(e);
		}
	}

	public String getString(String name) throws DataModelException {
		try{
			return resultSet.getString(name);
		}catch(SQLException e){
			throw new DataModelException(e);
		}
	}

	public String getString(int column) throws DataModelException {
		try{
			return resultSet.getString(column+1);
		}catch(SQLException e){
			throw new DataModelException(e);
		}
	}

	public int getInt(String name) throws DataModelException {
		try{
			return resultSet.getInt(name);
		}catch(SQLException e){
			throw new DataModelException(e);
		}
	}

	public int getInt(int column) throws DataModelException {
		try{
			return resultSet.getInt(column+1);
		}catch(SQLException e){
			throw new DataModelException(e);
		}
	}

	public long getLong(String name) throws DataModelException {
		try{
			return resultSet.getLong(name);
		}catch(SQLException e){
			throw new DataModelException(e);
		}
	}

	public long getLong(int column) throws DataModelException {
		try{
			return resultSet.getLong(column+1);
		}catch(SQLException e){
			throw new DataModelException(e);
		}
	}

	public float getFloat(String name)throws DataModelException{
		try{
			return resultSet.getFloat(name);
		}catch(SQLException e){
			throw new DataModelException(e);
		}
	}

	public float getFloat(int column)throws DataModelException{
		try{
			return resultSet.getFloat(column+1);
		}catch(SQLException e){
			throw new DataModelException(e);
		}
	}

	public double getDouble(String name)throws DataModelException{
		try{
			return resultSet.getDouble(name);
		}catch(SQLException e){
			throw new DataModelException(e);
		}
	}

	public double getDouble(int column)throws DataModelException{
		try{
			return resultSet.getDouble(column+1);
		}catch(SQLException e){
			throw new DataModelException(e);
		}
	}

	public Date getDate(String name) throws DataModelException {
		try{
			return resultSet.getDate(name);
		}catch(SQLException e){
			throw new DataModelException(e);
		}
	}

	public Date getDate(int column) throws DataModelException {
		try{
			return resultSet.getDate(column+1);
		}catch(SQLException e){
			throw new DataModelException(e);
		}
	}

	public Object getObject(String name) throws DataModelException {
		try{
			return resultSet.getObject(name);
		}catch(SQLException e){
			throw new DataModelException(e);
		}
	}

	public Object getObject(int column) throws DataModelException {
		try{
			return resultSet.getObject(column+1);
		}catch(SQLException e){
			throw new DataModelException(e);
		}
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

}
