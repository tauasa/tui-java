/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.gis;



/**
 * Interface definition for a geocoding service
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public interface IGeocoder {

	/**
	 * Returns a two-element array containing the latitude and longitude at the first
	 * and second positions, respectively.
	 * @throws IllegalStateException
	 * */
	public double[] geocode(String streetAddress, String city, String state);


}
