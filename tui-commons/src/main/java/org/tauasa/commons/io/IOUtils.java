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
package org.tauasa.commons.io;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tauasa.commons.util.Utils;
	

/**
 * Static helper methods for performing I/O operations
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=org.tauasa.commons.io.IOUtils">tauasa@gmail.com</a>
 */
public class IOUtils extends Utils{

	private static final Logger logger = LoggerFactory.getLogger(IOUtils.class);
	
	public static final int DEFAULT_BUFFER_SIZE = 32768;
	
	/**
	 * Reads the specified {@link InputStream} into memory then 
	 * returns the data as a byte array 
	 * */
	public static byte[] read(InputStream in)throws IOException{
		XInputStreamReader reader = new XInputStreamReader(in);
		return reader.read();
	}
	
	/**
	 * Pipes the specified {@link InputStream} to the specified {@link OutputStream} 
	 * using the specified buffer size
	 * */
	public static void pipe(InputStream in, OutputStream out, int bufferSize)throws IOException{
		byte[] buffer = new byte[bufferSize];
		int i;
		while((i=in.read(buffer)) != -1){
			out.write(buffer, 0, i);
		}
	}
	
	/**
	 * Opens a {@link FileInputStream} to the specified path
	 * */
	public static InputStream open(String path)throws IOException{
		return new FileInputStream(path);
	}
	
	/**
	 * Pipes the specified {@link InputStream} to the specified {@link OutputStream}
	 * */
	public static void pipe(InputStream in, OutputStream out)throws IOException{
		pipe(in, out, DEFAULT_BUFFER_SIZE);
	}
	
	/**
	 * Closes the specified {@link Closeable} swallowing any {@link Throwable}s
	 * */
	public static void closeIgnoringException(Closeable c){
		if(c==null){
			return;
		}
		try{
			c.close();
		}catch(Throwable e){
			if(logger.isDebugEnabled()){
				logger.debug("[SWALLOWED] Could not close "+c.getClass().getName(), e);
			}
		}
	}
	
	/**
	 * Closes the specified {@link AutoCloseable} swallowing any {@link Throwable}s
	 * */
	public static void closeIgnoringException(AutoCloseable c){
		if(c==null){
			return;
		}
		try{
			c.close();
		}catch(Throwable e){
			if(logger.isDebugEnabled()){
				logger.debug("[SWALLOWED] Could not close "+c.getClass().getName(), e);
			}
		}
	}
	
}
