/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.gis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tauasa.commons.net.HTTPUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * IGeocoder implementation that uses the Google Geocoding API (V3).
 * Results are fetched in JSON format (as opposed to XML).
 * 
 * @see https://developers.google.com/maps/documentation/geocoding/index
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class GoogleGeocoder extends AbstractGeocoder {

	private static final Logger log = LoggerFactory.getLogger(GoogleGeocoder.class);
	private static final String URL = "%s://maps.googleapis.com/maps/api/geocode/json?address=%s&sensor=%b";
	private static final String HTTP_SCHEME= "http";
	private static final String HTTPS_SCHEME = "https";

	private boolean useSsl, sensor;
	private int timeout = 1000;

	public GoogleGeocoder() {

	}

	public GoogleGeocoder(boolean useSsl) {
		setUseSsl(useSsl);
	}

	@Override
	public double[] geocode(String streetAddress, String city, String state){

		String address = encode(streetAddress, city, state);

		if(log.isDebugEnabled()){
			log.debug(String.format("Encoded address: %s", address));
		}

		String url = String.format(URL, useSsl?HTTPS_SCHEME:HTTP_SCHEME, address, sensor);

		if(log.isDebugEnabled()){
			log.debug(String.format("URL: %s", url));
		}

		HTTPUtils.HttpResponse res = null;

		try{
			res = HTTPUtils.doGet(url, timeout);
		}catch(Exception e){
			throw new RuntimeException(e);
		}

		JsonElement json = res.getBodyAsJson();

		if(res.getStatus()!=200 || json==null){
			log.warn(String.format("HTTP Status: %s, Body: %s",
					res.getStatus(), json==null?"":json.toString()));
			return null;
		}

		if(log.isDebugEnabled()){
			log.debug(String.format("Response (body): %s", json.toString()));
		}

		JsonObject root = json.getAsJsonObject();

		String status = root.get("status").getAsString();

		if(!"OK".equals(status)){
			log.warn(String.format("Bad status: %s", status));
			return null;
		}

		JsonArray results = root.getAsJsonArray("results");

		if(results.size()==0){
			log.warn("No results");
			return null;
		}

		JsonObject result = results.get(0).getAsJsonObject();

		if(result.has("formatted_address") & log.isDebugEnabled()){
			log.debug(String.format("formatted_address: %s", result.get("formatted_address").getAsString()));
		}

		JsonObject geometry = result.getAsJsonObject("geometry");
		JsonObject location = geometry.getAsJsonObject("location");

		if(log.isDebugEnabled()){
			String locationType = geometry.get("location_type").getAsString();
			log.debug(String.format("location_type", locationType));
		}

		return new double[]{
				location.get("lat").getAsDouble(),
				location.get("lng").getAsDouble()
		};
	}

	public boolean isUseSsl() {
		return useSsl;
	}

	public void setUseSsl(boolean useSsl) {
		this.useSsl = useSsl;
	}

	public boolean isSensor() {
		return sensor;
	}

	public void setSensor(boolean sensor) {
		this.sensor = sensor;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}


}


