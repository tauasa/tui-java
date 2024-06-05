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
