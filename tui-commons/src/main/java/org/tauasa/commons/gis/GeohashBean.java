/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.gis;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Wraps a geohash
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
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




