/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.web.smoke;

import org.tauasa.commons.util.XProperties;
import org.tauasa.web.ServletHelper;

import java.io.IOException;
import java.util.List;

/**
 * Renders a {@link SmoketestResult}
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public interface ISmoketestResultsRenderer {

	/**
	 * Initialize this renderer using the specified {@link XProperties} (provided by the {@link SmoketestServlet})
	 * */
	public void init(XProperties smoketestProperties);

	/**
	 * Using the specified {@link ServletHelper}, renders an output for the specified results and/or errors
	 * */
	public void render(List<SmoketestResult> results, List<SmoketestException> errors, ServletHelper helper)throws IOException;



}
