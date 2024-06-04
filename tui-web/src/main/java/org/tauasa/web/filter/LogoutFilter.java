/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.web.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tauasa.commons.util.Utils;


/**
 * {@link Filter} implementation that performs a logout by invalidating the current {@link HttpSession} and redirects or
 * forwards the request to an URI in the current context.
 * <p/>
 * <b>NOTE:</b> For JBoss deployments using JAAS, the jboss-web.xml
 * <code>securityDomain</code> element should have it's <code>flushOnSessionInvalidate</code>
 * attribute set to true. (Reference: http://wiki.jboss.org/wiki/Wiki.jsp?page=CachingLoginCredentials)
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class LogoutFilter extends AbstractFilter {

	private static final Logger log = LoggerFactory.getLogger(LogoutFilter.class);

	public static final String DEFAULT_URI = "/index.jsp";
	public static final String PARM_URI = "uri";
	public static final String PARM_REDIRECT = "redirect";

	public LogoutFilter() {

	}

	@Override
	public Logger getLogger(){
		return log;
	}

	@Override
	public void doFilter(FilterHelper helper) throws IOException, ServletException{

		//get the session if it exists
		HttpSession session = helper.getSession(false);
		if(session!=null){
			getLogger().info(String.format("Invalidating session %s [remote addr: %s]", session.getId(), helper.getRemoteAddr()));
			session.invalidate();
		}

		//URI we're sending client to
		String uri = getStringParam(PARM_URI, DEFAULT_URI);

		if(getBooleanParam(PARM_REDIRECT)){
			//pass along any request parameters
			List<String> names = helper.getParameterNames();
			StringBuilder b = new StringBuilder();
			for(int i=0;i<names.size();i++){
				String name = names.get(i);
				b.append(name).append("=").append(helper.getParam(name));
				if(i!=names.size()-1){
					b.append("&");
				}
			}
			String parms = b.toString();
			if(!Utils.isEmpty(parms)){
				uri += "?"+parms;
			}
			String location = String.format("%s%s", helper.getContextPath(), uri);
			getLogger().info(String.format("Redirecting to %s", location));
			helper.getResponse().sendRedirect(location);
		}else{
			getLogger().info(String.format("Forwarding to %s", uri));
			helper.forward(uri);
		}

	}

}
