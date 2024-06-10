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

import java.util.Date;

import org.tauasa.commons.data.DataModelException;
import org.tauasa.commons.data.IDataModel;
import org.tauasa.commons.util.DateUtils;
import org.tauasa.commons.util.Utils;


/**
 * A timestamped message used for providing feedback from an {@link ISmoketest}
 *
 * @author Tauasa Timoteo
 * 
 */
public final class SmoketestMessage {

	private static final Formatter DEFAULT_FORMATTER = new DefaultFormatter();

	private Object value;
	private Date timestamp = new Date();
	private Type type = Type.INFO;
	private Formatter formatter = DEFAULT_FORMATTER;
	private Throwable error;

	public SmoketestMessage(Object value) {
		setValue(value);
	}

	public SmoketestMessage(Object value, Type type) {
		setValue(value);
		setType(type);
	}

	public SmoketestMessage(Object value, Throwable error) {
		setValue(value);
		setError(error);
	}

	public SmoketestMessage(Object value, Type type, Throwable error) {
		this(value, type);
		setError(error);
	}

	public SmoketestMessage(Throwable error) {
		this(Type.ERROR, error);
	}

	public SmoketestMessage(Type type, Throwable error) {
		setType(type);
		setError(error);
	}

	public SmoketestMessage(Object value, Type type, Formatter formatter) {
		this(value, type);
		setFormatter(formatter);
	}

	public SmoketestMessage(Object value, Formatter formatter) {
		this(value);
		setFormatter(formatter);
	}

	public SmoketestMessage(Object value, Throwable error, Formatter formatter) {
		this(value, error);
		setError(error);
		setFormatter(formatter);
	}

	public SmoketestMessage(Object value, Type type, Throwable error, Formatter formatter) {
		this(value, error, formatter);
		setType(type);
	}

	public boolean isWarningMessage(){
		return Type.WARN.equals(type);
	}

	public boolean isErrorMessage(){
		return Type.ERROR.equals(type);
	}

	public Throwable getError() {
		return error;
	}

	public void setError(Throwable error) {
		this.error = error;
	}

	public Object getValue() {
		return value;
	}
	
