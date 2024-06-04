/**
 * Copyright 2014 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.tauasa.commons.jdbc.JDBCUtils;
import org.tauasa.commons.jdbi.JDBIUtils;
import org.tauasa.commons.util.Utils;

/**
 * {@link IDataModel} implementation based on the {@link AbstractTableModel}
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class TableDataModel extends AbstractTableModel implements IDataModel {

	private static final long serialVersionUID = 1L;
	protected String[] columns;
	protected Object[][] rows;
	protected int currentRow = -1;

	/**
	 * Creates a DataModel using the specified column names and rows of data
	 * */
	public TableDataModel(String[] columns, Object[][] rows) {
		this.columns=columns;
		this.rows=rows;
	}

	/**
	 * Creates a DataModel using the specified column names and initializes
	 * the model with the specified number of rows
	 * */
	public TableDataModel(String[] columns, int numRows) {
		this.columns=columns;
		rows = new Object[numRows][columns.length];
	}
	
	/**
	 * Creates a DataModel from the specified {@link List} of {@link Map}s. 
	 * This type of list is used as a ResultSet in JDBI. This constructor is 
	 * similar to the {@link TableDataModel#TableDataModel(ResultSet)} constructor 
	 * in that the list is iterated over completely
	 * */
	public TableDataModel(List<Map<String, Object>> rs)throws DataModelException{
		if(Utils.isEmpty(rs)){
			throw new DataModelException("List is null");
		}
		
		//extract column names
		columns = JDBIUtils.getColumnNames(rs);
		
		//get the first row
		Map<String, Object> first = rs.get(0);
		
		//populate rows
		rows = new Object[rs.size()][first.size()];
		
		//iterate over the list...
		for(int i=0;i<rs.size();i++){
			
			//get the next row...
			Map<String, Object> row = rs.get(i);
			
			//iterate over each column in this row...
			for(int j=0;j<columns.length;j++){
				//get the value for this column and
				Object value = row.get(columns[j]);
				setValueAt(value, i, j);
			}
		}
	}

	/**
	 * Creates a DataModel from the specified {@link ResultSet}.  The specified {@link ResultSet}
	 * type must NOT be {@link ResultSet#TYPE_FORWARD_ONLY}. This constructor iterates over the 
	 * entire ResultSet twice.
	 * */
	public TableDataModel(ResultSet rs)throws SQLException, DataModelException{
		if(rs.getType() == ResultSet.TYPE_FORWARD_ONLY){
			throw new DataModelException("ResultSetType cannot be TYPE_FORWARD_ONLY");
		}

		//fetch the columns
		columns = JDBCUtils.getColumnNames(rs);

		if(rs.isBeforeFirst()){
			//move to the first row
			rs.beforeFirst();
		}

		int numRows = 0;

		while(rs.next()){
			numRows++;
		}

		rows = new Object[numRows][columns.length];

		//populate the model
		rs.beforeFirst();//go back to the beginning

		int rowIndex = 0;
		while(rs.next()){

			for(int i=0;i<columns.length;i++){

				//set the value
				setValueAt(rs.getObject(columns[i]), rowIndex, i);

			}
			rowIndex++;
		}

	}

	/////////////////////////////////////
	// Begin TableModel method impls & overrides
	/////////////////////////////////////
	public Object getValueAt(int row, int col) {
		return rows[row][col];
	}

	public int getColumnCount() {
		return columns==null ? 0 : columns.length;
	}

	public int getRowCount() {
		return rows==null ? 0 : rows.length;
	}

	@Override
	public boolean isCellEditable(int row, int col){
		return true;
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
			rows[row][col] = value;
			fireTableCellUpdated(row, col);
	}

	/////////////////////////////////////
	// End TableModel method impls & overrides
	/////////////////////////////////////

	public String[] getColumnNames() throws DataModelException {
		return columns;
	}

	public boolean moveTo(int rowNum) throws DataModelException {
		currentRow = rowNum;
		return true;
	}

	public boolean reset() throws DataModelException {
		currentRow = 0;
		return true;
	}

	public boolean next() throws DataModelException {
		if(currentRow+1 > rows.length-1){
			return false;
		}
		currentRow++;
		return true;
	}

	public boolean previous() throws DataModelException {
		if(currentRow==0){
			return false;
		}
		currentRow--;
		return true;
	}

	public boolean last() throws DataModelException {
		currentRow = rows.length-1;
		return true;
	}

	public boolean first() throws DataModelException {
		return reset();
	}

	public int size() throws DataModelException {
		return getRowCount();
	}

	protected int getColumnIndex(String columnName)throws DataModelException{
		for(int i=0;i<columns.length;i++){
			if(columns[i].equals(columnName)){
				return i;
			}
		}
		throw new DataModelException("Column not found: "+columnName);
	}

	public String getString(String name) throws DataModelException {
		return getString(getColumnIndex(name));
	}

	public String getString(int column) throws DataModelException {
		Object value = getValueAt(currentRow, column);
		return value==null ? null : value.toString();
	}

	public Number getNumber(String name)throws DataModelException{
		return getNumber(getColumnIndex(name));
	}

	public Number getNumber(int column)throws DataModelException{
		return (Number)getValueAt(currentRow, column);
	}

	public int getInt(String name) throws DataModelException {
		return getInt(getColumnIndex(name));
	}

	public int getInt(int column) throws DataModelException {
		return getNumber(column).intValue();
	}

	public long getLong(String name) throws DataModelException {
		return getLong(getColumnIndex(name));
	}

	public long getLong(int column) throws DataModelException {
		return getNumber(column).longValue();
	}

	public float getFloat(String name)throws DataModelException{
		return getFloat(getColumnIndex(name));
	}

	public float getFloat(int column)throws DataModelException{
		return getNumber(column).floatValue();
	}

	public double getDouble(String name)throws DataModelException{
		return getDouble(getColumnIndex(name));
	}

	public double getDouble(int column)throws DataModelException{
		return getNumber(column).doubleValue();
	}

	public Date getDate(String name) throws DataModelException {
		return getDate(getColumnIndex(name));
	}

	public Date getDate(int column) throws DataModelException {
		return (Date)getValueAt(currentRow, column);
	}

	public Object getObject(String name) throws DataModelException {
		return getObject(getColumnIndex(name));
	}

	public Object getObject(int column) throws DataModelException {
		return getValueAt(currentRow, column);
	}

}
