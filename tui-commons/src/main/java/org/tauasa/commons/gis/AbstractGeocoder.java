/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
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
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
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
