/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.gis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tauasa.commons.util.Utils;

/**
 * TODO - document GeohashUtils
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public final class GeohashUtils extends Utils {

	private static final Logger log = LoggerFactory.getLogger(GeohashUtils.class);

	private static final Geohash GEOHASH = new Geohash();

	private GeohashUtils(){
	}

	public static Geohash getGeohash(){
		return GEOHASH;
	}

	public static String encode(double[] latlon){
		return encode(latlon[0], latlon[1]);
	}

	public static String encode(double lat, double lon){
		String hash = GEOHASH.encode(lat, lon);
		if(log.isDebugEnabled()){
			log.debug(String.format("encode(%f, %f) -> %s", lat, lon, hash));
		}
		return hash;
	}

	public static double[] decode(String geohash){
		double[] arr = GEOHASH.decode(geohash);
		if(log.isDebugEnabled()){
			log.debug(String.format("decode(%s) -> [%f, %f]", geohash, arr[0], arr[1]));
		}
		return arr;
	}


}
