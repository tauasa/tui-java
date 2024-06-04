/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.jdbc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

/**
 * Wrapper class for arbitrary data types that will be bound to a {@link PreparedStatement}
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class XArg implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * SQL type as defined in {@link Types}
	 * */
	protected int type;
	protected Object value;

	public XArg(Object value, int type) {
		setValue(value);
		setType(type);

	}

	@Override
	public String toString(){
		String val = value==null ? "null" : value.toString();
		if(val.length() > 255){
			val = val.substring(0, 254) + "[trimmed]";
		}
		return new StringBuilder()
		.append("XArg[type: ").append(JDBCUtils.getTypeName(type)).append(", ")
		.append("value: \"").append(val).append("\"]").toString();
	}

	/**
	 * Binds this argument to the specified PreparedStatement
	 * */
	public void bind(PreparedStatement stmt, int index)throws SQLException{
		if(type==Types.VARCHAR){

			stmt.setString(index, value==null ? null : value.toString());

		}else if(type==Types.DATE){

			stmt.setDate(index, value==null ? null : new java.sql.Date(((Date)value).getTime()));

		}else if(type==Types.TIMESTAMP){

			stmt.setTimestamp(index, value==null ? null : new java.sql.Timestamp(((Date)value).getTime()));

		}else if(type==Types.INTEGER){

			stmt.setInt(index, ((Number)value).intValue());

		}else if(type==Types.FLOAT){

			stmt.setFloat(index, ((Number)value).floatValue());

		}else if(type==Types.DOUBLE){

			stmt.setDouble(index, ((Number)value).doubleValue());

		}else if(type==Types.DECIMAL){

			if(value instanceof BigDecimal){

				stmt.setBigDecimal(index, (BigDecimal)value);

			}else if(value == null){

				stmt.setNull(index, Types.NUMERIC);

			}else{

				stmt.setDouble(index, ((Number)value).doubleValue());

			}

		}else{

			stmt.setObject(index, value, type);

		}

	}

	public static XArg[] array(String... values){
		XArg[] arr = new XArg[values.length];
		for(int i=0;i<values.length;i++){
			arr[i] = string(values[i]);
		}
		return arr;
	}

	public static XArg create(BigDecimal value){
		return new XArg(value, Types.DECIMAL);
	}

	public static XArg string(String value){
		return new XArg(value, Types.VARCHAR);
	}

	public static XArg string(Serializable value){
		return new XArg(value, Types.VARCHAR);
	}

	public static XArg create(Serializable value){
		if(value instanceof Number){
			return new XArg(value, Types.INTEGER);
		}
		return new XArg(value, Types.VARCHAR);
	}

	public static XArg date(Date value){
		return new XArg(value, Types.DATE);
	}

	public static XArg timestamp(Date value){
		return new XArg(value, Types.TIMESTAMP);
	}

	public static XArg create(int value){
		return new XArg(new Integer(value), Types.INTEGER);
	}

	public static XArg create(long value){
		return new XArg(new Long(value), Types.INTEGER);
	}

	public static XArg create(float value){
		return new XArg(new Float(value), Types.FLOAT);
	}

	public static XArg create(double value){
		return new XArg(new Double(value), Types.DOUBLE);
	}

	public int getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	public void setType(int i) {
		type = i;
	}

	public void setValue(Object object) {
		value = object;
	}

}
