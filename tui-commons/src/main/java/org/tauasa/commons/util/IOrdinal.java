/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.util;

import java.util.Comparator;


/**
 * An ordinal object
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public interface IOrdinal{

	/**
	 * Returns an object's ordinal value
	 * */
	public int getOrdinal();

	/**
	 * Sets an object's ordinal value
	 * */
	public void setOrdinal(int ordinal);

	public static class OrdinalComparator implements Comparator<IOrdinal>{
		@Override
		public int compare(IOrdinal o1, IOrdinal o2) {
			if(o1!=null && o2==null){
				return 1;
			}else if(o1==null && o2!=null){
				return -1;
			}
			return o1.getOrdinal() == o2.getOrdinal() ? 0 :
				(o1.getOrdinal() > o2.getOrdinal() ? 1 : -1);
		}
	}

}
