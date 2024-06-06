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
package org.tauasa.commons.net;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tauasa.commons.io.XInputStreamReader;
import org.tauasa.commons.util.Utils;
import org.tauasa.commons.xml.XmlUtils;
import org.w3c.dom.Document;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Static utility methods for performing HTTP GET and POST operations. This class
 * is backed by the Apache {@link HttpClient}
 *
 * @author Tauasa Timoteo
 * 
 */
public class HTTPUtils {

	private static final Logger logger = LoggerFactory.getLogger(HTTPUtils.class);

	/**
	 * Default connection timeout in milliseconds. This property can be overridden
	 * with the {@value #DEFAULT_TIMEOUT_KEY} system property. A value of zero (the default)
	 * means a timeout should not be used (so you should probably set this, duh)
	 * */
	public static final int DEFAULT_TIMEOUT = 0;
	/**
	 * System property key for the default connection timeout
	 * */
	public static final String DEFAULT_TIMEOUT_KEY = String.format("%s.timeout", HTTPUtils.class.getName());

	private HTTPUtils() {
	}

	/**
	 * Creates an Apache {@link HttpClient} from the specified {@link Configuration}, posts a payload to the specified URL,
	 * and returns a {@link HttpResponse} object. The payload is usually an XML/SOAP document
	 * */
	public static HttpResponse postPayload(String strUrl, Payload payload, Configuration config)throws Exception{
		if(logger.isDebugEnabled()){
			logger.debug(String.format("Posting %d byte payload to %s", payload.getSize(), strUrl));
		}
		HttpClient client = createHttpClient(config);

		PostMethod method = new PostMethod(strUrl);
		method.setRequestEntity(new ByteArrayRequestEntity(payload.getContent(), payload.getContentType()));
		if(payload.getHeaders()!=null){
			Set<String> names = payload.getHeaders().keySet();
			for (String name : names) {
				Object value = payload.getHeaders().get(name);
				if(value!=null){
					method.setRequestHeader(name, value.toString());
				}
			}
		}

		try{
			return execute(client, method);
		}finally{
			method.releaseConnection();
		}
	}

	/**
	 * Equivalent to {@link #postPayload(String, byte[], Configuration)} with a null {@link Configuration} object
	 * */
	public static HttpResponse postPayload(String strUrl, Payload payload)throws Exception{
		return postPayload(strUrl, payload, null);

	}

	/**
	 * Creates an Apache {@link HttpClient} from the specified {@link Configuration} and posts the specified
	 * parameters to an URL and returns a {@link HttpResponse} object
	 * */
	public static HttpResponse doPost(String strUrl, Map<String, ?> parameters, Configuration config)throws Exception{
		if(logger.isDebugEnabled()){
			logger.debug(String.format("HTTP POST %s with %d parameters", strUrl, parameters==null?0:parameters.size()));
		}
		HttpClient client = createHttpClient(config);

		PostMethod method = new PostMethod(strUrl);

		if(!Utils.isEmpty(parameters)){
			for (String key : parameters.keySet()) {
				String val = parameters.get(key)==null ? "" : parameters.get(key).toString();
				if(logger.isDebugEnabled()){
					logger.debug(String.format("Parameter: %s=%s", key, val));
				}
				method.addParameter(key, val);
			}
		}

		try{
			return execute(client, method);
		}finally{
			method.releaseConnection();
		}
	}

	/**
	 * Posts parameters to the specified URL using a default Apache {@link HttpClient}
	 * */
	public static HttpResponse doPost(String strUrl, Map<String, ?> parameters)throws Exception{
		return doPost(strUrl, parameters, null);
	}

	/**
	 * Creates an Apache {@link HttpClient} from the specified {@link Configuration} and gets the content of the specified URL
	 * */
	public static HttpResponse doGet(String strUrl, Configuration config)throws Exception{
		return doGet(strUrl, null, null, config);
	}

	public static HttpResponse doGet(String strUrl, int connectionTimeout)throws Exception{
		return doGet(strUrl, null, null, new Configuration(connectionTimeout));
	}

	/**
	 * Creates an Apache {@link HttpClient} from the specified {@link Configuration} and gets the content of the specified URL
	 * using Basic authentication if necessary
	 * */
	public static HttpResponse doGet(String strUrl, String userName, String password, Configuration config)throws Exception{
		if(logger.isDebugEnabled()){
			logger.debug(String.format("HTTP GET %s", strUrl));
		}
		HttpClient client = createHttpClient(config);

		GetMethod method = new GetMethod(strUrl);

		if(!Utils.isEmpty(userName) && !Utils.isEmpty(password)){
			//setup Basic authentication
			if(logger.isDebugEnabled()){
				logger.debug("Performing basic authentication");
			}
			client.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
			method.setDoAuthentication(true);
		}

		try{
			return execute(client, method);
		}finally{
			method.releaseConnection();
		}
	}

