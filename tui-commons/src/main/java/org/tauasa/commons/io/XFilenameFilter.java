/*
 * Copyright 2012 Tauasa Timoteo
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation 
 * files (the “Software”), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-
 * INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN 
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF 
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 */
package org.tauasa.commons.io;

import java.io.File;
import java.io.FilenameFilter;

/**
 * {@link FilenameFilter} implementation that filters on file name 
 * using a prefix and suffix
 *
 * @author Tauasa Timoteo
 * 
 */
public class XFilenameFilter implements FilenameFilter {

	protected String prefix, suffix;

	public XFilenameFilter(String prefix, String suffix) {
		this.prefix=prefix;
		this.suffix=suffix;
	}

        @Override
	public boolean accept(File dir, String fileName) {
		if(prefix!=null && suffix!=null){

			return fileName.startsWith(prefix) && fileName.endsWith(suffix);

		}else if(prefix!=null){

			return fileName.startsWith(prefix);

		}else if(suffix!=null){

			return fileName.endsWith(suffix);

		}
		return true;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
}
