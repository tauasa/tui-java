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
import java.util.Properties;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tauasa.commons.util.DateUtils;
import org.tauasa.commons.util.Utils;
import org.tauasa.commons.util.XProperties;
import org.tauasa.web.ServletHelper;
import org.tauasa.web.smoke.SmoketestMessage.Type;

/**
 * Simple {@link ISmoketestResultsRenderer} that renders smoke test results as an XHTML-formatted page
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class XHTMLRenderer implements ISmoketestResultsRenderer {

	private static final Logger logger = LoggerFactory.getLogger(XHTMLRenderer.class);

	//public static final String CONTENT_TYPE = "application/xhtml+xml";
	//public static final String CONTENT_TYPE = "text/xml";
	public static final String CONTENT_TYPE = "text/html";

	public static final String HOST_KEY = "host";
	public static final String CLIENT_KEY = "client";
	public static final String RESULTS_KEY = "results";

	public static final String SERVLET_CONTEXT_KEY = "servlet.context";
	public static final String SERVLET_CONTEXT_FILTER_KEY = "servlet.context.filter";
	public static final String SYSTEM_PROPERTIES_KEY = "system.properties";
	public static final String SYSTEM_PROPERTIES_FILTER_KEY = "system.properties.filter";
	public static final String BUILD_INFO_KEY = "build.info";

	private ServletHelper mHelper;
	private Properties mProperties;

	public XHTMLRenderer() {

	}

	public void init(XProperties smoketestProperties){
		logger.info("Initializing");
		this.mProperties=smoketestProperties;
	}

	private boolean isTrue(String property){
		return "true".equalsIgnoreCase(mProperties.getProperty(getClass().getSimpleName()+"."+property));
	}

	public void render(List<SmoketestResult> results, List<SmoketestException> errors, ServletHelper helper)throws IOException {
		logger.info("Rendering HTML");

		this.mHelper=helper;
		this.mHelper.setContentType("text/html;charset=UTF-8");


		String requestUri = helper.getRequest().getRequestURI();

		//println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
		println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
		println("<!-- ////////////////////////////////////////////////////////");
		println("Copyright(c) ", DateUtils.getYear(new Date()), " Tauasa Timoteo. All rights reserved.");
		println("///////////////////////////////////////////////////////// -->");
		println("\t<head>");
		println("\t\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>");
		println("\t\t<title>Smoketest: ", requestUri, "</title>");
		println("\t</head>");

		println("\t<body>");

		println("\t<h1>", requestUri, "</h1>");

		if(!Utils.isEmpty(results)){
			//sort the results by the starting time stamp
			Collections.sort(results, new Comparator<SmoketestResult>(){
				public int compare(SmoketestResult o1, SmoketestResult o2){
					return o1.getTestStartTime().compareTo(o2.getTestStartTime());
				}
			});

			//println("<strong>Tests:</strong>");

			println("<ol>");

			//enumerate the tests that were run and provide links to the result
			for (SmoketestResult result : results) {
				String testName = result.getSmokeTest().getClass().getSimpleName();
				int numWarnings = result.getNumMessagesForType(Type.WARN);
				int numErrors = result.getNumMessagesForType(Type.ERROR);
				println("<li><a href=\"#", testName, "\">", testName, "</a> [", result.getElapedTime(), "ms, ", numErrors, " errors, ", numWarnings, " warnings]</li>");
			}

			println("</ol>");
			println("<p/>");

		}

		println("\t<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" width=\"100%\">");

		//always show overall results
		//println("\t<tr bgcolor=\"#6666CC\">");
		//println("\t<th colspan=\"2\"><font color=\"#FFFFFF\">Overall Result</font></th>");
		//println("\t</tr>");

		println("\t<tr bgcolor=\"#",(Utils.isEmpty(errors)?"00FF00":"FF0000"),"\">");
		println("\t<th colspan=\"2\"><font color=\"#"+(Utils.isEmpty(errors)?"000000":"FFFFFF")+"\">",(Utils.isEmpty(errors)?"SUCCESS":"FAILURE"),"</font></th>");
		println("\t</tr>");

		//writer build info
		String buildPropertiesPath = mProperties.getProperty(getClass().getSimpleName()+"."+BUILD_INFO_KEY);
		if(buildPropertiesPath!=null){
			printProperties(XProperties.loadProperties(getClass().getClassLoader().getResourceAsStream(buildPropertiesPath)),
					"Build Info",
					null, false);
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

		println("\t</table>");
		println("\t<p/>");

		if(isTrue(RESULTS_KEY)){
			println("\t<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" width=\"100%\">");

			println("\t<tr bgcolor=\"#6666CC\">");
			println("\t<th colspan=\"3\"><font color=\"#FFFFFF\">Test Results</font></th>");
			println("\t</tr>");

			if(Utils.isEmpty(results)){

				logger.warn("No results to write");

				println("\t<tr bgcolor=\"#CCCCCC\">");
				println("\t<th colspan=\"3\">No Results</th>");
				println("\t</tr>");

			}else{

				logger.info("Writing results for "+results.size()+" smoketests");

				for (SmoketestResult result : results) {
					String testName = result.getSmokeTest().getClass().getSimpleName();
					println("\t<tr bgcolor=\"#FFFFCA\">");
					println("\t<th colspan=\"2\"><a name=\"", testName, "\">",
							testName,
							"</a> [Elapsed Time: ",
							result.getElapedTime(),
							"ms]</th>");
					println("\t</tr>");

					println("\t<tr bgcolor=\"#CCCCCC\">");
					println("\t<th>Message</th><th>Timestamp</th>");
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

						println("\t<td valign=\"top\"><font color=\"", fontColor, "\">",
								msg.getFormattedMessage(),
								"</font>&nbsp;</td>",
								"<td valign=\"top\" align=\"center\"><font color=\"", fontColor, "\">",
								DateUtils.toMMDDYYYYHHMMSS(msg.getTimestamp()),
								"</font></td>");
								//,"<td valign=\"top\"><font color=\"", fontColor, "\">",
								//msg.isErrorMessage()?msg.getError().getMessage():"", "</font>&nbsp;</td>");
						println("\t</tr>");
					}
				}

			}

			println("\t</table>");
			println("\t<p/>");
		}

		//write servlet context attributes
		if(isTrue(SERVLET_CONTEXT_KEY)){
			//add the appropriate servlet context attributes to a hashtable
			ServletContext context = helper.getServletContext();
			Enumeration<String> names = context.getAttributeNames();
			Hashtable<String, Object> hash = new Hashtable<String, Object>();
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				String value = context.getAttribute(name)==null?"":context.getAttribute(name).toString();
				hash.put(name, value);
			}
			printProperties(hash,
					"ServletContext Attributes",
					mProperties.getProperty(getClass().getSimpleName()+"."+SERVLET_CONTEXT_FILTER_KEY));
		}

		//write system properties (use filter if specified)
		if(isTrue(SYSTEM_PROPERTIES_KEY)){
			printProperties(System.getProperties(),
					"System Properties",
					mProperties.getProperty(getClass().getSimpleName()+"."+SYSTEM_PROPERTIES_FILTER_KEY));
		}

		println("\t</body>");
		println("</html>");

	}

	private void printProperties(@SuppressWarnings("rawtypes") Hashtable props, String tableTitle, String filter)throws IOException{
		printProperties(props, tableTitle, filter, true);
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

		ArrayList<String> keys = new ArrayList<String>(props.keySet());
		Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);

		for(int i=0;i<keys.size();i++){
			String key = keys.get(i).toString();
			if(p==null || p.matcher(key).matches()){
				//show the property is there is no filter or a filter match
				println("\t<tr>");
				println("\t<td>", key, "</td><td>", props.get(key), "&nbsp;</td>");
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
