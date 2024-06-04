/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.tauasa.commons.util.Utils;

/**
 * {@link File} subclass that includes convenience I/O methods for
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class XFile extends File {

	private static final long serialVersionUID = 1L;

	private static final FilenameComparator COMPARATOR = new FilenameComparator();

	public XFile(String path) {
		super(path);
	}

	public XFile(File file){
		super(file.getAbsolutePath());
	}

	public XFile(String parent, String child) {
		super(parent, child);
	}

	public XFile(File parent, String child) {
		super(parent, child);
	}

	public XFile(URI uri) {
		super(uri);
	}
	
	/**
	 * Converts the specified file path to a Unix-like path by:
	 * <ol>
	 * <li>Replacing all backslashes (\) with a forward slash (/)</li>
	 * <li>Removing any leading drive letter</li>
	 * </ol>
	 * */
	public static String toUnixPath(String path){
		if(Utils.isEmpty(path)){
			return path;
		}
		path = path.trim().replace('\\', '/');
		
		if(path.matches("^[a-zA-Z]:.*")){
			path = path.substring(2);
		}
		return path;
	}
	
	public String getUnixPath(){
		return toUnixPath(super.getPath());
	}

	/**
	 * Returns true if the specified file path exists
	 * */
	public static boolean exists(String path){
		return (new XFile(path)).exists();
	}

	/**
	 * Opens an output stream to this file
	 * */
	public FileOutputStream getOutputStream()throws IOException{
		return getOutputStream(false);
	}


	public FileOutputStream getOutputStream(boolean append)throws IOException{
		return new FileOutputStream(this.getAbsolutePath(), append);
	}

	/**
	 * Returns an input stream to this file
	 * */
	public InputStream getInputStream()throws IOException{
		return new FileInputStream(this);
	}

	/**
	 * Returns the content-type of this file
	 * */
	public static String getContentType(File file)throws Exception{
		return file.toURI().toURL().openConnection().getContentType();
	}

	/**
	 * Creates a new input stream reader from this file
	 * */
	public XInputStreamReader getReader()throws IOException{
		return new FileReader(this);
	}

	/**
	 * Reads the contents of this file as an array of bytes
	 * */
	public byte[] read()throws IOException{
		return getReader().read();
	}

	/**
	 * Returns the contents of this file as a string
	 * */
	public String readString()throws IOException{
		return ((FileReader)getReader()).readString();
	}

	/**
	 * Writes the specified data to this file. If <code>append</code> is <code>false</code>
	 * the file is overwritten, otherwise it is appended to any existing file.
	 * */
	public void write(byte[] data, boolean append)throws IOException{
		if(data==null){
			throw new NullPointerException("Specified data is null");
		}
		FileOutputStream out = getOutputStream(append);
		out.write(data);
		out.close();
	}//*/

	/**
	 * Writes data to this file. Any existing data will be overwritten without warning.
	 * */
	public void write(byte[] bytes)throws IOException{
		write(bytes, false);
	}

	/**
	 * Appends data to an existing file or writes a new one if this file doesn't exist.
	 * */
	public void append(byte[] bytes)throws IOException{
		write(bytes, true);
	}

	/**
	 * Writes data to this file. Any existing data will be overwritten without warning.
	 * */
	public void write(CharSequence data)throws IOException{
		if(data==null){
			throw new NullPointerException("Specified data is null");
		}
		write(data.toString().getBytes(), false);
	}

	/**
	 * Appends data to an existing file or writes a new one if this file doesn't exist.
	 * */
	public void append(CharSequence data)throws IOException{
		if(data==null){
			throw new NullPointerException("Specified data is null");
		}
		write(data.toString().getBytes(), true);
	}

	/**
	 * Convenience method for quickly writing data to a file. This method is synonymous with:
	 * <code>new XFile(String).write(CharSequence)</code>
	 * */
	public static void write(String path, CharSequence data)throws IOException{
		new XFile(path).write(data);
	}

	/**
	 * Convenience method for quickly writing data to a file. This method is synonymous with:
	 * <code>new XFile(String).append(CharSequence)</code>
	 * */
	public static void append(String path, CharSequence data)throws IOException{
		new XFile(path).append(data);
	}

	/**
	 * Convenience method for quickly writing data to a file. This method is synonymous with:
	 * <code>new XFile(String).write(byte[])</code>
	 * */
	public static void write(String path, byte[] data)throws IOException{
		new XFile(path).write(data);
	}

	/**
	 * Convenience method for quickly writing data to a file. This method is synonymous with:
	 * <code>new XFile(String).append(byte[])</code>
	 * */
	public static void append(String path, byte[] data)throws IOException{
		new XFile(path).append(data);
	}

	/**
	 * Convenience method for quickly reading a file. This method is synonymous with:
	 * <code>new XFile(String).read()</code>
	 * */
	public static byte[] read(String path)throws IOException{
		return new XFile(path).read();
	}

	/**
	 * Convenience method for quickly reading a file. This method is synonymous with:
	 * <code>new XFile(String).readString()</code>
	 * */
	public static String readString(String path)throws IOException{
		return new XFile(path).readString();
	}

	/**
	 * Convenience method for quickly deleting a file. This method is synonymous with:
	 * <code>new XFile(String).delete()</code>
	 * */
	public static boolean delete(String path){
		return new XFile(path).delete();
	}

	/**
	 * Returns a collection of files matching the specified file filter if
	 * this file is a directory.  Returns null if this file is not a directory
	 * */
	public List<XFile> getFiles(FileFilter filter, boolean recurse){
		if(!isDirectory()){
			return null;
		}

		ArrayList<XFile> files = new ArrayList<XFile>();

		File[] list = null;

		if(filter!=null){
			list = listFiles(filter);
		}else{
			list = listFiles();
		}

		if(list==null){
			return files;
		}

		for(int i=0;i<list.length;i++){
			XFile f = new XFile(list[i]);
			if(recurse && f.isDirectory()){
				files.addAll(f.getFiles(filter, recurse));
			}else{
				files.add(f);
			}
		}

		//sort all of these files
		Collections.sort(files, COMPARATOR);

		return files;
	}

	/**
	 * Returns a collection of all the files in this directory
	 * WITHOUT recursing
	 * */
	public List<XFile> getFiles(FileFilter filter){
		return getFiles(filter, false);
	}

	/**
	 * Equivalent to getFiles(null)
	 * */
	public List<XFile> getFiles(){
		return getFiles(null);
	}

	/**
	 * Using the specified path, creates any directories that do not currently exist.
	 * This method returns true if the path already exists or, if it doesn't already
	 * exist, is successfully created.
	 * @throws IOException if the path already exists and it is NOT a directory
	 * */
	public static boolean createDirectoryPath(String path)throws IOException{
		XFile f = new XFile(path);
		if(f.exists()){
			if(!f.isDirectory()){
				throw new IOException(String.format("%s already exists and is not a directory", path));
			}
			return true;
		}
		return f.mkdirs();
	}

	/**
	 * {@link Comparator} implementation for comparing {@link File}s by name
	 * */
	public static class FilenameComparator implements Comparator<File>{
		public int compare(File f1, File f2) {
			return f1.getName().compareToIgnoreCase(f2.getName());
		}
	}

	/**
	 * {@link XInputStreamReader} subclass for reading a {@link File}
	 * */
	public static class FileReader extends XInputStreamReader{
		public FileReader(File file)throws FileNotFoundException{
			super(new FileInputStream(file));
		}

		public FileReader(String path)throws IOException{
			this(new XFile(path));
		}

		/**
		 * Returns the contents of the underlying file as a java.lang.String
		 * */
		public String readString()throws IOException{
			return new String(read());
		}
	}
}
