/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.web;

import javax.servlet.http.HttpServletRequest;

import org.tauasa.commons.util.FormatUtils;
import org.tauasa.commons.util.Utils;

/**
 * Static utility methods that can be exposed to JSP's via /WEB-INF/functions.tld
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
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
