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

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.google.common.base.Throwables;

/**
 * Static utility methods
 *
 * @author tauasa@gmail dot com
 * 
 */
public class Utils {
	
	public static final String UTF8 = "UTF-8";
	public static final byte[] NEWLINE = "\r\n".getBytes();
	public static final String WHITESPACE = " ";
	public static final byte[] TAB = "\t".getBytes();
	public static final byte[] COMMA = ",".getBytes();
	public static final String AMP = "&";
	public static final String AMP_ENCODED = "&amp;";
	public static final String NON_ALPHANUMERIC = "[^a-zA-Z0-9]";
	public static final String NON_NUMERIC = "[^\\d]";

	/**
	 * Factory method for creating a {@link Map} initialized with
	 * the specified key and value
	 * */
	public static <K, V> Map<K, V> createMap(K key, V value){
		HashMap<K, V> map = new HashMap<>();
		map.put(key, value);
		return map;
	}

	/**
	 * Creates a {@link List} of {@link Integer}s with the specified range
	 * */
	public static List<Integer> range(int start, int end){
		return range(start, end, 1);
	}

	/**
	 * Removes every occurrence of the specified tokens from the specified string
	 * */
	public static String remove(String s, String[] tokens){
		if(s==null || tokens==null){
			return s;
		}
		for(String t : tokens){
			s = replace(s, t, "");
		}
		return s;
	}

	public static List<Integer> range(int start, int end, int step){
		if(start >= end || (end-start) < step){
			throw new IllegalArgumentException(
					String.format("start (%d) must be < end (%d) by at least the specified step (%d)",
							start, end, step));
		}
		ArrayList<Integer> list = new ArrayList<>();

		for(int i = start; i < end+step ; i += step){
			list.add(i);
		}
		return list;
	}

	public static Number sum(List<? extends Number> list){
		BigDecimal total = BigDecimal.ZERO;
		for (Number number : list) {
			total = total.add(new BigDecimal(number.doubleValue()));
		}
		return total;
	}

	/**
	 * Factory method for creating a {@link Map} initialized with
	 * the specified keys and values
	 * */
	public static <K, V> Map<K, V> createMap(K[] key, V[] value){
		HashMap<K, V> map = new HashMap<>();
		for(int i=0;i<key.length;i++){
			map.put(key[i], value[i]);
		}
		return map;
	}

	/**
	 * Returns true if the specified {@link CharSequence} is null or empty
	 * */
	public static final boolean isEmpty(CharSequence s){
		return s==null || s.length() == 0;
	}

	/**
	 * Returns true if the specified {@link String} is null or has no non-whitespace characters
	 * */
	public static final boolean isEmpty(String s){
		return s==null || s.trim().length() == 0;
	}

	public static String createString(char c, int len){
		StringBuilder b = new StringBuilder();
		for(int i=0;i<len;i++){
			b.append(c);
		}
		return b.toString();
	}

	/**
	 * Returns true if the specified Collection is null or empty
	 * */
	public static final boolean isEmpty(Collection<?> c){
		return (c==null || c.isEmpty());
	}

	/**
	 * Returns true if the specified List is null or empty
	 * */
	public static final boolean isEmpty(List<?> list){
		return (list==null || list.isEmpty());
	}

	/**
	 * Returns true if the specified Map is null or empty
	 * */
	public static final boolean isEmpty(Map<?, ?> map){
		return (map==null || map.isEmpty());
	}

	/**
	 * Returns true if the specified array is null or zero-length
	 * */
	public static final boolean isEmpty(Object[] arr){
		return (arr==null || arr.length == 0);
	}

	/////////////////////////////////////
	// String methods
	/////////////////////////////////////

