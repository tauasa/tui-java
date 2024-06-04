/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.web;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A host consisting of name, IP address and port
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class Host implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name, address;
	private int port;

	public Host() {
	}

	public Host(String address, int port) {
		setAddress(address);
		setPort(port);
	}

	public Host(String name, String address, int port) {
		this(address, port);
		setName(name);
	}

	public String getAddressPort(){
		return String.format("%s:%d", address, port);
	}

	@Override
	public String toString(){
		return new ToStringBuilder(this).append("address", address).
				append("port", port).append("name", name).toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
