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

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Wraps a geohash
 *
 * @author Tauasa Timoteo
 * 
 */
public class GeohashBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String geohash;
	private double[] latlon;

	public GeohashBean(final String geohash) {
		this.geohash=geohash;
		this.latlon=GeohashUtils.decode(geohash);
	}

	public GeohashBean(final double[] latlon) {
		this.latlon=latlon;
		this.geohash=GeohashUtils.encode(latlon);
	}

	@Override
	public String toString(){
		return new ToStringBuilder(this).
				append("latlon", (latlon!=null && latlon.length==2) ?
						String.format("[%f , %f]", latlon[0], latlon[1]) : "").
						append("geohash", geohash).
						toString();
	}

	public static GeohashBean create(final String geohash){
		return new GeohashBean(geohash);
	}

	public static GeohashBean create(final double[] latlon){
		return new GeohashBean(latlon);
	}

	public String getGeohash(){
		return geohash;
	}

	public double getLatitude(){
		return latlon[0];
	}

	public double getLongitude(){
		return latlon[1];
	}

	public double[] getLatLon(){
		return latlon;
	}

}




