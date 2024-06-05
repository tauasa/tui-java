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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pattern matcher
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class PatternUtils extends Utils {

	public static final Pattern EMAIL = Pattern.compile("[\\w\\-\\._]+@[\\w\\-_]+\\.[\\w\\-\\._]+");
	public static final Pattern SSN = Pattern.compile("[0-9]{3}\\-?[0-9]{2}\\-?[0-9]{4}");
	public static final Pattern SSN_STRICT = Pattern.compile("[0-9]{3}\\-[0-9]{2}\\-[0-9]{4}");
	public static final Pattern PHONE_FORMATTED = Pattern.compile("[0-9]{3}\\-[0-9]{3}\\-[0-9]{4}");
	public static final Pattern PHONE_PLAIN = Pattern.compile("[0-9]{10}");
	public static final Pattern ALPHA = Pattern.compile("[a-zA-Z]+");
	public static final Pattern ALPHANUMERIC = Pattern.compile("[0-9a-zA-Z]+");
	public static final Pattern NUMERIC = Pattern.compile("[\\d]+");
	public static final Pattern INTEGER = Pattern.compile("[0-9]+");

	/**
	 * Returns true if the specified {@link CharSequence} matches the specified {@link Pattern}
	 * */
	public static boolean isMatch(final CharSequence input, final Pattern p){
		if (isEmpty(input)) {
			return false;
		}
		return p.matcher(input).matches();
	}

	public static boolean isMatch(final CharSequence input, final String regex){
		if (isEmpty(input)) {
			return false;
		}
		return Pattern.matches(regex, input);
	}

	public static Matcher matcher(final CharSequence s, final Pattern p){
		if (isEmpty(s)) {
			return null;
		}
		return p.matcher(s);
	}

	/**
	 * Returns true if the specified {@link String} is a well-formed US phone number (i.e. 800-555-1212)
	 * */
	public static boolean isPhone(String s){
		return isMatch(s, PHONE_PLAIN) || isMatch(s, PHONE_FORMATTED);
	}

	/**
	 * Returns true if the specified {@link String} is a well-formed email address
	 * */
	public static boolean isEmail(String s){
		return isMatch(s, EMAIL);
	}

	/**
	 * Returns true if the specified {@link String} is a well-formed SSN (###-##-#### or #########)
	 * */
	public static boolean isSSN(String s){
		return isMatch(s, SSN) || isMatch(s, SSN_STRICT);
	}

	/**
	 * Returns true if the specified {@link String} is a well-formed SSN (###-##-####)
	 * */
	public static boolean isSSNStrict(String s){
		return isMatch(s, SSN_STRICT);
	}

	/**
	 * Returns true if the specified string contains only alpha characters
	 * */
	public static boolean isAlpha(String s){
		return isMatch(s, ALPHA);
	}

	/**
	 * Returns true if the specified string is a numeric value (floating-point or integer)
	 * */
	public static boolean isNumeric(String s){
		return isMatch(s, NUMERIC);
	}

	/**
	 * Returns true if the specified string is an integer or long value
	 * */
	public static boolean isWholeNumber(String s){
		return isMatch(s, INTEGER);
	}

	/**
	 * Convenience method synonymous with {@link #isWholeNumber(String)}
	 * */
	public static boolean isInt(String s){
		return isMatch(s, INTEGER);
	}

	/**
	 * Returns true if the specified string is alphanumeric
	 * */
	public static boolean isAlphaNumeric(String s){
		return isMatch(s, ALPHANUMERIC);
	}

}
