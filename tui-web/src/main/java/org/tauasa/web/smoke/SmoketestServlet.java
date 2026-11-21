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
package org.tauasa.web.smoke;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tauasa.commons.util.Utils;
import org.tauasa.commons.util.XProperties;
import org.tauasa.web.ServletHelper;

/**
 * Invokes one or more {@link ISmoketest} implementations.
 * <p>
 * The tests can show configuration, query results and host details, and can send
 * mail, so every request must come from a user in the container role named by
 * {@value #SMOKETEST_REQUIRED_ROLE_KEY} in {@value #PROPERTIES_FILE}. Without that
 * setting, or without that role, the servlet answers 403. Map it behind your
 * container's security constraint as well.
 * <p>
 * {@value #SMOKETEST_ALLOW_VIRTUAL_HOST_KEY} only checks the host name in the
 * request URL, which the client controls through its {@code Host} header. It is not
 * access control.
 *
 * @author Tauasa Timoteo
 * 
 */
public class SmoketestServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(SmoketestServlet.class);

	public static final String PROPERTIES_FILE = "/META-INF/smoketest.properties";
	public static final String SMOKETEST_CLASSES_KEY = "smoketest.classes";
	public static final String SMOKETEST_RENDERER_KEY = "smoketest.results.renderer";
	public static final String SMOKETEST_ALLOW_VIRTUAL_HOST_KEY = "smoketest.allow.virtual.host";
	public static final String SMOKETEST_EXCEPTION_HANDLER = "smoketest.exception.handler";
	/**
	 * The container role a user must have to run the smoke tests. Without it set,
	 * every request is refused with 403
	 * */
	public static final String SMOKETEST_REQUIRED_ROLE_KEY = "smoketest.required.role";

	private static final String REMOVED_JQUERY_RENDERER = "org.tauasa.web.smoke.JQueryXHTMLRenderer";

	private XProperties smoketestProperties;
	private List<Class<? extends ISmoketest>> smokeTests;
	private ISmoketestResultsRenderer renderer;
	private boolean allowVirtualHost = true;
	private String requiredRole;
	private ISmoketestExceptionHandler exceptionHandler;

	public SmoketestServlet() {

	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		if(logger.isInfoEnabled()){
			logger.info(String.format("Initializing from %s", PROPERTIES_FILE));
		}

		try{
			// ClassLoader paths have no leading slash: with one, the lookup returns null
			java.io.InputStream in = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE.substring(1));
			if(in==null){
				throw new ServletException(PROPERTIES_FILE+" is not on the classpath");
			}
			smoketestProperties = XProperties.loadProperties(in);
			if(logger.isDebugEnabled()){
				//names only: values can be passwords
				logger.debug(String.format("%s keys: %s", PROPERTIES_FILE, smoketestProperties.stringPropertyNames()));
			}

		}catch(IOException e){
			logger.error("Could not load "+PROPERTIES_FILE, e);
			throw new ServletException(e);
		}
		if(logger.isInfoEnabled()){
			logger.info("Parsing ISmoketest impls");
		}
		String strSmokeTests = smoketestProperties.getProperty(SMOKETEST_CLASSES_KEY);
		if(Utils.isEmpty(strSmokeTests)){
			String msg = String.format("%s is not defined in %s", SMOKETEST_CLASSES_KEY, PROPERTIES_FILE);
			logger.error(msg);
			throw new ServletException(msg);
		}

		smokeTests = new ArrayList<>();
		String[] array = Utils.split(strSmokeTests, ",");
		if(logger.isInfoEnabled()){
			logger.info(String.format("Found %d ISmoketest impls... loading classes", array.length));
		}
		int i=0;
		try{
			for (i=0;i<array.length;i++) {
				Class<? extends ISmoketest> impl = (Class<? extends ISmoketest>) Class.forName(array[i].trim());
				smokeTests.add(impl);
				if(logger.isInfoEnabled()){
					logger.info("Loaded ISmoketest impl: "+impl.getName());
				}
			}
		}catch(ClassNotFoundException e){
			logger.error("Could not load smoketest class "+array[i], e);
			throw new ServletException(e);
		}

		String strRenderer = smoketestProperties.getProperty(SMOKETEST_RENDERER_KEY);
		if(Utils.isEmpty(strRenderer)){
			throw new ServletException(SMOKETEST_RENDERER_KEY+" is not defined in "+PROPERTIES_FILE);
		}
		if(strRenderer.trim().equals(REMOVED_JQUERY_RENDERER)){
			// Removed in 1.0.1 with the jQuery 1.3.2 / jQuery UI 1.7.2 files it served,
			// which have known XSS vulnerabilities.
			throw new ServletException(REMOVED_JQUERY_RENDERER+" was removed in tui-web 1.0.1. Set "
					+SMOKETEST_RENDERER_KEY+" in "+PROPERTIES_FILE+" to "+XHTMLRenderer.class.getName());
		}
		logger.info("Creating ISmoketestResultsRenderer of type "+strRenderer);
		try{
			renderer = (ISmoketestResultsRenderer)Class.forName(strRenderer).getConstructors()[0].newInstance();
			renderer.init(smoketestProperties);//initialize
		}catch(ClassNotFoundException | IllegalAccessException | InstantiationException | IllegalArgumentException | InvocationTargetException | SecurityException e){
			logger.error("Could not create renderer instance of type "+strRenderer, e);
			throw new ServletException(e);
		}

		requiredRole = smoketestProperties.getProperty(SMOKETEST_REQUIRED_ROLE_KEY);
		if(Utils.isEmpty(requiredRole)){
			logger.error(String.format("%s is not set in %s: every request will be refused", SMOKETEST_REQUIRED_ROLE_KEY, PROPERTIES_FILE));
		}

		String strAllowVirtualHost = smoketestProperties.getProperty(SMOKETEST_ALLOW_VIRTUAL_HOST_KEY);
		allowVirtualHost = strAllowVirtualHost==null ? true : Boolean.parseBoolean(strAllowVirtualHost);
		logger.info("Allow Virtual Hosts: "+allowVirtualHost);

		String strExceptionHandler = smoketestProperties.getProperty(SMOKETEST_EXCEPTION_HANDLER);
		if(Utils.isEmpty(strExceptionHandler)){
			String msg = SMOKETEST_EXCEPTION_HANDLER+" is not defined in "+PROPERTIES_FILE;
			logger.error(msg);
			throw new ServletException(msg);
		}
		logger.info("Creating ISmoketestExceptionHandler of type "+strExceptionHandler);
		try{
			exceptionHandler = (ISmoketestExceptionHandler)Class.forName(strExceptionHandler).getConstructors()[0].newInstance();
		}catch(ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | SecurityException | InvocationTargetException e){
			logger.error("Could not create ISmoketestExceptionHandler instance of type "+strExceptionHandler, e);
			throw new ServletException(e);
		}

	}

	public static String buildErrorAndResultsMessage(SmoketestException e, ServletHelper helper){
		StringBuilder b = new StringBuilder()
		.append("Error Message: ").append(e.getMessage()).append("\r\n");

		if(e.getResult()!=null && !e.getResult().isEmpty()){
			b.append("===========================================\r\n")
			.append("Results: ").append("\r\n");
			for (SmoketestMessage msg : e.getResult()) {
				b.append("  +").append(msg.getValue());
				if(msg.isErrorMessage()){
					b.append(msg.getError()!=null?" (Error: "+msg.getError().getMessage()+")":"");
				}
				b.append("\r\n");
			}
		}
		b.append("\r\nStack Trace:\r\n").append(Utils.getStackTrace(e)).append("\r\n");
		return b.toString();
	}

	public static String buildClientServerInfo(ServletHelper helper){
		String whoDunnit = helper.getRequest().getRemoteUser();
		//build a message
		StringBuilder b = new StringBuilder()
		.append("Server Info").append("\r\n");
		try{
			b.append("  +Host: ").append(InetAddress.getLocalHost().getHostName()).append("\r\n");
		}catch(UnknownHostException e){
			b.append("  +Host: ").append(e.getMessage()).append("\r\n");
		}

		b.append("  +URL: ").append(helper.getRequestURL()).append("\r\n")
		.append("  +Query: ").append(helper.getQueryString()).append("\r\n")
		.append("===========================================\r\n")
		.append("Client Info").append("\r\n")
		.append("  +Remote Host: ").append(helper.getRemoteAddr()).append("\r\n")
		.append("  +User: ").append(whoDunnit).append("\r\n")
		.append("  +User-Agent: ").append(helper.getUserAgent()).append("\r\n");

		return b.toString();
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		ServletHelper helper = new ServletHelper(super.getServletContext(), req, resp);

		// Fail closed: without a configured role, or without that role, nobody runs
		// the tests or sees their output.
		if(Utils.isEmpty(requiredRole)){
			logger.error(String.format("Refusing smoketest request: %s is not set in %s", SMOKETEST_REQUIRED_ROLE_KEY, PROPERTIES_FILE));
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		if(!req.isUserInRole(requiredRole)){
			logger.warn(String.format("Refusing smoketest request from %s: not in role %s", req.getRemoteAddr(), requiredRole));
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		String hostName = null;
		try{
			InetAddress localhost = InetAddress.getLocalHost();
			hostName = localhost.getHostName();
		}catch(UnknownHostException t){
			throw new ServletException("Could not determine hostname");
		}

		if(!allowVirtualHost &&
			!helper.getRequestURL().toLowerCase().contains(hostName)){
			//smoke test is configured to be accessed via real server
			//name, not a virtual host (i.e. java.tauasa, www.tauasa).
			//The client sets the URL's host, so this is a convenience, not access control.
			logger.error(String.format("Smoketest must be accessed via %s, NOT %s", hostName, helper.getRequestURL()));
			helper.getResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
			helper.getResponse().getWriter().println("Access Denied");
			return;
		}

		ArrayList<SmoketestResult> results = new ArrayList<>();
		ArrayList<SmoketestException> exceptions = new ArrayList<>();

		for (Class<? extends ISmoketest> clazz : smokeTests) {
			ISmoketest test = null;
			try{
				logger.info("CREATING: "+clazz.getName());
				test = (ISmoketest)clazz.getConstructors()[0].newInstance();
				logger.info("INITIALIZING: "+clazz.getName());
				test.init(smoketestProperties);
				if(!test.isActive()){
					logger.warn("INACTIVE: "+clazz.getSimpleName());
				}else{
					logger.info("EXECUTING: "+clazz.getName());
					test.execute(helper);
					results.add(test.getResult());
				}
				logger.info("COMPLETED: "+clazz.getName());
			}catch(SmoketestException e){
				//allow the exception handler to do its thang
				exceptionHandler.handleException(e,
						helper,
						test==null ? logger : test.getLogger());
				//if the handler didn't re-throw the exception
				//we need to store any results contained in the exception
				results.add(e.getResult());
				//store the exception in case we need to process if later on
				exceptions.add(e);
			}catch(IllegalAccessException | IllegalArgumentException | InstantiationException | SecurityException | InvocationTargetException e){
				//an exception occurred that was not explicity
				//handled in the smoke test - log the exception
				//and re-throw the exception as a ServletException
				String msg = "DOH! Somebody needs to fix their smoketest, yo";
				logger.error(msg, e);
				if(test!=null){
					test.getLogger().error(msg, e);
					//results.add(new SmoketestResult(test, new SmoketestMessage("Doh!", e, SmoketestMessage.Type.ERROR)));
				}
				throw new ServletException(e);
			}finally{
				//clean up any resources on the smoke test
				//if its not null
				if(test!=null){
					logger.info("CLEANUP: "+clazz.getName());
					test.cleanUp();
				}
			}
		}//end the for-loop

		//if we've gotten this far and there are
		//exceptions we need to log them as an error
		//using this servlet's logger
		if(!Utils.isEmpty(exceptions)){
			StringBuilder b = new StringBuilder();
			b.append(buildClientServerInfo(helper));
			int count=1;
			for (SmoketestException e : exceptions) {
				b.append(count).append(". ").append(e.getResult().getSmokeTest().getClass().getName()).append("\r\n")
				.append(buildErrorAndResultsMessage(e, helper));
				count++;
			}
			logger.error(String.format("%d SMOKETEST(S) FAILED:\r\n%s", count, b.toString()));
		}

		//write the results
		if(logger.isInfoEnabled()){
			logger.info(String.format("Rendering results with %s", renderer.getClass().getName()));
		}

		renderer.render(results, exceptions, helper);

		if(logger.isInfoEnabled()){
			logger.info("Smoketest complete");
		}


	}

}
