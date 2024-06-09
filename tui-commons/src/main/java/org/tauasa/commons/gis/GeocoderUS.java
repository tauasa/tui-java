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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tauasa.commons.net.HTTPUtils;
import org.tauasa.commons.util.Utils;

/**
 * IGeocoder implementation that uses the geocoder.us service (http://geocoder.us/)
 *
 * @author Tauasa Timoteo
 * 
 */
public class GeocoderUS extends AbstractGeocoder {

	private static final Logger logger = LoggerFactory.getLogger(GeocoderUS.class);
	public static final String DEFAULT_URL = "http://rpc.geocoder.us/service/csv";

	private String userName, password, url=DEFAULT_URL;

	public GeocoderUS() {

	}

	public GeocoderUS(String userName, String password) {
		this.userName=userName;
		this.password=password;
	}

	@Override
	public double[] geocode(String streetAddress, String city, String state){

		String b = url + "?address=" + encode(streetAddress, city, state);

		String _url = b;

		HTTPUtils.HttpResponse res = null;

		try{
			res = HTTPUtils.doGet(_url, userName, password);
		}catch(Exception e){
			throw new RuntimeException(e);
		}

		String body = res.getBodyAsString();


		if(res.getStatus()!=200){
			logger.warn(String.format("HTTP Status: %s, Body: %s",
					res.getStatus(), body));
			return null;
		}

		if(logger.isDebugEnabled()){
			logger.debug(String.format("Response (body): %s", body));
		}

		if(Utils.isEmpty(body)){
			throw new RuntimeException("No response available from "+_url);
		}

		return Utils.toDoubleArray(Utils.split(body, ","));
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
}