	/**
	 * Gets the content of the specified URL using a default Apache {@link HttpClient}
	 * */
	public static HttpResponse doGet(String strUrl)throws Exception{
		return doGet(strUrl, null, null, null);
	}

	/**
	 * Gets the content of the specified URL using a default Apache {@link HttpClient} and using the specified
	 * username/password for Basic Authentication
	 * */
	public static HttpResponse doGet(String strUrl, String userName, String password)throws Exception{
		return doGet(strUrl, userName, password, null);
	}

	/**
	 * Creates a new instance of Apache {@link HttpClient} with a default connection timeout of {@value #DEFAULT_TIMEOUT}ms.
	 * The timeout used by this method can be overridden in the {@value #DEFAULT_TIMEOUT_KEY} system property
	 * */
	public static HttpClient createHttpClient(){
		return createHttpClient(Integer.parseInt(System.getProperty(DEFAULT_TIMEOUT_KEY, String.valueOf(DEFAULT_TIMEOUT))));
	}

	/**
	 * Creates a new instance of Apache {@link HttpClient} with the specified connection timeout
	 * */
	public static HttpClient createHttpClient(int connectionTimeout){
		return createHttpClient(new Configuration(connectionTimeout));
	}

	/**
	 * Creates a new instance of Apache {@link HttpClient} with the specified connection timeout and proxy settings
	 * */
	public static HttpClient createHttpClient(Configuration config){
		if(logger.isDebugEnabled()){
			logger.debug("Creating HttpClient");
		}
		if(config==null){
			config = new Configuration();
		}

		HttpClient client = new HttpClient();
		HttpConnectionManager connManager = client.getHttpConnectionManager();
		HttpConnectionManagerParams params = connManager.getParams();

		//set the connection timeout in milliseconds
		if(config.connectionTimeout > 0){
			if(logger.isDebugEnabled()){
				logger.debug(String.format("HttpClient connection timeout: %d", config.connectionTimeout));
			}
			params.setConnectionTimeout(config.connectionTimeout);
		}

		if(!Utils.isEmpty(config.proxyHost)){
			if(config.proxyPort < 1){
				throw new IllegalArgumentException(String.format("Invalid proxy port specified: %d", config.proxyPort));
			}
			if(logger.isDebugEnabled()){
				logger.debug(String.format("Setting proxy to %s:%d", config.proxyHost, config.proxyPort));
			}

			HostConfiguration cfg = client.getHostConfiguration();
			cfg.setProxy(config.proxyHost, config.proxyPort);

			if(!Utils.isEmpty(config.proxyUser) && !Utils.isEmpty(config.proxyPass)){
				if(logger.isDebugEnabled()){
					logger.debug(String.format("Setting proxy authorization to %s/%s", config.proxyUser, config.proxyPass));
				}
				HttpState state = client.getState();
				state.setProxyCredentials(AuthScope.ANY, new
						UsernamePasswordCredentials(config.proxyUser, config.proxyPass));
			}
		}
		return client;
	}

	/**
	 * Wrapper class for posting a payload (i.e. SOAP message)
	 * */
	public static class Payload{

		private byte[] content;
		private String contentType;
		private HashMap<String, String> headers;
		public Payload() {
			super();
		}

		public Payload(byte[] content, String contentType, HashMap<String, String> headers) {
			super();
			this.content = content;
			this.contentType = contentType;
			this.headers = headers;
		}

		public Payload(Document content, String contentType, HashMap<String, String> headers)throws Exception{
			this(XmlUtils.toByteArray(content), contentType, headers);
		}

		public Payload(String content, String contentType, HashMap<String, String> headers)throws Exception{
			this(content.getBytes(), contentType, headers);
		}

		public Payload(byte[] content, String contentType) {
			this(content, contentType, null);
		}

		public Payload(Document content, String contentType)throws Exception{
			this(content, contentType, null);
		}

		public Payload(String content, String contentType)throws Exception{
			this(content, contentType, null);
		}

		public Payload(byte[] content) {
			this(content, null, null);
		}

		public Payload(Document content)throws Exception{
			this(content, null, null);
		}

		public Payload(String content)throws Exception{
			this(content, null, null);
		}

		public void addHeader(String name, String value){
			if(headers==null){
				headers = new HashMap<>();
			}
			headers.put(name, value);
		}

		public int getSize(){
			return content.length;
		}

		public String getContentType() {
			return contentType;
		}

		public void setContentType(String contentType) {
			this.contentType = contentType;
		}

		public HashMap<String, String> getHeaders() {
			return headers;
		}

		public void setHeaders(HashMap<String, String> headers) {
			this.headers = headers;
		}

		public byte[] getContent() {
			return content;
		}

