/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.io;

import java.io.File;
import java.io.FilenameFilter;

/**
 * {@link FilenameFilter} implementation that filters on file name 
 * using a prefix and suffix
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class XFilenameFilter implements FilenameFilter {

	protected String prefix, suffix;

	public XFilenameFilter(String prefix, String suffix) {
		setPrefix(prefix);
		setSuffix(suffix);
	}

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