	/**
	 * Converts a {@link String} to title case. Example: "TAUASA", "tauasa", or "taUasA" would return "Tauasa".
	 * A null or empty/zero-length string (after invoking {@link String#trim()}) is returned unchanged. Multiple
	 * words will be returned in title case separated by a single space regardless of the amount of whitespace
	 * separating tokens in the original string.
	 * */
	public static String titleCase(String string){
		if(isEmpty(string)){
			return string;
		}
		string = string.trim();
		if(string.length()==1){
			return string.toUpperCase();
		}
		String[] arr = split(string.toLowerCase(), " ");
		StringBuilder b = new StringBuilder();
		for (int i=0;i<arr.length;i++) {
			String s = arr[i];
			b.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1));
			if(i!=arr.length-1){
				b.append(" ");
			}
		}
		return b.toString();
	}

	/**
	 * Left-pads a {@link String} with the specified character to the specified width/length
	 * */
	public static String leftPad(String s, int width, char c) {
		return String.format("%" + width + "s", s).replace(' ', c);
	}

	public static String leftPad(String s, int width, String b) {
		return String.format("%" + width + "s", s).replace(" ", b);
	}

	/**
	 * Right-pads a {@link String} with the specified character to the specified width/length
	 * */
	public static String rightPad(String s, int width, char c) {
		return String.format("%-" + width + "s", s).replace(' ', c);
	}

	public static String rightPad(String s, int width, String b) {
		return String.format("%-" + width + "s", s).replace(" ", b);
	}

	/**
	 * Trims the specified string to the specified length. If the string
	 * is null or it's length is less than or equal to the specified length
	 * the string argument/parameter is returned unchanged
	 * */
	public static String trim(String s, int length){
		if(s==null || s.length() <= length){
			return s;
		}
		return s.substring(0, length).trim();
	}

	/**
	 * Truncates a {@link String} to the specified length. If it is shorter-than or equal-to the
	 * specified length the original value is returned
	 * */
	public static String truncate(String s, int length){
		return trim(s, length);
	}

	/*
	 * Splits the specified string into an array of {@link String}
	 * along the specified delimiter. This method no longer uses
	 * {@link StringTokenizer} - instead it uses {@link String#split(String)}
	 *
	 *
	public static final String[] split2(String s, String delim){
		if(s==null){
			return null;
		}
		return s.split(delim);
	}//*/
	/**
	 * Splits a {@link String} along the specified delimiter using a {@link StringTokenizer}. This
	 * method is similar to {@link String#split(String)} in that trailing empty strings are not
	 * included in the resulting array; it is different in that empty tokens in the middle of the
	 * string are skipped but subsequent tokens are included in the returned array.
	 * */
	public static final String[] split(String s, String delim){
		if(isEmpty(s)){
			return null;
		}
		StringTokenizer tokenizer = new StringTokenizer(s, delim);
		String[] arr = new String[tokenizer.countTokens()];
		int i=0;
		while (tokenizer.hasMoreTokens()) {
			arr[i] = tokenizer.nextToken();
			i++;
		}
		return arr;
	}

	/**
	 * Replaces all instances of <code>match</code> with <code>replace</code>.
	 * This method no longer uses regular expressions to replace the matching
	 * string - it uses {@link String#replace(CharSequence, CharSequence)}
	 * */
	public static String replace(String s, String match, String replace){
		if(s==null){
			return null;
		}
		return s.replace(match, replace);
		//return Pattern.compile(match).matcher(string).replaceAll(replacement);
	}

	/**
	 * Removes all non-alphanumeric characters
	 * */
	public static String removeNonAlphaNumerics(String s){
		if(s==null){
			return null;
		}
		return s.replaceAll(NON_ALPHANUMERIC, "");
	}

	/**
	 * Removes all non-numeric characters
	 * */
	public static String removeNonNumerics(String s){
		if(s==null){
			return null;
		}
		return s.replaceAll(NON_NUMERIC, "");
	}

	/**
	 * Escapes any single quotes in the specified string
	 */
	public static String sqlQuoteEsc(String string){
		if (isEmpty(string)) {
			return null;
		}

		StringBuilder buf = new StringBuilder();

		for (int i = 0; i < string.length(); i++){
			if (string.charAt(i) == '\''){
				buf.append("''");
			}else{
				buf.append(string.charAt(i));
			}
		}
		return buf.toString();
	}

	public static final String join(Collection<?> array){
		return join(array.toArray());
	}

	public static final void sleep(long millis){
		try{
			Thread.sleep(millis);
		}catch(InterruptedException e){
			throw new RuntimeException(e);
		}
	}

	public static final String join(Collection<?> array, String delimiter){
		if(array==null){
			return null;
		}
		return join(array.toArray(), delimiter);
	}

	/**
	 * Joins the specified array whitespace as the separator. Equivalent to {@link #join(array, WHITESPACE)}
	 * */
	public static final String join(Object[] array){
		if(array==null){
			return null;
		}
		return join(array, WHITESPACE);
	}

	/**
	 * Joins the specified array along the specified delimiter.  If the array
	 * is null or empty an empty, zero-length string is returned.
	 * */
	public static final String join(Object[] array, String delimiter){
		return join(array, null, null, delimiter);
	}

	/**
	 * Prefixes and suffixes each array element with the corresponding prefix/suffix, and
	 * joins each prefixed/suffixed element using the specified delimiter.  If the array
	 * is null or empty an empty, zero-length string is returned.
	 * */
	public static final String join(Object[] array, String prefix, String suffix, String delimiter){
		if (isEmpty(array)) {
			return "";
		}
		StringBuilder buf = new StringBuilder();
		for (int i=0;i<array.length;i++) {
			if(prefix!=null){
				buf.append(prefix);
			}
			buf.append(array[i].toString());
			if(suffix!=null){
				buf.append(suffix);
			}
			if (i!=array.length-1) {
				buf.append(delimiter);
			}
		}
		return buf.toString();
	}

	/**
	 * Similar to the {@link #join(Object[] array, String delimiter)} method but allows
	 * the array to be specified as a varargs and forces the delimiter to be a single char
	 * */
	public static final String join(char delimiter, Object...a){
		return join(a, String.valueOf(delimiter));
	}


	/////////////////////////////////////
	// Array methods
	/////////////////////////////////////

	/**
	 * Returns a sorted array without any duplicates
	 * */
	public static String[] removeDuplicates(String[] dups){

		Arrays.sort(dups);

		int k = 1;

		for (int i = 1; i < dups.length; i++){
			if (! dups[i].equals(dups[i -1])){
				dups[k++] = dups[i];
			}
		}

		String[] unique = new String[k];

		System.arraycopy(dups, 0, unique, 0, k);

		return unique;
	}

	/**
	 * Returns true if the specified array contains the specified parameter
	 * */
	public static boolean hasParm(final String[] args, String parm){
		//String[] copy = new String[args.length];
		//System.arraycopy(args, 0, copy, 0, args.length);
		Arrays.sort(args);//the array must be sorted
		return Arrays.binarySearch(args, parm) >= 0;
	}

	/**
	 * Creates an array of boolean values from the specified {@link String} array
	 * */
	public static boolean[] toBooleanArray(String... a) {
		boolean[] arr = new boolean[a.length];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = Boolean.parseBoolean(a[i]);
		}
		return arr;
	}

	/**
	 * Creates an array of int values from the specified {@link String} array
	 * */
	public static int[] toIntArray(String... a) {
		int[] arr = new int[a.length];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = Integer.parseInt(a[i]);
		}
		return arr;
	}

	public static Integer[] toIntegerArray(String... a) {
		Integer[] arr = new Integer[a.length];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = Integer.valueOf(a[i]);
		}
		return arr;
	}

	/**
	 * Creates an array of long values from the specified {@link String} array
	 * */
	public static long[] toLongArray(String... a) {
		long[] arr = new long[a.length];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = Long.parseLong(a[i]);
		}
		return arr;
	}

	/**
	 * Creates an array of float values from the specified {@link String} array
	 * */
	public static float[] toFloatArray(String... a) {
		float[] arr = new float[a.length];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = Float.parseFloat(a[i]);
		}
		return arr;
	}

	/**
	 * Creates an array of double values from the specified {@link String} array
	 * */
	public static double[] toDoubleArray(String... a) {
		double[] arr = new double[a.length];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = Double.parseDouble(a[i]);
		}
		return arr;
	}

	/**
	 * Creates an array of BigDecimal values from the specified {@link String} array
	 * */
	public static BigDecimal[] toBigDecimalArray(String... a) {
		BigDecimal[] arr = new BigDecimal[a.length];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = new BigDecimal(a[i]);
		}
		return arr;
	}

	/////////////////////////////////////
	// Throwable methods
	/////////////////////////////////////
	
	/**
	 * Returns the inner-most {@link Throwable}
	 * */
	public static Throwable getRootCause(Throwable t){
		return Throwables.getRootCause(t);
	}
	
	/**
	 * Propogates the specified {@link Throwable} if it is an instance
	 * of {@link RuntimeException} or {@link Error}
	 * */
	public static <X extends Throwable> void propogate(Throwable t, Class<X> type)throws X{
		Throwables.propagateIfPossible(t, type);
	}
	
	/**
	 * Propogates the specified {@link Throwable} if it is an instance
	 * of {@link RuntimeException} or {@link Error}
	 * */
	public static RuntimeException propogate(Throwable t){
		return Throwables.propagate(t);
	}

	/**
	 * Returns the stack trace of the specified Throwable
	 * */
	public static final String getStackTrace(Throwable t){
		return Throwables.getStackTraceAsString(t);
		/*
		java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		t.printStackTrace(new java.io.PrintWriter(out, true));
		return out.toString();
		//*/
	}

	/**
	 * Returns the message attribute of the specified Throwable or the entire stack trace
	 * if the message attribute is null
	 * */
	public static final String getMessageOrStackTrace(Throwable t){
		return t.getMessage()==null ? getStackTrace(t) : t.getMessage();
	}

	/////////////////////////////////////
	// I/O methods
	/////////////////////////////////////

	/**
	 * Writes the specified objects to an output stream (only UTF-8 bytes are written)
	 * */
	public static final void write(OutputStream out, Object... values)throws IOException{
		for (Object val : values) {
			if(val==null){
				continue;
			}
			out.write(val.toString().getBytes(UTF8));
		}
	}

	/**
	 * Writes the specified objects to an output stream (only UTF-8 bytes are written) and
	 * adds a CRLF to the end
	 * */
	public static final void writeln(OutputStream out, Object... values)throws IOException{
		write(out, values);
		out.write(NEWLINE);
	}
}
