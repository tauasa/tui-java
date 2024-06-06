/*
 * Copyright 2012 Tauasa Timoteo
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this 
 * software and associated documentation files (the “Software”), to deal in 
 * the Software without restriction, including without limitation the rights to use, 
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of 
 * the Software, and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE 
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT 
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR 
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package org.tauasa.web.smoke;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tauasa.commons.io.XInputStreamReader;
import org.tauasa.commons.util.Utils;
import org.tauasa.commons.util.XProperties;
import org.tauasa.web.ServletHelper;

/**
 * Invokes one or more {@link ISmoketest} implementations
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

	public static final String JQUERY_PATH = "/jquery-ui-1.7.2.custom.zip";
	public static final String CACHED_RESOURCE_PARAM = "_cache";
	public static final String RESOURCE_FILE_PARAM = "_res";
	private static final int CACHE_BUFFER_SIZE = 32768;

	/**
	 * String array of paths to resources files that may be downloaded
	 * from this servlet (very limited to ensure security)
	 * */
	public static final String[] AUTHORIZED_RESOURCE_FILES = {
		PROPERTIES_FILE
		,"/WEB-INF/maven-build.properties"
		//,"struts-config.xml"
		//,"jboss-web.xml"
		//,"web.xml"
	};

	private XProperties smoketestProperties;
	private List<Class<? extends ISmoketest>> smokeTests;
	private ISmoketestResultsRenderer renderer;
	private boolean allowVirtualHost = true;
	private ISmoketestExceptionHandler exceptionHandler;
	private Map<String, CacheResource> cachedResources;

	public SmoketestServlet() {

	}

	private static String getContentTypeForName(String name){
		String n = name.toLowerCase();
		if(n.endsWith(".js")){
			return "text/javascript";
		}else if(n.endsWith(".css")){
			return "text/css";
		}else if(n.endsWith(".png")){
			return "image/PNG";
		}else if(n.endsWith(".jpg") || n.endsWith(".jpeg")){
			return "image/JPEG";
		}else if(n.endsWith(".gif")){
			return "image/GIF";
		}
		return "text/plain";
	}

	public static Map<String, CacheResource> loadCacheResourcesFromZip(String zipPath)throws Exception{
		HashMap<String, CacheResource> map = new HashMap<>();

		ZipInputStream zipIn = new ZipInputStream(SmoketestServlet.class.getResourceAsStream(zipPath));

		byte[] buffer = new byte[CACHE_BUFFER_SIZE];

		ZipEntry entry;

		while((entry=zipIn.getNextEntry())!=null){
			String name = entry.getName();
			byte[] content;
			try (ByteArrayOutputStream bOut = new ByteArrayOutputStream()) {
				int n;
				while ((n = zipIn.read(buffer, 0, CACHE_BUFFER_SIZE)) > -1){
					bOut.write(buffer, 0, n);
				}
				content = bOut.toByteArray();
				if(content.length==0){
					continue;//skip empty content
				}
			}
			zipIn.closeEntry();

			map.put(name, new CacheResource(getContentTypeForName(name), content));
		}

		return map;
	}

	private void initCachedResources()throws Exception {
		//extract jQuery stuff from zip file and add to the cache
		cachedResources = loadCacheResourcesFromZip(JQUERY_PATH);

	}

	/**
	 * Streams the resource requested in the {@value #CACHED_RESOURCE_PARAM} parameter
	 * */
	private void streamCachedResource(ServletHelper helper)throws IOException, ServletException{
		String resPath = helper.getParam(CACHED_RESOURCE_PARAM);
		if(Utils.isEmpty(cachedResources)){
			logger.warn("Could not load cached resource for "+resPath+" because the cache is empty");
			return;
		}

		CacheResource res = cachedResources.get(resPath);
		if(res==null){
			logger.warn("Cached resource for "+resPath+" does not exist");
			return;
		}

		logger.info("Writing cached resource "+resPath);

		helper.setContentType(res.contentType);
            try (ServletOutputStream out = helper.getOutputStream()) {
                out.write(res.content);
                out.flush();
            }
	}

	public static class CacheResource{
		String contentType;
		byte[] content;
		CacheResource(String contentType, byte[] content){
			this.contentType=contentType;
			this.content=content;
		}
		public String getContentType(){
			return contentType;
		}
		public byte[] getContent(){
			return content;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		//load jquery stuff into a cache so it can be served from this servlet
		try{
			initCachedResources();
		}catch(Exception e){
			logger.error("Could not load "+JQUERY_PATH+" into memory", e);
		}

		if(logger.isInfoEnabled()){
			logger.info(String.format("Initializing from %s", PROPERTIES_FILE));
		}

		try{
			smoketestProperties = XProperties.loadProperties(getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE));
			if(logger.isDebugEnabled()){
				logger.debug(String.format("%s\r\n%s", PROPERTIES_FILE, smoketestProperties.toString()));
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
		logger.info("Creating ISmoketestResultsRenderer of type "+strRenderer);
		try{
			renderer = (ISmoketestResultsRenderer)Class.forName(strRenderer).getConstructors()[0].newInstance();
			renderer.init(smoketestProperties);//initialize
		}catch(ClassNotFoundException | IllegalAccessException | InstantiationException | IllegalArgumentException | InvocationTargetException | SecurityException e){
			logger.error("Could not create renderer instance of type "+strRenderer, e);
			throw new ServletException(e);
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

	private void streamResourceFile(ServletHelper helper) throws ServletException, IOException{

		String path = helper.getParam(RESOURCE_FILE_PARAM);

		logger.info("Attempting to download "+path);

		if(!Utils.hasParm(AUTHORIZED_RESOURCE_FILES, path)){
			String user = helper.getRequest().getRemoteUser();
			String msg = (user==null?"Client at "+helper.getRemoteAddr():user)+" is not authorized to download "+path;
			logger.error(msg);
			throw new ServletException(msg);
		}

		XInputStreamReader reader = new XInputStreamReader(getClass().getClassLoader().getResourceAsStream(path));

		byte[] content = reader.read();

		//always stream resource as a plain text
		helper.setContentType("text/plain");

		try (ServletOutputStream out = helper.getOutputStream()) {
			out.write(content);
			out.flush();
		}

	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		ServletHelper helper = new ServletHelper(super.getServletContext(), req, resp);

		//handle request for a cached resource
		if(helper.paramExists(CACHED_RESOURCE_PARAM)){
			streamCachedResource(helper);
			return;
		}

		//handle request for streaming a file (i.e. maven-build.properties)
		if(helper.paramExists(RESOURCE_FILE_PARAM)){
			streamResourceFile(helper);
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
			//name, not a virtual host (i.e. java.tauasa, www.tauasa)
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
