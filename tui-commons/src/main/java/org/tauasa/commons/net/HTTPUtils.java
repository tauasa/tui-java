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
package org.tauasa.commons.net;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tauasa.commons.util.Utils;
import org.tauasa.commons.xml.XmlUtils;
import org.w3c.dom.Document;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Static utility methods for performing HTTP GET and POST operations, backed by
 * the JDK's {@link HttpClient}. HTTPS certificates and host names are always
 * checked, every request has a timeout, and Basic credentials are only ever sent
 * to the URL they were given for, over HTTPS.
 *
 * @author Tauasa Timoteo
 * 
 */
public class HTTPUtils {

	private static final Logger logger = LoggerFactory.getLogger(HTTPUtils.class);

	/**
	 * Default timeout in milliseconds, used for connecting and again for the whole
	 * response. Override it with the {@value #DEFAULT_TIMEOUT_KEY} system property
	 * */
	public static final int DEFAULT_TIMEOUT = 30000;
	/**
	 * System property key for the default timeout
	 * */
	public static final String DEFAULT_TIMEOUT_KEY = String.format("%s.timeout", HTTPUtils.class.getName());

	private HTTPUtils() {
	}

	/**
	 * Posts a payload, usually an XML/SOAP document, to the specified URL. Redirects
	 * aren't followed: a redirect would carry the payload's headers, which can hold
	 * credentials, to another host, and turn the POST into a GET
	 * */
	public static HttpResponse postPayload(String strUrl, Payload payload, Configuration config)throws IOException, InterruptedException{
		if(logger.isDebugEnabled()){
			logger.debug(String.format("Posting %d byte payload to %s", payload.getSize(), strUrl));
		}
		HttpRequest.Builder req = request(strUrl, config)
				.POST(HttpRequest.BodyPublishers.ofByteArray(payload.getContent()));
		if(!Utils.isEmpty(payload.getContentType())){
			req.header("Content-Type", payload.getContentType());
		}
		if(payload.getHeaders()!=null){
			payload.getHeaders().forEach((name, value) -> {
				if(value!=null){
					req.header(name, value);
				}
			});
		}
		return execute(createHttpClient(config, HttpClient.Redirect.NEVER), req.build());
	}

	/**
	 * Equivalent to {@link #postPayload(String, Payload, Configuration)} with a null {@link Configuration}
	 * */
	public static HttpResponse postPayload(String strUrl, Payload payload)throws IOException, InterruptedException{
		return postPayload(strUrl, payload, null);
	}

	/**
	 * Posts the parameters to the specified URL as an
	 * {@code application/x-www-form-urlencoded} form. Redirects aren't followed, as
	 * for {@link #postPayload(String, Payload, Configuration)}
	 * */
	public static HttpResponse doPost(String strUrl, Map<String, ?> parameters, Configuration config)throws IOException, InterruptedException{
		if(logger.isDebugEnabled()){
			logger.debug(String.format("HTTP POST %s with %d parameters", strUrl, parameters==null?0:parameters.size()));
		}
		StringJoiner form = new StringJoiner("&");
		if(!Utils.isEmpty(parameters)){
			parameters.forEach((key, value) -> form.add(URLEncoder.encode(key, StandardCharsets.UTF_8) + "="
					+ URLEncoder.encode(value==null ? "" : value.toString(), StandardCharsets.UTF_8)));
		}
		HttpRequest req = request(strUrl, config)
				.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
				.POST(HttpRequest.BodyPublishers.ofString(form.toString(), StandardCharsets.UTF_8))
				.build();
		return execute(createHttpClient(config, HttpClient.Redirect.NEVER), req);
	}

	/**
	 * Posts parameters to the specified URL with the default {@link Configuration}
	 * */
	public static HttpResponse doPost(String strUrl, Map<String, ?> parameters)throws IOException, InterruptedException{
		return doPost(strUrl, parameters, null);
	}

	/**
	 * Gets the content of the specified URL
	 * */
	public static HttpResponse doGet(String strUrl, Configuration config)throws IOException, InterruptedException{
		return doGet(strUrl, null, null, config);
	}

	/**
	 * Gets the content of the specified URL with the given timeout in milliseconds
	 * */
	public static HttpResponse doGet(String strUrl, int connectionTimeout)throws IOException, InterruptedException{
		return doGet(strUrl, null, null, new Configuration(connectionTimeout));
	}

