/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.util;

import org.apache.commons.codec.binary.Base64;

/**
 * Simple Base64 encoder/decoder utility backed by {@link Base64} in the Apache Commons Codec project
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
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
