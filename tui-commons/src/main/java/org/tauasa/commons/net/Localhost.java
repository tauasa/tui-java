/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.net;

import java.net.InetAddress;

/**
 * Utility class for interrogating the localhost
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class Localhost {

	private static InetAddress localhost;

	static{
		try{
			localhost = InetAddress.getLocalHost();
		}catch(Exception e){
			throw new RuntimeException(e.toString());
		}
	}

	private Localhost() {
		//no instances created
	}

	public static String getHostAddress(){
		return localhost.getHostAddress();
	}

	public static String getHostName(){
		return localhost.getHostName();
	}
	
}
