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

import java.io.File;
import java.io.FileFilter;

/**
 * {@link FileFilter} implementation that filters on file name
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class XFileFilter extends XFilenameFilter implements FileFilter{

	/**
	 * Includes matching patterns
	 * */
	public static final int MODE_INCLUDE = 0;
	/**
	 * Excludes matching patterns
	 * */
	public static final int MODE_EXCLUDE = 1;

	protected boolean recurse = false;

	protected int mode = MODE_INCLUDE;

	public XFileFilter(String prefix, String suffix){
		super(prefix, suffix);
	}

	public XFileFilter(String prefix, String suffix, int mode){
		this(prefix, suffix);
		this.mode=mode;
	}

	public XFileFilter(String prefix, String suffix, boolean recurse){
		this(prefix, suffix);
		this.recurse=recurse;
	}

	public XFileFilter(String prefix, String suffix, int mode, boolean recurse){
		this(prefix, suffix, mode);
		this.recurse=recurse;
	}

	/**
	 * Returns true if the prefix and suffix match the beginning and
	 * ending of the specified file's name OR if recurse is true
	 * */
	public boolean accept(File file){
		if(file.isDirectory() && recurse){
			return true;
		}

		boolean accept = true;

		String name = file.getName();

		if(prefix!=null && suffix!=null){

			accept = name.startsWith(prefix) && name.endsWith(suffix);

		}else if(prefix!=null){

			accept = name.startsWith(prefix);

		}else if(suffix!=null){

			accept = name.endsWith(suffix);

		}

		return mode==MODE_INCLUDE ? accept : !accept;

	}
	
}
