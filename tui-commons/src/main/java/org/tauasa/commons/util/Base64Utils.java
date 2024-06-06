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

import org.apache.commons.codec.binary.Base64;

/**
 * Simple Base64 encoder/decoder utility backed by {@link Base64} in the Apache Commons Codec project
 *
 * @author Tauasa Timoteo
 * 
 */
public class Base64Utils extends Utils {

	/**
	 * Converts the specified string to a base64-encoded string. Any trailing CR and/or LF are removed
	 * */
	public static String encode(String plain){
		String enc = Base64.encodeBase64String(plain.getBytes());
		if(enc.endsWith("\n")){
			enc = enc.replace("\n", "");
		}
		if(enc.endsWith("\r")){
			enc = enc.replace("\r", "");
		}
		return enc;
	}
	
	public static String decode(String encoded){
		return new String(Base64.decodeBase64(encoded));
	}
}
