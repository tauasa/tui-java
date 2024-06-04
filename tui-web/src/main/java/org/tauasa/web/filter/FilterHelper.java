/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tauasa.web.ServletHelper;

/**
 * {@link ServletHelper} subclass used for servlet filters
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class FilterHelper extends ServletHelper {

	private FilterChain chain;

	public FilterHelper(FilterConfig config, FilterChain chain, HttpServletRequest req, HttpServletResponse res) {
		super(config.getServletContext(), req, res);
		this.chain=chain;
	}

	public void doFilter()throws IOException, ServletException{
		chain.doFilter(super.getRequest(), super.getResponse());
	}

	public FilterChain getChain() {
		return chain;
	}

}
