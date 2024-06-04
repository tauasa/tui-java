/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.util;

import java.io.Serializable;


/**
 * A key-value pair
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class KeyValuePair<K, V> implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private K key;
	private V value;
	
	public KeyValuePair() {
	}
	
	public KeyValuePair(K key, V value) {
		setKey(key);
		setValue(value);
	}

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}
}
