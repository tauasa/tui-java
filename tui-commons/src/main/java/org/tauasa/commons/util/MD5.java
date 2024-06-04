/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.util;


/**
 * Simple hash utility for generating an MD5 hash
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class MD5 {
	
	/**
	 * Generates an MD5 hash from the specified {@link String}
	 * */
	public static String hash(String s){
		try{
			return MessageDigestUtils.generateHash(s, MessageDigestUtils.Digest.MD5);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
}
