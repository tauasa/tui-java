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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Generate a hash using a specific digest
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class MessageDigestUtils {

	//"MD2", "MD5", "SHA", "SHA-1", "SHA-256", "SHA-384", "SHA-512"
	public static enum Digest{
		/**
		 * MD2 digest that generates a 32-character hash
		 * */
		MD2("MD2"),
		/**
		 * MD5 digest that generates a 32-character hash
		 * */
		MD5("MD5"),
		/**
		 * SHA digest that generates a 40-character hash (synonymous with SHA-1)
		 * */
		SHA("SHA"),
		/**
		 * SHA-1 digest that generates a 40-character hash (synonymous with SHA)
		 * */
		SHA_1("SHA-1"),
		/**
		 * SHA-256 digest that generates a 64-character hash
		 * */
		SHA_256("SHA-256"),
		/**
		 * SHA-384 digest that generates a 96-character hash
		 * */
		SHA_384("SHA-384"),
		/**
		 * SHA-512 digest that generates a 128-character hash
		 * */
		SHA_512("SHA-512");
		private String sysName;
		Digest(String sysName){
			this.sysName=sysName;
		}
		public String sysName(){
			return sysName;
		}
		public static Digest forSysName(String s){
			for (Digest d : values()) {
				if(d.sysName().equals(s)){
					return d;
				}
			}
			return null;
		}
	}
	
	/*
	public static String generateMD5Hash(String plainText)throws NoSuchAlgorithmException{
		return generateHash(plainText, Digest.MD5);
	}

	public static String generateSHAHash(String plainText)throws NoSuchAlgorithmException{
		return generateHash(plainText, Digest.SHA);
	}//*/
	
	/**
	 * Hashes the specified {@link String} using the specified {@link Digest}
	 * */
	public static String generateHash(String plainText, Digest digest)throws NoSuchAlgorithmException{
		MessageDigest md = MessageDigest.getInstance(digest.sysName());
		byte[] output = md.digest(plainText.getBytes());
		StringBuilder sb = new StringBuilder(2*output.length);
		for (int i=0; i<output.length; ++i) {
			int k = output[i] & 0xFF;
			if (k<0x10) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(k));
		}
		return sb.toString();
	}
}
