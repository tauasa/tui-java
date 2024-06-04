/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.web.smoke;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tauasa.commons.util.DateUtils;
import org.tauasa.commons.util.PatternUtils;
import org.tauasa.commons.util.Utils;
import org.tauasa.commons.util.XProperties;
import org.tauasa.web.ServletHelper;
import org.tauasa.web.smoke.SmoketestMessage.Type;

/**
 * Renders test results in XHTML but uses jQuery to display results in a tabbed format
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class JQueryXHTMLRenderer implements ISmoketestResultsRenderer {

	private static final Logger logger = LoggerFactory.getLogger(JQueryXHTMLRenderer.class);

	public static final String CONTENT_TYPE = "text/html";
	public static final String DEFAULT_DATE_FORMAT = "h:mm:ss a";

	public static final String HOST_KEY = "host";
	public static final String CLIENT_KEY = "client";
	public static final String RESULTS_KEY = "results";
	public static final String FONT_KEY = "font";
	public static final String MARGIN_KEY = "margin";

	public static final String SERVLET_CONTEXT_KEY = "servlet.context";
	public static final String SERVLET_CONTEXT_FILTER_KEY = "servlet.context.filter";
	public static final String SYSTEM_PROPERTIES_KEY = "system.properties";
	public static final String SYSTEM_PROPERTIES_FILTER_KEY = "system.properties.filter";
	public static final String BUILD_INFO_KEY = "build.info";
	public static final String TIMESTAMP_FORMAT_KEY = "timestamp.format";
	public static final String LOUD_RESULTS_KEY = "loud.results";
	public static final String DOWNLOAD_RESOURCES_KEY = "download.resources";

	private ServletHelper mHelper;
	private XProperties mProperties;

	public JQueryXHTMLRenderer() {

	}

	public void init(XProperties smoketestProperties){
		logger.info("Initializing");
		//mProperties=new XProperties(smoketestProperties);
		mProperties=smoketestProperties;

		//if we're not debugging then don't enumerate any of our properties
		if(!logger.isDebugEnabled()){
			return;
		}

		//log all of the properties pertinent to this renderer
		List<String> keys = mProperties.getSortedKeys();

		StringBuilder b = new StringBuilder();

		b.append("\r\n============================================");
		b.append("\r\nProperties:\r\n");

		for (String key : keys) {
			if(!key.startsWith(getClass().getSimpleName()+".")){
				continue;
			}
			b.append(key).append("=").append(mProperties.getProperty(key)).append("\r\n");
		}

		b.append("============================================");

		logger.debug(getClass().getSimpleName()+b.toString());

	}

	private boolean isTrue(String property){
		return "true".equalsIgnoreCase(mProperties.getProperty(getClass().getSimpleName()+"."+property));
	}

	public void render(List<SmoketestResult> results, List<SmoketestException> errors, ServletHelper helper)throws IOException {
		logger.info("Rendering HTML");

		this.mHelper=helper;
		this.mHelper.setContentType("text/html;charset=UTF-8");

		Date today = new Date();

		String requestUri = helper.getRequest().getRequestURI();

		//println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
		println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
		println("<!-- ////////////////////////////////////////////////////////");
		println("Copyright(c) ", DateUtils.getYear(today), " Tauasa Timoteo. All rights reserved.");
		println("///////////////////////////////////////////////////////// -->");
		println("\t<head>");
		//lets make sure results aren't cached in the browser
		println("\t\t<meta name=\"DESCRIPTION\" content=\"Smoketest results for ", requestUri, "\">");
		println("\t\t<meta http-equiv=\"PRAGMA\" content=\"NO-CACHE\">");
		println("\t\t<meta http-equiv=\"CACHE-CONTROL\" content=\"NO-CACHE\">");
		println("\t\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>");
		println("\t\t<title>Smoketest: ", requestUri, "</title>");

		String font = mProperties.getProperty(getClass().getSimpleName()+"."+FONT_KEY, "75% \"Trebuchet MS\", sans-serif");
		String margin = mProperties.getProperty(getClass().getSimpleName()+"."+MARGIN_KEY, "10px");

		println("\t\t<link type=\"text/css\" href=\"?", SmoketestServlet.CACHED_RESOURCE_PARAM, "=css/smoothness/jquery-ui-1.7.2.custom.css\" rel=\"stylesheet\" />");
		println("\t\t<script type=\"text/javascript\" src=\"?", SmoketestServlet.CACHED_RESOURCE_PARAM, "=js/jquery-1.3.2.min.js\"></script>");
		println("\t\t<script type=\"text/javascript\" src=\"?", SmoketestServlet.CACHED_RESOURCE_PARAM, "=js/jquery-ui-1.7.2.custom.min.js\"></script>");
		println("\t\t<script type=\"text/javascript\">$(function(){$('#tabs').tabs();});</script>");
		println("\t\t<style type=\"text/css\">body{font: ", font, "; margin: ", margin,";}</style>");
		println("\t</head>");

		//begin the body node... if we're configured for
		//"loud results" we need to make the background color
		//a garish red or green (we should probably make it
		//blink too ;) - otherwise we just default to a humdrum gray
		println("\t<body bgcolor=\"#",
				isTrue(LOUD_RESULTS_KEY) ? (Utils.isEmpty(errors)?"00FF00":"FF0000") : "EEEEEE",
				"\">");

		println("\t<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">");
		println("\t<tr>");
		println("\t<td><h1>", requestUri, "</h1></td>");
		println("\t<td align=\"right\"><h1>", DateUtils.toMMDDYYYYHHMMSS(today), "</h1></td>");
		println("\t</tr>");
		println("\t</table>");

		//println("\t<h1>", requestUri, "</h1>");

		println("<div id=\"tabs\">");
		println("<ul>");
		println("<li><a href=\"#tabs-0\">Overview</a></li>");
		println("<li><a href=\"#tabs-1\">Config</a></li>");

		long totalElapsedTime = 0;
		int totalErrorMessages = 0;
		int totalWarningMessages = 0;
		int totalInfoMessages = 0;

		if(!Utils.isEmpty(results)){
			//sort the results by the starting time stamp
			Collections.sort(results, new Comparator<SmoketestResult>(){
				public int compare(SmoketestResult o1, SmoketestResult o2){
					return o1.getTestStartTime().compareTo(o2.getTestStartTime());
				}
			});

			//enumerate the tests that were run and provide links to the result
			int tabCount = 2;
			for (SmoketestResult result : results) {
				totalElapsedTime += result.getElapedTime();
				totalErrorMessages += result.getNumMessagesForType(Type.ERROR);
				totalWarningMessages += result.getNumMessagesForType(Type.WARN);
				totalInfoMessages += result.getNumMessagesForType(Type.INFO);

				println("<li><a href=\"#tabs-", tabCount, "\">",
						result.getSmokeTest().getClass().getSimpleName(),
						"</a></li>");
				tabCount++;
			}
		}

		println("</ul>");
		println("<p/>");

		println("\t<div id=\"tabs-0\">");

		println("\t<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" width=\"100%\">");

		println("\t<tr bgcolor=\"#",(Utils.isEmpty(errors)?"00FF00":"FF0000"),"\">");
		println("\t<th colspan=\"2\"><font color=\"#"+(Utils.isEmpty(errors)?"000000":"FFFFFF")+"\">",(Utils.isEmpty(errors)?"SUCCESS":"FAILURE"),"</font></th>");
		println("\t</tr>");

		//write out a short summary that includes:
		//1. Total Number of Tests
		//2. Elapsed Time
		//3. Total Number of Errors
		//4. Total Number of Messages by type (INFO, WARN, ERROR)
		println("\t<tr bgcolor=\"#6666CC\">");
		println("\t<th colspan=\"2\"><font color=\"#FFFFFF\">Summary</font></th>");
		println("\t</tr>");
		println("\t<tr>");
		println("\t<td>Test Count</td><td>", (results==null?0:results.size()), "</td>");
		println("\t</tr>");
		println("\t<tr>");
		println("\t<td>Elapsed Time</td><td>", totalElapsedTime, "ms</td>");
		println("\t</tr>");
		println("\t<tr>");
		println("\t<td>Errors</td><td>", (errors==null?0:errors.size()), "</td>");
		println("\t</tr>");
		println("\t<tr>");
		println("\t<td>Message Count</td><td>Infos: ", totalInfoMessages, ", Warnings: ", totalWarningMessages, ", Errors: ", totalErrorMessages, "</td>");
		println("\t</tr>");

		//write build info
		String buildPropertiesPath = mProperties.getProperty(getClass().getSimpleName()+"."+BUILD_INFO_KEY);

		String buildPropLink = "<a style=\"color:#FFFFFF\" href=\"?"+SmoketestServlet.RESOURCE_FILE_PARAM+"="+buildPropertiesPath+"\">"+buildPropertiesPath+"</a>";

		if(buildPropertiesPath!=null){

			//XInputStreamReader reader = new XInputStreamReader(getClass().getClassLoader().getResourceAsStream(path));
			try{
				printProperties(XProperties.loadProperties(getClass().getClassLoader().getResourceAsStream(buildPropertiesPath)),
						"Build Info ["+(isTrue(DOWNLOAD_RESOURCES_KEY)?buildPropLink:buildPropertiesPath)+"]",
						null, false);
			}catch(Exception e){
				logger.warn("Problem printing build file \""+buildPropertiesPath+"\"", e);
			}
		}

		if(isTrue(HOST_KEY)){

			println("\t<tr bgcolor=\"#6666CC\">");
			println("\t<th colspan=\"2\"><font color=\"#FFFFFF\">Host Info</font></th>");
			println("\t</tr>");

			println("\t<tr>");
			println("\t<td>Host Name</td><td>", InetAddress.getLocalHost().getHostName(), "</td>");
			println("\t</tr>");
			println("\t<tr>");
			println("\t<td>IP Address</td><td>", InetAddress.getLocalHost().getHostAddress(), "</td>");
			println("\t</tr>");
			println("\t<tr>");
			println("\t<td>Date/Time</td><td>", new Date(), "</td>");
			println("\t</tr>");
		}

		if(isTrue(CLIENT_KEY)){
			println("\t<tr bgcolor=\"#6666CC\">");
			println("\t<th colspan=\"2\"><font color=\"#FFFFFF\">Client Info</font></th>");
			println("\t</tr>");
			println("\t<tr>");
			println("\t<td>Remote Address</td><td>", helper.getRemoteAddr(), "</td>");
			println("\t</tr>");
			println("\t<tr>");
			println("\t<td>Request URL</td><td>", helper.getRequestURL(), "</td>");
			println("\t</tr>");
			println("\t<tr>");
			println("\t<td>Query String</td><td>", helper.getQueryString(), "</td>");
			println("\t</tr>");
			println("\t<tr>");
			println("\t<td>User-Agent</td><td>", helper.getUserAgent(), "</td>");
			println("\t</tr>");
		}

		//write system properties (use filter if specified)
		if(isTrue(SYSTEM_PROPERTIES_KEY)){
			printProperties(System.getProperties(),
					"System Properties",
					mProperties.getProperty(getClass().getSimpleName()+"."+SYSTEM_PROPERTIES_FILTER_KEY), false);
		}

		//write servlet context attributes
		if(isTrue(SERVLET_CONTEXT_KEY)){
			//add the appropriate servlet context attributes to a hashtable
			ServletContext context = helper.getServletContext();
			Enumeration<String> names = context.getAttributeNames();
			Hashtable<String, String> hash = new Hashtable<String, String>();
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				String value = context.getAttribute(name)==null?"":context.getAttribute(name).toString();
				hash.put(name, value);
			}
			printProperties(hash,
					"ServletContext Attributes",
					mProperties.getProperty(getClass().getSimpleName()+"."+SERVLET_CONTEXT_FILTER_KEY), false);
		}

		println("\t</table>");
		println("\t</div>");
		println("\t<p/>");

		//end tab 0

		///////////////////////////////////////////////////
		//dump the properties file to the screen
		println("\t<div id=\"tabs-1\">");
		println("\t<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" width=\"100%\">");

		String propFileLink = "<a style=\"color:#FFFFFF\" href=\"?"+SmoketestServlet.RESOURCE_FILE_PARAM+"="+SmoketestServlet.PROPERTIES_FILE+"\">"+SmoketestServlet.PROPERTIES_FILE+"</a>";

		println("\t<tr bgcolor=\"#6666CC\">");
		println("\t<th colspan=\"2\"><font color=\"#FFFFFF\">Configuration [",
				(isTrue(DOWNLOAD_RESOURCES_KEY)?propFileLink:SmoketestServlet.PROPERTIES_FILE),
				"]</font></th>");
		println("\t</tr>");
		println("\t<tr bgcolor=\"#CCCCCC\">");
		println("\t<th>Property Key</th><th>Value</th>");
		println("\t</tr>");

		List<String> propKeys = mProperties.getSortedKeys();

		for (String key : propKeys) {
			println("\t<tr>");
			println("\t<td>", key, "</td><td>", mProperties.get(key), "</td>");
			println("\t</tr>");
		}

		println("\t</table>");
		println("\t</div>");
		println("\t<p/>");
		//end tab 1
		//////////////////////////////////////////////////

		if(isTrue(RESULTS_KEY) && !Utils.isEmpty(results)){

			//boolean mergeFields = isTrue(MERGE_FIELDS_KEY);

			logger.info("Writing results for "+results.size()+" smoketests");

			String tsFormat = mProperties.getProperty(getClass().getSimpleName()+"."+TIMESTAMP_FORMAT_KEY, DEFAULT_DATE_FORMAT);

			int tabCount = 1;
			for (SmoketestResult result : results) {
				tabCount++;

				println("\t<div id=\"tabs-", tabCount,"\">");

				////////////////////////////////////////////////
				//write out a small summary
				String poc = result.getSmokeTest().getPointOfContact();
				if(poc!=null &&
						(PatternUtils.isEmail(poc) || poc.toLowerCase().startsWith("http"))){
					String href = PatternUtils.isEmail(poc) ?
							"mailto:"+poc+"?Subject="+result.getSmokeTest().getClass().getSimpleName()+"-"+requestUri : poc;
					poc = "<a href=\""+href+"\">"+poc+"</a>";
				}else if(poc==null){
					poc = "Point-of-Contact not available";
				}

				String description = result.getSmokeTest().getDescription();
				if(Utils.isEmpty(description)){
					description = "Description not available";
				}

				println("\t<table border=\"0\" cellpadding=\"3\" cellspacing=\"0\" bgcolor=\"#EEEEEE\" width=\"100%\">");
				println("\t<tr>");
				println("\t<td align=\"right\" valign=\"top\">");

				println("\t\t<table border=\"0\" cellpadding=\"3\" cellspacing=\"1\" width=\"100%\">");
				println("\t\t<tr bgcolor=\"#FFFFCA\">");
				println("\t\t<th align=\"right\" nowrap=\"true\">Elapsed Time:</th><th align=\"right\">", result.getElapedTime(), "ms</th>");
				println("\t\t</tr>");
				println("\t\t<tr bgcolor=\"", Type.INFO.bgColor(), "\">");
				println("\t\t<th align=\"right\" nowrap=\"true\"><font color=\"", Type.INFO.fontColor(), "\">Info Messages:</font></th><th align=\"right\"><font color=\"", Type.INFO.fontColor(), "\">", result.getNumMessagesForType(Type.INFO), "</font></th>");
				println("\t\t</tr>");
				println("\t\t<tr bgcolor=\"", Type.WARN.bgColor(), "\">");
				println("\t\t<th align=\"right\" nowrap=\"true\"><font color=\"", Type.WARN.fontColor(), "\">Warning Messages:</font></th><th align=\"right\"><font color=\"", Type.WARN.fontColor(), "\">", result.getNumMessagesForType(Type.WARN), "</font></th>");
				println("\t\t</tr>");
				println("\t\t<tr bgcolor=\"", Type.ERROR.bgColor(), "\">");
				println("\t\t<th align=\"right\" nowrap=\"true\"><font color=\"", Type.ERROR.fontColor(), "\">Error Messages:</font></th><th align=\"right\"><font color=\"", Type.ERROR.fontColor(), "\">", result.getNumMessagesForType(Type.ERROR), "</font></th>");
				println("\t\t</tr>");
				println("\t\t</table>");

				println("\t</td>");
				println("\t<td width=\"75%\" valign=\"top\">");
				println("\t<strong>FQN:</strong> <i>", result.getSmokeTest().getClass().getName(), "</i><br/>");
				println("\t<strong>Contact:</strong> <i>", poc, "</i><br/>");
				println("\t<strong>Description:</strong> <i>", description, "</i><br/>");
				println("\t</td>");
				println("\t</tr>");
				println("\t</table>");

				//end of small summary
				////////////////////////////////////////////////

				println("\t<p/>");

				println("\t<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" width=\"100%\">");
				println("\t<tr bgcolor=\"#6666CC\">");
				println("\t<th><font color=\"#FFFFFF\">Type</font></th>");
				println("\t<th><font color=\"#FFFFFF\">Message</font></th>");
				println("\t<th><font color=\"#FFFFFF\">Timestamp</font></th>");
				println("\t</tr>");

				//sort messages by timestamp
				Collections.sort(result, new Comparator<SmoketestMessage>(){
					public int compare(SmoketestMessage o1, SmoketestMessage o2){
						return o1.getTimestamp().compareTo(o2.getTimestamp());
					}
				});

				for (SmoketestMessage msg : result) {
					//write messages in a table

					//change bgcolor based on message type
					String bgcolor = msg.getType().bgColor();
					String fontColor = msg.getType().fontColor();

					println("\t<tr bgcolor=\"", bgcolor, "\">");

					println("\t<td valign=\"top\" align=\"center\"><font color=\"", fontColor, "\">",
							msg.getType().name(), "</td>",
							"<td valign=\"top\"><font color=\"", fontColor, "\">",
							msg.getFormattedMessage(),
							"</font>&nbsp;</td>",
							"<td valign=\"top\" align=\"center\"><font color=\"", fontColor, "\">",
							DateUtils.formatDate(msg.getTimestamp(), tsFormat),
							"</font></td>");

					println("\t</tr>");
				}

				println("\t</table>");
				println("\t</div>");
				println("\t<p/>");

			}//end for loop

		}

		println("\t<p/><br/>");

		//println("&copy;", DateUtils.getYear(today), " Tauasa Timoteo. All rights reserved.<br/>");


		println("\t</body>");
		println("</html>");

	}

	@SuppressWarnings("unchecked")
	private void printProperties(@SuppressWarnings("rawtypes") Hashtable props, String tableTitle, String filter, boolean standaloneTable)throws IOException{
		if(standaloneTable){
			println("\t<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" width=\"100%\">");
		}

		println("\t<tr bgcolor=\"#6666CC\">");
		println("\t<th colspan=\"2\" width=\"30%\"><font color=\"#FFFFFF\">",
				tableTitle,
				filter!=null?" [Filter: "+filter+"]":"",
				"</font></th>");
		println("\t</tr>");

		Pattern p = null;
		if(filter!=null){
			p = Pattern.compile(filter);
		}

		@SuppressWarnings("rawtypes")
		ArrayList keys = new ArrayList(props.keySet());
		Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);

		for(int i=0;i<keys.size();i++){
			String key = keys.get(i).toString();
			if(p==null || p.matcher(key).matches()){
				String value = props.get(key)!=null ? props.get(key).toString() : "";
				if(value.toLowerCase().startsWith("http")){
					value = "<a href=\""+value+"\">" + value + "</a>";
				}
				//show the property is there is no filter or a filter match
				println("\t<tr>");
				println("\t<td>", key, "</td><td>", value, "&nbsp;</td>");
				println("\t</tr>");
			}
		}

		if(standaloneTable){
			println("\t</table>");
			println("\t<p/>");
		}
	}

	private void println(Object...args)throws IOException{
		if(!Utils.isEmpty(args)){
			for (Object arg : args) {
				mHelper.getResponse().getWriter().print(arg);
			}
		}
		mHelper.getResponse().getWriter().println();

	}

}