		public void setContent(byte[] content) {
			this.content = content;
		}
	}

	/**
	 * Executes an {@link HttpMethod} on the specified {@link HttpClient} and returns a new {@link HttpResponse}
	 * */
	private static HttpResponse execute(HttpClient client, HttpMethod method)throws Exception{
		if(logger.isDebugEnabled()){
			logger.debug(String.format("Executing %s method", method.getName()));
		}
		int code = client.executeMethod(method);
		if(logger.isDebugEnabled()){
			logger.debug(String.format("Response code: %d", code));
		}
		HttpResponse res = new HttpResponse(code, XInputStreamReader.readBytes(method.getResponseBodyAsStream()));
		Header[] headers = method.getResponseHeaders();
		for (Header header : headers) {
			String name = header.getName();
			String value = header.getValue();
			if(logger.isDebugEnabled()){
				logger.debug(String.format("Response header: %s = %s", name, value));
			}
			res.addHeader(name, value);
		}
		return res;
	}


	/**
	 * A minimal wrapper bean for HTTP response information (status/code, body, content-type, etc)
	 * */
	public static class HttpResponse{
		public static final String CONTENT_TYPE = "Content-Type";
		/**
		 * HTTP status (i.e. 200, 404, etc)
		 * */
		private int status;
		/**
		 * The body of the response
		 * */
		private byte[] body;
		/**
		 * Resonse headers
		 * */
		private HashMap<String, String> headers;
		public HttpResponse(int status, byte[] body){
			this.status=status;
			this.body=body;
		}
		public int getStatus() {
			return status;
		}
		/**
		 * Returns the body of this response
		 * */
		public byte[] getBody() {
			return body;
		}
		public int getContentLength() {
			return body==null?0:body.length;
		}
		protected void addHeader(String name, String value){
			if(headers==null){
				headers = new HashMap<>();
			}
			headers.put(name.toLowerCase(), value);
		}
		/**
		 * Returns the header value for the specified name or null if it doesn't exist. Matching is
		 * <strong>case-insensitve</strong>
		 * */
		public String getHeader(String name){
			return headers==null ? null : headers.get(name.toLowerCase());
		}
		/**
		 * Returns the body of this response as a {@link String}
		 * */
		public String getBodyAsString(){
			return body==null ? null : new String(body);
		}
		/**
		 * Equivalent to invoking {@link #getHeader(String)} with a name of \"{@value #CONTENT_TYPE}\"
		 * */
		public String getContentType() {
			return getHeader(CONTENT_TYPE);
		}
		/**
		 * Returns the body of this response as an XML {@link Document}
		 * */
		public Document getBodyAsXml()throws Exception{
			return XmlUtils.createDocument(body);
		}

		/**
		 * Returns the body of this response as a {@link JsonElement}
		 * */
		public JsonElement getBodyAsJson(){
			return JsonParser.parseString(getBodyAsString());
		}
	}

	/**
	 * Simple configuration bean for Apache {@link HttpClient}
	 * */
	public static class Configuration{
		/**
		 * Connection timeout in milliseconds
		 * */
		int connectionTimeout;
		/**
		 * Proxy port
		 * */
		int proxyPort;
		/**
		 * Proxy host
		 * */
		String proxyHost;
		/**
		 * Proxy user
		 * */
		String proxyUser;
		/**
		 * Password for proxy user
		 * */
		String proxyPass;
		public Configuration() {

		}
		public Configuration(int connectionTimeout) {
			this.connectionTimeout=connectionTimeout;
		}
		public Configuration(int connectionTimeout, String proxyHost, int proxyPort) {
			this(connectionTimeout);
			this.proxyHost=proxyHost;
			this.proxyPort=proxyPort;
		}
		public Configuration(int connectionTimeout, String proxyHost, int proxyPort, String proxyUser, String proxyPass) {
			this(connectionTimeout, proxyHost, proxyPort);
			this.proxyUser=proxyUser;
			this.proxyPass=proxyPass;
		}
		public int getConnectionTimeout() {
			return connectionTimeout;
		}
		public void setConnectionTimeout(int connectionTimeout) {
			this.connectionTimeout = connectionTimeout;
		}
		public int getProxyPort() {
			return proxyPort;
		}
		public void setProxyPort(int proxyPort) {
			this.proxyPort = proxyPort;
		}
		public String getProxyHost() {
			return proxyHost;
		}
		public void setProxyHost(String proxyHost) {
			this.proxyHost = proxyHost;
		}
		public String getProxyUser() {
			return proxyUser;
		}
		public void setProxyUser(String proxyUser) {
			this.proxyUser = proxyUser;
		}
		public String getProxyPass() {
			return proxyPass;
		}
		public void setProxyPass(String proxyPass) {
			this.proxyPass = proxyPass;
		}

	}

}
