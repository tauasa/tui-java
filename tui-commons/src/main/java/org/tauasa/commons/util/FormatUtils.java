/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.util;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;

/**
 * Formats {@link String} values into commonly-used formats
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class FormatUtils extends Utils{

	public static final NumberFormat PRETTY_NUMBER_FORMAT = new DecimalFormat("#,##0.00");

	public static String formatSsn(String ssn){
		if(isEmpty(ssn) || ssn.trim().length()!=9){
			return ssn;
		}
		ssn = ssn.trim();
		return String.format("%s-%s-%s", ssn.substring(0, 3), ssn.substring(3, 5), ssn.substring(5));
	}

	public static String maskSsn(String ssn, String mask){
		if(isEmpty(ssn) || ssn.trim().length()!=9){
			return ssn;
		}
		return String.format("%s%s", createString(mask.charAt(0), 5), ssn.substring(5));
	}

	public static String formatPhone(String s){
		if(isEmpty(s) || (s.trim().length()!=10 && s.trim().length()!=7)){
			return s;
		}
		s = s.trim();
		if(s.length()==10){
			return String.format("(%s) %s-%s", s.substring(0, 3), s.substring(3, 6), s.substring(6));
		}

		//must be 7 chars
		return String.format("%s-%s", s.substring(0, 3), s.substring(3));
	}

	public static String formatPhone2(String s){
		if(isEmpty(s) || (s.trim().length()!=10 && s.trim().length()!=7)){
			return s;
		}
		s = s.trim();
		if(s.length()==10){
			return String.format("%s-%s-%s", s.substring(0, 3), s.substring(3, 6), s.substring(6));
		}

		//must be 7 chars
		return String.format("%s-%s", s.substring(0, 3), s.substring(3));
	}

	/**
	 * Convenience method that formats the specified {@code format} using the specified argument
	 * array by invoking {@link String#format(String, Object...)}. Only {@literal %s} specifiers are considered when determining if the specified format
	 * should be formatted
	 * */
	public static String format(String format, Object...args){
		if(format!=null && format.indexOf("%s")!=-1 && !isEmpty(args)){
			return String.format(format, args);
		}
		return null;
	}

	public static String format(short n, String fmt){
		return format(n, fmt);
	}

	public static String format(int n, String fmt){
		return format(n, fmt);
	}

	public static String format(long n, String fmt){
		return format(new Long(n), fmt);
	}

	public static String format(float n, String fmt){
		return format(new Float(n), fmt);
	}

	public static String format(double n, String fmt){
		return format(new Double(n), fmt);
	}

	public static String format(Number n, String fmt){
		return format(n, new DecimalFormat(fmt));
	}

	public static String format(Number n, NumberFormat fmt){
		return _format(n, fmt);
	}

	private static final String _format(Object obj, Format fmt){
		return fmt.format(obj);
	}

}
