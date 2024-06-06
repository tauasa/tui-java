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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Convenient utility class for reading an {@link InputStream} and/or piping
 * the contents of an {@link InputStream} to a corresponding {@link OutputStream}
 *
 * @author Tauasa Timoteo
 * 
 */
public class XInputStreamReader {

	private InputStream in;
	private int bufferSize = 32768;

	public XInputStreamReader(InputStream in){
		this.in=in;
	}

	public XInputStreamReader(InputStream in, int bufferSize){
		this(in);
		this.bufferSize=bufferSize;
	}

	public static String readString(InputStream in)throws IOException{
		return new String(readBytes(in));
	}

	public static byte[] readBytes(InputStream in)throws IOException{
		XInputStreamReader reader = new XInputStreamReader(in);
		return reader.read();
	}

	/**
	 * Pipes the data from the this reader's input stream to the specified output stream
	 * */
	public void pipe(OutputStream out)throws IOException{
		byte[] buffer = new byte[bufferSize];
		int i;
		while((i=in.read(buffer)) != -1){
			out.write(buffer, 0, i);
		}
	}

	/**
	 * Reads the contents of this reader's input stream as an array of bytes
	 * */
	public byte[] read()throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		pipe(out);
		return out.toByteArray();
	}
	
}