	public String getStringValue() {
		return value==null ? null : value.toString();
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString(){
		return new ToStringFormatter().format(this);
	}

	/**
	 * Defines the type of message
	 * */
	public static enum Type{
		INFO("#FFFFFF", "#000000"),//white/black
		WARN("#FF7F00", "#000000"),//orange/black
		ERROR("#FF0000", "#FFFFFF");//red/white
		private String bgColor, fontColor;
		Type(String bgColor, String fontColor){
			this.bgColor=bgColor;
			this.fontColor=fontColor;
		}
		public String bgColor(){
			return bgColor;
		}
		public String fontColor(){
			return fontColor;
		}
	}

	public String getFormattedMessage(){
		if(formatter!=null){
			return formatter.format(this);
		}
		return value==null?"null":value.toString();

	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	};

	public Formatter getFormatter() {
		return formatter;
	}

	public void setFormatter(Formatter formatter) {
		this.formatter = formatter;
	}

	/**
	 * Formats a SmoketestMessage into a human-readable string value
	 * */
	public static interface Formatter{
		/**
		 * Returns the specified message as a java.lang.String. The returned
		 * value should never be null
		 * */
		public String format(SmoketestMessage msg);
	}

	/**
	 * {@link Formatter} implementation that targets an {@link IDataModel}
	 * */
	public static class DataModelFormatter implements Formatter{

		String tableTitle;
		public DataModelFormatter(){

		}

		public DataModelFormatter(String tableTitle){
			this.tableTitle=tableTitle;
		}

		/**
		 * Returns the {@link SmoketestMessage#value} attribute as an HTML table
		 * if its of type {@link IDataModel}, otherwise the value returned is
		 * {@link SmoketestMessage#value#toString()} wrapped in &lt;pre&gt;...&lt;/pre&gt; tags
		 * */
        @Override
		public String format(SmoketestMessage msg){
			if(msg.getValue()==null){
				return "";
			}
			//if not a DataModel return the stringified value
			//wrapped in <pre/> tags
			if(!(msg.getValue() instanceof IDataModel)){
				return new StringBuilder()
				.append("<pre>")
				.append(msg.getValue().toString())
				.append("</pre>").toString();
			}

			//create an HTML table from our DataModel
			IDataModel data = (IDataModel)msg.getValue();

			try{
				String[] columns = data.getColumnNames();

				StringBuilder b = new StringBuilder()
				.append("<!-- ").append(getClass().getName()).append(" -->\r\n")
				.append("<table border=\"1\" cellpadding=\"2\" cellspacing=\"0\">\r\n");

				if(tableTitle!=null){
					b.append("<tr bgcolor=\"#FFFFCA\">\r\n")
					.append("<th colspan=\"").append(columns.length).append("\">")
					.append(tableTitle).append("</th>\r\n")
					.append("</tr>\r\n");
				}

				b.append("<tr bgcolor=\"#EEEEEE\">\r\n");
				for (String column : columns) {
					b.append("<th>").append(column).append("</th>");
				}
				b.append("\r\n</tr>\r\n");

				while(data.next()){
					b.append("<tr>\r\n");

					for(int i=0;i<columns.length;i++){
						Object value = data.getObject(i);
						b.append("<td>").append(value==null?"":value.toString()).append("&nbsp;</td>");
					}

					b.append("\r\n</tr>\r\n");
				}

				b.append("</table>");

				return b.toString();
			}catch(DataModelException e){
				return Utils.getStackTrace(e);
			}
		}
	}

	/**
	 * Returns the value attribute of a SmoketestMessage wrapped in
	 * &lt;pre&gt;...&lt;/pre&gt;
	 * */
	public static class PreFormattedTextFormatter implements Formatter{
		@Override
		public String format(SmoketestMessage msg){
			if(msg.getValue()==null){
				return "";
			}
			return new StringBuilder()
			.append("<!-- ").append(getClass().getName()).append(" -->\r\n")
			.append("<pre>")
			.append(msg.getValue().toString())
			.append("</pre>").toString();
		}
	}


	public static String formatLink(String value){
		if(!value.toLowerCase().startsWith("http")){
			return value;
		}
		return new StringBuilder()
		.append("<a href=\"").append(value).append("\">")
		.append(value)
		.append("</a>").toString();
	}

	/**
	 * If the SmoketestMessage.message attribute starts with "http" the
	 * value is rendered as an HTML anchor tag
	 * (i.e. &lt;a href="<i>value</i>"&gt;<i>value</i>&lt;/a&gt;)
	 * */
	public static class DefaultFormatter implements Formatter{
		@Override
		public String format(SmoketestMessage msg){
			if(msg.getValue()==null){
				return "";
			}
			return formatLink(msg.getValue().toString());
		}
	}

	/**
	 * Treats the SmoketestMessage.message attribute as a string representation
	 * of a java.util.Properties object. The string values is tokenized into rows
	 * along a CRLF, and each row is tokenized into key/value pairs. The results
	 * are rendered/formatted into a two-column HTML table.
	 * */
	public static class HTMLPropertiesFormatter implements Formatter{
		String tableTitle;
		public HTMLPropertiesFormatter(){

		}

		public HTMLPropertiesFormatter(String tableTitle){
			this.tableTitle=tableTitle;
		}

        @Override
		public String format(SmoketestMessage msg){
			if(msg.getValue()==null){
				return "";
			}
			String[] rows = Utils.split(msg.getValue().toString(), "\r\n");

			StringBuilder b = new StringBuilder()
			.append("<!-- ").append(getClass().getName()).append(" -->\r\n")
			.append("<table border=\"1\" cellpadding=\"2\" cellspacing=\"0\">\r\n");

			if(tableTitle!=null){
				b.append("<tr bgcolor=\"#FFFFCA\">\r\n")
				.append("<th colspan=\"2\">").append(tableTitle).append("</th>\r\n")
				.append("</tr>\r\n");
			}

			b.append("<tr bgcolor=\"#EEEEEE\">\r\n")
			.append("<th>Key</th><th>Value</th>\r\n")
			.append("</tr>\r\n");

			for (String row : rows) {
				String[] cols = Utils.split(row, "=");
				b.append("<tr>\r\n")
				.append("<td>").append(cols[0]).append("</td><td>").append(cols.length > 1 ? formatLink(cols[1]) : "").append("&nbsp;</td>\r\n")
				.append("</tr>\r\n");
			}

			b.append("</table>");

			return b.toString();
		}
	}

	/**
	 * Returns the value attribute of a SmoketestMessage in &lt;pre&gt;...&lt;/pre&gt;
	 * tags and, assuming values attribute's toString() method returns XML, encodes
	 * &lt; and &gt; characters so it is readable in a browser
	 * */
	public static class PreformattedXmlFormatter implements Formatter{
		@Override
		public String format(SmoketestMessage msg){
			if(msg.getValue()==null){
				return "";
			}
			//replace < with &lt;
			return new StringBuilder()
			.append("<!-- ").append(getClass().getName()).append(" -->\r\n")
			.append("<div bgcolor=\"#FFFFCA\"><pre>")
			.append(msg.getValue().toString().replace("<", "&lt;").replace(">", "&gt;"))
			.append("</pre></div>").toString();
		}
	}

	public static class ToStringFormatter implements Formatter{
		@Override
		public String format(SmoketestMessage msg){
			return new StringBuilder()
			.append("[").append(msg.getType().name()).append("] ")
			.append(DateUtils.toMMDDYYYYHHMMSS(msg.getTimestamp()))
			//.append(msg.getKey())
			.append(": ")
			.append(msg.getValue()==null?"null":msg.getValue().toString()).toString();
		}
	}



}
