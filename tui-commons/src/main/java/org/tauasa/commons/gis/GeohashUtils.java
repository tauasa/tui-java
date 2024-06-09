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
import org.tauasa.commons.util.Utils;

/**
 * Geohash utility methods
 *
 * @author Tauasa Timoteo
 * 
 */
public final class GeohashUtils extends Utils {

	private static final Logger log = LoggerFactory.getLogger(GeohashUtils.class);

	private static final Geohash GEOHASH = new Geohash();

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
