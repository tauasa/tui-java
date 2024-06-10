/*
 * Copyright 2012 Tauasa Timoteo
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation 
 * files (the “Software”), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-
 * INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN 
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF 
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 */
package org.tauasa.web;

import javax.servlet.http.HttpServletRequest;

import org.tauasa.commons.util.FormatUtils;
import org.tauasa.commons.util.Utils;

/**
 * Static utility methods that can be exposed to JSP's via /WEB-INF/functions.tld
 *
 * @author Tauasa Timoteo
 */
public class JSTL{

	//private static final Logger log = LoggerFactory.getLogger(JSTL.class);

	public static boolean isEnabled(String key){
		return Boolean.getBoolean(key);
	}

	public static String getSystemProperty(String key, String def){
		return System.getProperty(key, def);
	}

	public static String getStackTrace(Throwable t){
		return Utils.getStackTrace(t);
	}

	public static boolean isUserInRole(HttpServletRequest req, String[] roles){
		if(Utils.isEmpty(roles)){
			return true;
		}
		for(String role : roles){
			if(isUserInRole(req, role)){
				return true;
			}
		}
		return false;
	}

	public static boolean isUserInRole(HttpServletRequest req, String role){
		try{
			return HttpServletRequestHelper.isUserInRole(req, role);
		}catch(Exception e){
			//log.warn(String.format("Error checking user for role %s", role), e);
		}
		return false;
	}

	public static String titleCase(Object o){
		if(o==null){
			return "";
		}
		return Utils.titleCase(o.toString());
	}

	public static String maskSsn(String s, String mask){
		return FormatUtils.maskSsn(s, mask);
	}

	public static String formatSsn(String s){
		return FormatUtils.formatSsn(s);
	}

	public static String formatPhone(String s){
		return FormatUtils.formatPhone(s);
	}

	public static String formatPhone2(String s){
		return FormatUtils.formatPhone2(s);
	}


}
