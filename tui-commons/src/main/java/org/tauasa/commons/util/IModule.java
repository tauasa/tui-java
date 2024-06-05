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

import java.math.BigDecimal;
import java.util.Date;

/**
 * Defines a general means for accessing system properties in an 
 * application- or module-specific way. This interface can be implemented by
 * an {@link Enum} that defines the keys and default values for all
 * application-/module-specific properties. Convenience methods are also
 * defined so system properties can be easily accessed as a specific type.
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 */
public interface IModule {

	/**
	 * String value that identifies the underlying property (usually a system property)
	 * */
	public String getKey();

	/**
	 * Returns a description of the implementing enum
	 * */
	public String getDescription();

	/**
	 * Returns the value as a java.lang.String
	 * */
	public String getStringValue();
	//public String getStringValue(String defaultValue);

	/**
	 * Returns the values as a boolean
	 * */
	public boolean getBooleanValue();
	//public boolean getBooleanValue(boolean defaultValue);

	/**
	 * Returns the value as a int
	 * */
	public int getIntValue();
	//public int getIntValue(int defaultValue);

	/**
	 * Returns the value as a long
	 * */
	public long getLongValue();
	//public long getLongValue(long defaultValue);

	/**
	 * Returns the value as a float
	 * */
	public float getFloatValue();
	//public float getFloatValue(float defaultValue);

	/**
	 * Returns the value as a double
	 * */
	public double getDoubleValue();

	/**
	 * Returns the value as a BigDecimal
	 * */
	public BigDecimal getBigDecimalValue();

	/**
	 * Returns the value as a java.util.Date
	 * */
	public Date getDateValue();

	/**
	 * Returns the value as a java.lang.String[]
	 * */
	public String[] getStringArrayValue();

}
