/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.util;

import org.tauasa.commons.util.MessageDigestUtils.Digest;


/**
 * Simple hash utility for generating various SHA hashes
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class SHA {
	
	/**
	 * Generates an SHA-1 hash from the specified {@link String}
	 * */
	public static String hash(String s){
		return hash(s, Digest.SHA);
	}
	
	/**
	 * Generates an SHA-256 hash from the specified {@link String}
	 * */
	public static String hash256(String s){
		return hash(s, Digest.SHA_256);
	}
	
	/**
	 * Generates an SHA-384 hash from the specified {@link String}
	 * */
	public static String hash384(String s){
		return hash(s, Digest.SHA_384);
	}
	
	/**
	 * Generates an SHA-512 hash from the specified {@link String}
	 * */
	public static String hash512(String s){
		return hash(s, Digest.SHA_512);
	}
	
	private static String hash(String s, Digest digest){
		try{
			return MessageDigestUtils.generateHash(s, digest);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
}
