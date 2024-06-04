/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * {@link HashMap} subclass that acts as an arbitrary Data Transfer Object
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
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

	public void populate(String[] propertyNames, Object[] values){
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

	public void setPropertyNames(String[] propertyNames){
		if(propertyNames==null){
			throw new NullPointerException("propertyNames array is null");
		}
		for(int i=0;i<propertyNames.length;i++){
			setProperty(propertyNames[i], null);
		}
	}

	public Object setProperty(String name, byte value){
		return setProperty(name, new Byte(value));
	}

	public Object setProperty(String name, short value){
		return setProperty(name, new Short(value));
	}

	public Object setProperty(String name, int value){
		return setProperty(name, new Integer(value));
	}

	public Object setProperty(String name, long value){
		return setProperty(name, new Long(value));
	}

	public Object setProperty(String name, float value){
		return setProperty(name, new Float(value));
	}

	public Object setProperty(String name, double value){
		return setProperty(name, new Double(value));
	}

	public Object setProperty(String name, char value){
		return setProperty(name, new Character(value));
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
			names[i] = it.next().toString();
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