	/**
	 * Gets the content of the specified URL, using Basic authentication when a user
	 * name and password are given. Credentials are sent only to this URL: never over
	 * plain HTTP, and redirects aren't followed, so they can't be passed to another host.
	 *
	 * @throws IllegalArgumentException if credentials are given for a URL that isn't {@code https}
	 * */
	public static HttpResponse doGet(String strUrl, String userName, String password, Configuration config)throws IOException, InterruptedException{
		if(logger.isDebugEnabled()){
			logger.debug(String.format("HTTP GET %s", strUrl));
		}
		HttpRequest.Builder req = request(strUrl, config).GET();
		boolean auth = !Utils.isEmpty(userName) && !Utils.isEmpty(password);
		if(auth){
			if(!"https".equalsIgnoreCase(URI.create(strUrl).getScheme())){
				throw new IllegalArgumentException("Basic credentials would be sent in clear text to " + strUrl
						+ ": use an https URL");
			}
			String token = Base64.getEncoder().encodeToString((userName + ":" + password).getBytes(StandardCharsets.UTF_8));
			req.header("Authorization", "Basic " + token);
		}
		HttpClient client = auth ? createHttpClient(config, HttpClient.Redirect.NEVER) : createHttpClient(config);
		return execute(client, req.build());
	}

	/**
	 * Gets the content of the specified URL with the default {@link Configuration}
	 * */
	public static HttpResponse doGet(String strUrl)throws IOException, InterruptedException{
		return doGet(strUrl, null, null, null);
	}

	/**
	 * Gets the content of the specified URL, using the user name and password for
	 * Basic authentication over HTTPS
	 * */
	public static HttpResponse doGet(String strUrl, String userName, String password)throws IOException, InterruptedException{
		return doGet(strUrl, userName, password, null);
	}

	/**
	 * Creates an {@link HttpClient} with the default timeout, {@value #DEFAULT_TIMEOUT}ms
	 * unless the {@value #DEFAULT_TIMEOUT_KEY} system property overrides it
	 * */
	public static HttpClient createHttpClient(){
		return createHttpClient(new Configuration());
	}

	/**
	 * Creates an {@link HttpClient} with the specified timeout in milliseconds
	 * */
	public static HttpClient createHttpClient(int connectionTimeout){
		return createHttpClient(new Configuration(connectionTimeout));
	}

	/**
	 * Creates an {@link HttpClient} with the specified timeout and proxy settings.
	 * It follows redirects, except from HTTPS to HTTP
	 * */
	public static HttpClient createHttpClient(Configuration config){
		return createHttpClient(config, HttpClient.Redirect.NORMAL);
	}

	private static HttpClient createHttpClient(Configuration config, HttpClient.Redirect redirect){
		if(config==null){
			config = new Configuration();
		}
		HttpClient.Builder b = HttpClient.newBuilder()
				.connectTimeout(timeout(config))
				.followRedirects(redirect);

		if(!Utils.isEmpty(config.proxyHost)){
			if(config.proxyPort < 1){
				throw new IllegalArgumentException(String.format("Invalid proxy port specified: %d", config.proxyPort));
			}
			if(logger.isDebugEnabled()){
				logger.debug(String.format("Setting proxy to %s:%d", config.proxyHost, config.proxyPort));
			}
			b.proxy(ProxySelector.of(new InetSocketAddress(config.proxyHost, config.proxyPort)));

			if(!Utils.isEmpty(config.proxyUser) && !Utils.isEmpty(config.proxyPass)){
				final String user = config.proxyUser;
				final char[] pass = config.proxyPass.toCharArray();
				// Answers only the proxy's challenge, never the target server's.
				b.authenticator(new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return getRequestorType()==RequestorType.PROXY ? new PasswordAuthentication(user, pass) : null;
					}
				});
			}
		}
		return b.build();
	}

	private static HttpRequest.Builder request(String strUrl, Configuration config){
		return HttpRequest.newBuilder(URI.create(strUrl)).timeout(timeout(config));
	}

	/** The configured timeout, or the default when it's zero or less */
	private static Duration timeout(Configuration config){
		int ms = config==null ? 0 : config.connectionTimeout;
		if(ms <= 0){
			ms = Integer.getInteger(DEFAULT_TIMEOUT_KEY, DEFAULT_TIMEOUT);
		}
		return Duration.ofMillis(ms);
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
			this(content.getBytes(StandardCharsets.UTF_8), contentType, headers);
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
	 * Sends the request and wraps what comes back in a new {@link HttpResponse}
	 * */
	private static HttpResponse execute(HttpClient client, HttpRequest req)throws IOException, InterruptedException{
		java.net.http.HttpResponse<byte[]> r = client.send(req, BodyHandlers.ofByteArray());
		if(logger.isDebugEnabled()){
			logger.debug(String.format("Response code: %d", r.statusCode()));
		}
		HttpResponse res = new HttpResponse(r.statusCode(), r.body());
		for (Map.Entry<String, List<String>> h : r.headers().map().entrySet()) {
			if(!h.getValue().isEmpty()){
				res.addHeader(h.getKey(), h.getValue().get(0));
			}
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
			return body==null ? null : new String(body, StandardCharsets.UTF_8);
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
	 * Connection timeout and proxy settings for the {@link HttpClient}s this class creates
	 * */
	public static class Configuration{
		/**
		 * Timeout in milliseconds, for connecting and again for the whole response.
		 * Zero or less means {@value HTTPUtils#DEFAULT_TIMEOUT}
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
