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

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

/**
 * Wrapper class for arbitrary data types that will be bound to a {@link PreparedStatement}
 *
 * @author Tauasa Timoteo
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
		switch (type) {
			case Types.VARCHAR:
				stmt.setString(index, value==null ? null : value.toString());
				break;
			case Types.DATE:
				stmt.setDate(index, value==null ? null : new java.sql.Date(((Date)value).getTime()));
				break;
			case Types.TIMESTAMP:
				stmt.setTimestamp(index, value==null ? null : new java.sql.Timestamp(((Date)value).getTime()));
				break;
			case Types.INTEGER:
				stmt.setInt(index, ((Number)value).intValue());
				break;
			case Types.FLOAT:
				stmt.setFloat(index, ((Number)value).floatValue());
				break;
			case Types.DOUBLE:
				stmt.setDouble(index, ((Number)value).doubleValue());
				break;
			case Types.DECIMAL:
				if(value instanceof BigDecimal){
					
					stmt.setBigDecimal(index, (BigDecimal)value);
					
				}else if(value == null){
					
					stmt.setNull(index, Types.NUMERIC);
					
				}else{
					
					stmt.setDouble(index, ((Number)value).doubleValue());
					
				}
				break;
			default:
				stmt.setObject(index, value, type);
				break;
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
		return new XArg(value, Types.INTEGER);
	}

	public static XArg create(long value){
		return new XArg(value, Types.BIGINT);
	}

	public static XArg create(float value){
		return new XArg(value, Types.FLOAT);
	}

	public static XArg create(double value){
		return new XArg(value, Types.DOUBLE);
	}

	public int getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	public final void setType(int i) {
		type = i;
	}

	public final void setValue(Object object) {
		value = object;
	}

}
