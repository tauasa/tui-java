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
package org.tauasa.commons.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link Properties} subclass that includes methods for type casting 
 * and property replacement
 *
 * @author Tauasa Timoteo
 * 
 */
public class XProperties extends Properties {

	private static final long serialVersionUID = 1L;
	/**
	 * {@link Pattern} used to find property value(s) containing <code>${value}</code> (Regex: <code>\$\{\S+?\}</code>)
	 * */
	public static final Pattern FIND_NESTED_PROPERTY_PATTERN = Pattern.compile("\\$\\{\\S+?\\}");
	/**
	 * {@link Pattern} used to extract the alphanumeric content of a tag like <code>${value}</code> (Regex: <code>\$\{(.*?)\}</code>)
	 * */
	public static final Pattern EXTRACT_NESTED_PROPERTY_PATTERN = Pattern.compile("\\$\\{(.*?)\\}");

	static final String DEFAULT_ARRAY_DELIMITER = ",";

	public XProperties() {
		super();
	}

	public XProperties(Properties defaults) {
		super(defaults);
	}
	
	public static List<String> getSortedKeys(Properties props){
		List<String> keys = new ArrayList<>();

		Iterator<Object> names = props.keySet().iterator();
		//sort the keys
		while(names.hasNext()){
			keys.add(names.next().toString());
		}

		Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);

		return keys;
	}
	
	public List<String> getSortedKeys(){
		return getSortedKeys(this);
	}
	
	/**
	 * Returns a {@link List} of keys matching {@code regex} in the specified 
	 * {@link Properties} object
	 * */
	public static List<String> getMatchingKeys(Properties props, String regex){
		List<String> matches = new ArrayList<>();

		Iterator<Object> keys = props.keySet().iterator();
		
		//grab keys that match our regex
		while(keys.hasNext()){
			String key = keys.next().toString();
			if(PatternUtils.isMatch(key, regex)){
				matches.add(key);
			}
		}

		Collections.sort(matches, String.CASE_INSENSITIVE_ORDER);

		return matches;
	}
	
	/**
	 * Returns a {@link List} of keys matching {@code regex}
	 * */
	public List<String> getMatchingKeys(String regex){
		return getMatchingKeys(this, regex);
	}

	/**
	 * Returns the property for the specified key as an array of strings
	 * split along the specified delimiter.
	 * */
	public String[] getPropertyArray(String key, String delimiter){
		String value = getProperty(key);
		if(value==null){
			return null;
		}
		if(delimiter==null){
			throw new NullPointerException("delimiter is null");
		}
		return Utils.split(value, delimiter);
	}

	/**
	 * Returns the property for the specified key as an array of strings
	 * split along DEFAULT_ARRAY_DELIMITER
	 * */
	public String[] getPropertyArray(String key){
		return getPropertyArray(key, DEFAULT_ARRAY_DELIMITER);
	}

	/**
	 * Dumps this objects properties to STDOUT
	 * */
	public void dump(){
		list(System.out);
	}

	/**
	 * Returns true if the value for the specified key is not null
	 * */
	public boolean hasProperty(String key){
		return get(key)!=null;
	}

	/**
	 * Returns a property value as a Number object
	 * */
	public Number getNumberProperty(String key){
		return getDoubleProperty(key);
	}

	/**
	 * Returns a property value as a double primitive
	 * */
	public double getDoubleProperty(String key){
		if(!hasProperty(key)){
			return 0;
		}
		return Double.parseDouble(getProperty(key));
	}

	/**
	 * Returns a property value as a float primitive
	 * */
	public float getFloatProperty(String key){
		if(!hasProperty(key)){
			return 0;
		}
		return Float.parseFloat(getProperty(key));
	}

	/**
	 * Returns a property value as a long primitive
	 * */
	public long getLongProperty(String key){
		if(!hasProperty(key)){
			return 0;
		}
		return Long.parseLong(getProperty(key));
	}

	/**
	 * Returns a property value as an int primitive
	 * */
	public int getIntProperty(String key){
		if(!hasProperty(key)){
			return 0;
		}
		return Integer.parseInt(getProperty(key));
	}

	/**
	 * Returns a property value as a boolean primitive.  If the property
	 * is undefined (null) then this methods returns the specified
	 * default value
	 * */
	public boolean getBooleanProperty(String key, boolean _default){
		if(!hasProperty(key)){
			return _default;
		}
		return "true".equalsIgnoreCase(getProperty(key));

	}

	/**
	 * Returns a property value as a boolean primitive. If the
	 * property is undefined (null) then this method returns false.
	 * */
	public boolean getBooleanProperty(String key){
		return getBooleanProperty(key, false);
	}

	/**
	 * Sets an int value under the specified key
	 * */
	public void setIntProperty(String key, int value){
		setProperty(key, String.valueOf(value));
	}

	/**
	 * Sets an long value under the specified key
	 * */
	public void setLongProperty(String key, long value){
		setProperty(key, String.valueOf(value));
	}

	/**
	 * Sets an double value under the specified key
	 * */
	public void setDoubleProperty(String key, double value){
		setProperty(key, String.valueOf(value));
	}

	/**
	 * Sets a boolean value under the specified key
	 * */
	public void setBooleanProperty(String key, boolean value){
		setProperty(key, String.valueOf(value));
	}

	/**
	 * Overrides default behavior by allowing a null value (which is
	 * stored as a XProperties.Null object)
	 * */
	@Override
	public Object put(Object key, Object value){
		return super.put(key, value==null ? new Null() : value);
	}

	/**
	 * Overrides default behavior - if the value referenced by <i>key</i> is
	 * null or a XProperties.Null object then a null value is returned.
	 * */
	@Override
	public Object get(Object key){
		Object obj = super.get(key);
		return (obj==null || (obj instanceof Null)) ? null : obj;
	}

	/**
	 * Creates an XProperties object from a standard Java properties file
	 * */
	public static final XProperties loadProperties(InputStream in)throws IOException{
		XProperties p = new XProperties();
		p.load(in);
		return p;
	}

	class Null implements Serializable{
		private static final long serialVersionUID = 1L;
	}

	/**
	 * Returns the trimmed string value for the specified key or null
	 * if it does not exist
	 * */
	@Override
	public String getProperty(String key){
		String val = super.getProperty(key);
		if(val==null){
			return null;
		}
		return populateSystemProperties(val.trim());
	}


	/**
	 * Using the specified {@link String} replaces all instances of <code>${FOO}</code> with <code>System.getProperty("FOO")</code>
	 * */
	public static String populateSystemProperties(String s){
		StringBuffer buffer = new StringBuffer();
		Matcher findExtractor = FIND_NESTED_PROPERTY_PATTERN.matcher(s);
		while (findExtractor.find()) {
			String nestedGroup = findExtractor.group();
			Matcher extractMatcher = EXTRACT_NESTED_PROPERTY_PATTERN.matcher(nestedGroup);
			while (extractMatcher.find()) {
				String sysProperty = extractMatcher.group(1);
				findExtractor.appendReplacement(buffer, System.getProperty(sysProperty));//replace ${N} with System.getProperty(N)
			}
		}
		findExtractor.appendTail(buffer);
		return buffer.toString();
	}

	/**
	 * Returns this properties object as an XML string
	 * */
	public String getXml(String comment)throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		super.storeToXML(out, comment);
		return new String(out.toByteArray());
	}
	   
}
