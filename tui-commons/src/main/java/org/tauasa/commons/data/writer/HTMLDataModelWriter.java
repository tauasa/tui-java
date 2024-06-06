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
package org.tauasa.commons.data.writer;

import org.tauasa.commons.data.DataModelException;
import org.tauasa.commons.data.IDataModel;
import org.tauasa.commons.util.Utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Simple {@link IDataModelWriter} implementation that writes an {@link IDataModel} as an HTML table
 *
 * @author Tauasa Timoteo
 * 
 */
public class HTMLDataModelWriter extends AbstractDataModelWriter {

	static final byte[] NEWLINE = "\r\n".getBytes();

	protected String title, header, subHeader;

	public HTMLDataModelWriter(OutputStream out) {
		super(out);
	}

	public HTMLDataModelWriter(OutputStream out, String title) {
		this(out);
		this.title=title;
	}

	final void writeln(Object obj)throws IOException{
		Utils.writeln(out, obj==null ? "&nbsp;" : obj);
	}

	final void write(Object obj)throws IOException{
		Utils.write(out, obj==null ? "&nbsp;" : obj);
	}

	static String createStyleSheet(){
		StringBuffer buffer = new StringBuffer();

		buffer.append("<style>\r\n");

		buffer.append("body.default{background-color: #FFFFFF; font-weight: normal; font-size: 11px; font-family: Arial, Tahoma, Verdana;}\r\n");

		buffer.append("table.default{border: 1px; border-style: solid; border-color: #000000}\r\n");

		buffer.append("tr.header{border: 1px; border-style: solid; background-color: #CCCCCC; border-color: #000000}\r\n");

		buffer.append("tr.default{border: 1px; border-style: solid; background-color: #FFFFFF; border-color: #000000}\r\n");

		buffer.append("tr.alternate{border: 1px; border-style: solid; background-color: #FFFFCC; border-color: #000000}\r\n");

		buffer.append("th.default{border: 1px; border-style: solid; border-color: #000000; text-align: center; font-weight: bold; font-size: 11px; font-family: Arial, Tahoma, Verdana;}\r\n");

		buffer.append("td.default{border: 1px; border-style: solid; border-color: #000000; text-align: left; font-weight: normal; font-size: 11px; font-family: Arial, Tahoma, Verdana;}\r\n");

		buffer.append("</style>\r\n");

		return buffer.toString();
	}

	public void write(IDataModel data) throws DataModelException, IOException {
		writeln("<html>");
		writeln("<head>");
		write("<title>");
		write(title);
		writeln("</title>");

		writeln(createStyleSheet());

		writeln("</head>");

		writeln("<body class=\"default\">");

		if(header==null){
			header = title;
		}

		write("<h1 class=\"default\">");
		write(header);
		writeln("</h1>");

		if(subHeader!=null){
			write(subHeader);
			writeln("<P>");
		}

		write("<b>");
		write(String.valueOf(data.size()));
		write(" Records");
		writeln("</b><p/>");

		writeln("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" class=\"default\">");

		//write column headers
		writeln("<tr class=\"header\">");
		String[] columns = data.getColumnNames();
		for(int i=0;i<columns.length;i++){
			write("<th class=\"default\">");
			write(columns[i]);
			writeln("</th>");
		}
		writeln("</tr>");

		//write data
		int x=0;
		while(data.next()){
			write("<tr class=\"");
			write(x%2==0?"default":"alternate");
			writeln("\">");

			for(int i=0;i<columns.length;i++){
				write("<td class=\"default\">");
				write(data.getObject(i));
				writeln("</td>");
			}

			writeln("</tr>");
			x++;
		}

		writeln("</table>");

		writeln("</body>");
		writeln("</html>");


	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getSubHeader() {
		return subHeader;
	}

	public void setSubHeader(String subHeader) {
		this.subHeader = subHeader;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
