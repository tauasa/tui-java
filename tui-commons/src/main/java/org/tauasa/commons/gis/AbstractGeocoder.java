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
package org.tauasa.commons.gis;

import static org.tauasa.commons.util.Utils.UTF8;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tauasa.commons.util.Utils;

/**
 * TODO - document AbstractGeocoder
 *
 * @author Tauasa Timoteo
 * 
 */
public abstract class AbstractGeocoder implements IGeocoder {

	private static final Logger log = LoggerFactory.getLogger(AbstractGeocoder.class);

	public static final String ENCODING = "UTF-8";
	public static final String TOKEN = ",+";

	public AbstractGeocoder() {

	}

	protected Logger getLog(){
		return log;
	}

	/**
	 * Encodes and joins the specified address components into a single string
	 * */
	protected String encode(String street, String city, String state){
		StringBuilder b = new StringBuilder();
		if(!Utils.isEmpty(street)){
			b.append(encode(street)).append(TOKEN);
		}
		if(!Utils.isEmpty(city)){
			b.append(encode(city)).append(TOKEN);
		}
		if(!Utils.isEmpty(state)){
			b.append(encode(state));
		}
		return b.toString();
	}

	/**
	 * URL encodes the specified {@link String} using UTF-8 character encoding.
	 * @throws RuntimeException that wraps an {@link UnsupportedEncodingException}
	 * */
	protected static String encode(String s){
		if(log.isTraceEnabled()){
			log.trace(String.format("encoding %s", s));
		}
		try{
			return URLEncoder.encode(s, UTF8);
		}catch(UnsupportedEncodingException e){
			throw new RuntimeException(e);
		}
	}

}
