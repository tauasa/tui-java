/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
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
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
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
