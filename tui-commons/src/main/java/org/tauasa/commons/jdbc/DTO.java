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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * {@link HashMap} subclass that acts as an arbitrary Data Transfer Object
 *
 * @author Tauasa Timoteo
 * 
 */
public class DTO extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	public DTO() {
		super();
	}

	public DTO(String[] propertyNames) {
		setPropertyNames(propertyNames);
	}

	public DTO(String[] propertyNames, Object[] values) {
		populate(propertyNames, values);
	}

	public final void populate(String[] propertyNames, Object[] values){
		if(propertyNames==null){
			throw new NullPointerException("propertyNames array is null");
		}else if(values==null){
			throw new NullPointerException("values array is null");
		}else if(values.length != propertyNames.length){
			throw new IllegalArgumentException("values array length ("+values.length+") does not match propertyNames length ("+propertyNames.length+")");
		}
		for(int i=0;i<propertyNames.length;i++){
			setProperty(propertyNames[i], values[i]);
		}
	}

	public final void setPropertyNames(String[] propertyNames){
		if(propertyNames==null){
			throw new NullPointerException("propertyNames array is null");
		}
		for (String propertyName : propertyNames) {
			setProperty(propertyName, null);
		}
	}

	public Object setProperty(String name, byte value){
		return setProperty(name, Byte.valueOf(value));
	}

	public Object setProperty(String name, short value){
		return setProperty(name, Short.valueOf(value));
	}

	public Object setProperty(String name, int value){
		return setProperty(name, Integer.valueOf(value));
	}

	public Object setProperty(String name, long value){
		return setProperty(name, Long.valueOf(value));
	}

	public Object setProperty(String name, float value){
		return setProperty(name, Float.valueOf(value));
	}

	public Object setProperty(String name, double value){
		return setProperty(name,  Double.valueOf(value));
	}

	public Object setProperty(String name, char value){
		return setProperty(name, Character.valueOf(value));
	}


	/**
	 * Sets the specified value as a named property and returns the
	 * previous value (which may be null)
	 * */
	public Object setProperty(String name, Object value){
		return super.put(name, value);
	}

	/**
	 * Returns the property for the specified name or null if it does
	 * not exist.
	 * */
	public Object getProperty(String name){
		return super.get(name);
	}

	/**
	 * Returns true if this DTO has the specified non-null property
	 * */
	public boolean hasProperty(String name){
		return getProperty(name)!=null;
	}

	/**
	 * Returns an array of all property names
	 * */
	public String[] getPropertyNames(){
		String[] names = new String[size()];
		Iterator<String> it = super.keySet().iterator();
		int i=0;
		while(it.hasNext()){
			names[i] = it.next();
			i++;
		}

		return names;
	}

	/**
	 * Returns a java.util.Collection of all property objects
	 * */
	public Collection<Object> getProperties(){
		return super.values();
	}




}
