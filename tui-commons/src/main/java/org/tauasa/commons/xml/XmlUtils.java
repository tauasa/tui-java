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
package org.tauasa.commons.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.tauasa.commons.util.DateUtils;
import org.tauasa.commons.util.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Static XML utility methods
 *
 * @author Tauasa Timoteo
 * 
 */
public class XmlUtils extends Utils {

	public static final String XML_VERSION = "1.0";
	public static final String ENCODING = "UTF-8";
	public static final String DEFAULT_DATE_FORMAT_ATTRIBUTE_NAME = "format";
	public static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
	public static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();


	/**
	 * Adds the specified child Document to the specified target Node that is a child of parent
	 * */
	public static final void appendDocument(Document parent, Node targetNode, Document child){
		DocumentFragment docFrag = child.createDocumentFragment();
		Element rootElement = child.getDocumentElement();
		docFrag.appendChild(rootElement);
		Node replacingNode = parent.importNode(docFrag, true);
		targetNode.appendChild(replacingNode);
	}

	/**
	 * Creates a new XML document
	 * */
	public static final Document createDocument()throws ParserConfigurationException{
		return DOCUMENT_BUILDER_FACTORY.newDocumentBuilder().newDocument();
	}

	/**
	 * Creates an XML document from the specified input stream
	 * */
	public static final Document createDocument(InputStream in)throws ParserConfigurationException, IOException, SAXException{
		return DOCUMENT_BUILDER_FACTORY.newDocumentBuilder().parse(in);
	}

	/**
	 * Creates an XML document from the specified input source
	 * */
	public static final Document createDocument(InputSource in)throws ParserConfigurationException, IOException, SAXException{
		return DOCUMENT_BUILDER_FACTORY.newDocumentBuilder().parse(in);
	}

	public static final Document createDocument(CharSequence xml)throws ParserConfigurationException, IOException, SAXException{
		return createDocument(xml.toString());
	}

	/**
	 * Creates an XML document from the specified string
	 * */
	public static final Document createDocument(String xml)throws ParserConfigurationException, IOException, SAXException{
		return createDocument(new InputSource(new StringReader(xml)));
	}

	/**
	 * Creates an XML document from the specified byte array
	 * */
	public static final Document createDocument(byte[] xml)throws ParserConfigurationException, IOException, SAXException{
		return createDocument(new ByteArrayInputStream(xml));
	}

	/**
	 * Converts the specified Node to a Document
	 * */
	public static final Document nodeToDocument(Node node)throws Exception{
		Transformer xf = TRANSFORMER_FACTORY.newTransformer();
		DOMResult dr = new DOMResult();
		xf.transform(new DOMSource(node), dr);
		return (Document)dr.getNode();
	}

	/**
	 * Returns the specified XML string in a pretty, readable format
	 * */
	public static final String formatXml(String xml)throws Exception{
		return toXmlString(createDocument(xml));
	}

	/**
	 * Converts the specified Document into an XML string
	 * */
	public static final String toXmlString(Document doc)throws Exception{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		writeXml(doc, out);
		return new String(out.toByteArray());
	}

	/**
	 * Converts the specified Document into a byte array
	 * */
	public static final byte[] toByteArray(Document doc)throws Exception{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		writeXml(doc, out);
		return out.toByteArray();
	}

	/**
	 * Converts the specified Node to an XML string
	 * */
	public static final String toXmlString(Node node)throws Exception{
		return toXmlString(nodeToDocument(node));
	}

	public static final void writeXml(Document doc, OutputStream out)throws Exception{
		writeXml(doc, new OutputStreamWriter(out), true);
		/*Transformer xformer = TRANSFORMER_FACTORY.newTransformer();
		xformer.transform(
				new DOMSource(doc),
				new StreamResult(out)
		);*/
	}

	public static final void writeXml(Node document, Writer writer, boolean omitXmlDeclaration, boolean indent)throws Exception{
		Transformer xform = TRANSFORMER_FACTORY.newTransformer();
		xform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, omitXmlDeclaration?"yes":"no");
		xform.setOutputProperty(OutputKeys.INDENT, indent?"yes":"no");
		xform.transform(
				new DOMSource(document),
				new StreamResult(writer)
				);
	}

	public static final void writeXml(Node document, Writer writer, boolean omitXmlDeclaration)throws Exception{
		writeXml(document, writer, omitXmlDeclaration, false);
		/*
		OutputFormat of = new OutputFormat(document, ENCODING, true);
		of.setOmitXMLDeclaration(omitXmlDeclaration);
		of.setOmitComments(omitComments);
		if(lineWidth > 0){
			of.setLineWidth(lineWidth);
		}
		of.setPreserveSpace(preserveSpace);
		XMLSerializer serial = new XMLSerializer(writer, of);
		serial.asDOMSerializer();
		serial.serialize(document.getDocumentElement());
		//*/
	}

	public static final String toXmlString(Node document, boolean omitXmlDeclaration)throws Exception{
		StringWriter writer = new StringWriter();
		writeXml(document, writer, omitXmlDeclaration);
		return writer.toString();
	}

	/**
	 * Returns the first child node found in the specified parent with the specified
	 * name or null if there is none.  If the parent name matches the specified name
	 * then the parent node is returned.
	 * */
	public static final Node findNode(Node parent, String name){
		return findNode(parent, null, name);
	}

	public static final Node findNode(Node parent, String namespace, String name){
		return findNode(parent, namespace, name, false);
	}

	public static final Node findNode(Node parent, String namespace, String name, boolean ignoreParent){
		name = name(namespace, name);
		if(!ignoreParent && parent.getNodeName().equals(name)){
			return parent;
		}

		if(parent.hasChildNodes()){
			NodeList children = parent.getChildNodes();
			for(int i=0;i<children.getLength();i++){
				Node node = findNode(children.item(i), name);
				if(node!=null){
					return node;
				}
			}
		}

		return null;
	}

	private static String name(String namespace, String name){
		return namespace==null ? name : namespace.concat(":").concat(name);
	}

	/**
	 * Returns the value of the first child node found in the specified parent with
	 * the specified name or null if there is none.  If the parent matches the specified
	 * name then the value of the parent node is returned.
	 * */
	public static String findNodeValue(Node parent, String name){
		return findNodeValue(parent, null, name);
	}

	public static final String findNodeValue(Node parent, String namespace, String name){
		name = name(namespace, name);
		Node node = findNode(parent, name);
		if(node==null){
			return null;
		}
		return getNodeValue(node);
	}

	public static final int findNodeValueInt(Node parent, String name){
		return findNodeValueInt(parent, null, name);
	}

	public static final int findNodeValueInt(Node parent, String namespace, String name){
		String value = findNodeValue(parent, namespace, name);
		if(value==null){
			return 0;
		}
		return Integer.parseInt(value);
	}

	public static final long findNodeValueLong(Node parent, String name){
		return findNodeValueLong(parent, null, name);
	}

	public static final long findNodeValueLong(Node parent, String namespace, String name){
		String value = findNodeValue(parent, namespace, name);
		if(value==null){
			return 0;
		}
		return Long.parseLong(value);
	}

	public static final float findNodeValueFloat(Node parent, String name){
		return findNodeValueFloat(parent, null, name);
	}


	public static final float findNodeValueFloat(Node parent, String namespace, String name){
		String value = findNodeValue(parent, namespace, name);
		if(value==null){
			return 0;
		}
		return Float.parseFloat(value);
	}

	public static final double findNodeValueDouble(Node parent, String name){
		return findNodeValueDouble(parent, null, name);
	}

	public static final double findNodeValueDouble(Node parent, String namespace, String name){
		String value = findNodeValue(parent, namespace, name);
		if(value==null){
			return 0;
		}
		return Double.parseDouble(value);
	}

	public static final boolean findNodeValueBoolean(Node parent, String name){
		return findNodeValueBoolean(parent, null, name);
	}

	public static final boolean findNodeValueBoolean(Node parent, String namespace, String name){
		String value = findNodeValue(parent, namespace, name);
		if(value==null){
			return false;
		}
		return Boolean.parseBoolean(value);
	}

	public static final Date findDateNode(Node parent, String name)throws Exception{
		return findDateNode(parent, name, DateUtils.MMDDYYYY);
	}

	public static final Date findDateNode(Node parent, String name, String format)throws Exception{
		Node dateNode = findNode(parent, name);
		if(dateNode==null){
			return null;
		}
		String dateString = getNodeValue(dateNode);
		if(format==null){
			format = DateUtils.MMDDYYYY;
		}
		return DateUtils.parseDate(dateString, format);
	}

	/**
	 * Returns the specified {@link Node}'s first child {@link Node} of type {@link Node#ELEMENT_NODE}
	 * or null if none is found
	 * */
	public static final Node findRootNode(Node node){
		NodeList children = node.getChildNodes();
		for(int i=0;i<children.getLength();i++){
			Node child = children.item(i);
			if(child.getNodeType()==Node.ELEMENT_NODE){
				return child;
			}
		}
		return null;
	}

	public static final Node findRootNode(InputStream in){
		try{
			return findRootNode(createDocument(in));
		}catch(IOException | ParserConfigurationException | SAXException e){
			throw new RuntimeException(e);
		}
	}

	public static final Node findRootNode(InputSource in){
		try{
			return findRootNode(createDocument(in));
		}catch(IOException | ParserConfigurationException | SAXException e){
			throw new RuntimeException(e);
		}
	}

	public static final Node findRootNode(CharSequence xml){
		try{
			return findRootNode(createDocument(xml));
		}catch(IOException | ParserConfigurationException | SAXException e){
			throw new RuntimeException(e);
		}
	}

	public static final Node findRootNode(String xml){
		try{
			return findRootNode(createDocument(xml));
		}catch(IOException | ParserConfigurationException | SAXException e){
			throw new RuntimeException(e);
		}
	}

	public static final Node findRootNode(byte[] xml){
		try{
			return findRootNode(createDocument(xml));
		}catch(IOException | ParserConfigurationException | SAXException e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the attribute node with the specified name or null if there is none
	 * */
	public static final Node getAttributeNode(Node parent, String attributeName){
		return getAttributeNode(parent, null, attributeName);
	}

	public static final Node getAttributeNode(Node parent, String namespace, String attributeName){
		if(parent==null || attributeName==null){
			return null;
		}
		NamedNodeMap map = parent.getAttributes();
		if(map==null){
			return null;
		}
		return map.getNamedItem(name(namespace, attributeName));

	}

	/**
	 * Returns the value of the specified attribute from the specified node
	 * or null if there is none.
	 * */
	public static final String getAttributeValue(Node parent, String attributeName){
		return getAttributeValue(parent, null, attributeName);
	}

	public static final String getAttributeValue(Node parent, String namespace, String attributeName){

		Node attributeNode = getAttributeNode(parent, namespace, attributeName);

		return attributeNode!=null ? attributeNode.getNodeValue() : null;

	}

	/**
	 * Returns the contents of the specified node as a string value
	 * */
	public static final String getNodeValue(Node node){
		if(node==null){
			return null;
		}
		StringBuilder buffer = new StringBuilder();

		NodeList children = node.getChildNodes();

		for(int i=0;i<children.getLength();i++){
			Node child = children.item(i);
			String value = child.getNodeValue();
			if(value==null){
				continue;
			}
			buffer.append(value);
		}

		return buffer.toString();
	}


	public static final void writeDocStart(OutputStream out)throws IOException{
		writeDocStart(XML_VERSION, ENCODING, out);
	}

	public static final void writeDocStart(String version, String encoding, OutputStream out)throws IOException{
		write(out, "<?xml version=\"");
		write(out, version);
		write(out, "\" encoding=\"");
		write(out, encoding);
		write(out, "\"?>\r\n");
	}

	public static final void writeNode(String nodeName, String nodeValue, OutputStream out, boolean cdata)throws IOException{
		writeNode(null, nodeName, nodeValue, out, cdata);
	}

	public static final void writeNode(String namespace, String nodeName, String nodeValue, OutputStream out, boolean cdata)throws IOException{
		writeNode(name(namespace, nodeName), nodeValue, null, null, out, cdata);
	}

	public static final void writeNode(String nodeName, String nodeValue, OutputStream out)throws IOException{
		writeNode(null, nodeName, nodeValue, out);
	}

	public static final void writeNode(String namespace, String nodeName, String nodeValue, OutputStream out)throws IOException{
		writeNode(namespace, nodeName, nodeValue, out, false);
	}

	public static final void writeNode(String nodeName, int nodeValue, OutputStream out)throws IOException{
		writeNode(null, nodeName, nodeValue, out);
	}

	public static final void writeNode(String namespace, String nodeName, int nodeValue, OutputStream out)throws IOException{
		writeNode(namespace, nodeName, String.valueOf(nodeValue), out);
	}

	public static final void writeNode(String nodeName, float nodeValue, OutputStream out)throws IOException{
		writeNode(null, nodeName, nodeValue, out);
	}

	public static final void writeNode(String namespace, String nodeName, float nodeValue, OutputStream out)throws IOException{
		writeNode(namespace, nodeName, String.valueOf(nodeValue), out);
	}

	public static final void writeNode(String nodeName, double nodeValue, OutputStream out)throws IOException{
		writeNode(null, nodeName, nodeValue, out);
	}

	public static final void writeNode(String namespace, String nodeName, double nodeValue, OutputStream out)throws IOException{
		writeNode(namespace, nodeName, String.valueOf(nodeValue), out);
	}

	public static final void writeNode(String nodeName, long nodeValue, OutputStream out)throws IOException{
		writeNode(null, nodeName, nodeValue, out);
	}

	public static final void writeNode(String namespace, String nodeName, long nodeValue, OutputStream out)throws IOException{
		writeNode(namespace, nodeName, String.valueOf(nodeValue), out);
	}

	public static final void writeNode(String nodeName, boolean nodeValue, OutputStream out)throws IOException{
		writeNode(null, nodeName, nodeValue, out);
	}

	public static final void writeNode(String namespace, String nodeName, boolean nodeValue, OutputStream out)throws IOException{
		writeNode(namespace, nodeName, String.valueOf(nodeValue), out);
	}

	public static final void writeNodeStart(String nodeName, OutputStream out)throws IOException{
		writeNodeStart(null, nodeName, out);
	}

	public static final void writeNodeStart(String namespace, String nodeName, OutputStream out)throws IOException{
		write(out, "<");
		write(out, name(namespace, nodeName));
		write(out, ">\r\n");
	}

	public static final void writeNode(String nodeName, String nodeValue, String[] attributeNames, String[] attributeValues, OutputStream out)throws IOException{
		writeNode(null, nodeName, nodeValue, attributeNames, attributeValues, out);
	}

	public static final void writeNode(String namespace, String nodeName, String nodeValue, String[] attributeNames, String[] attributeValues, OutputStream out)throws IOException{

		writeNodeStart(namespace, nodeName, attributeNames, attributeValues, out);

		if(nodeValue!=null){
			write(out, nodeValue);
		}

		writeNodeEnd(namespace, nodeName, out);

	}

	public static final void writeNodeStart(String nodeName, String[] attributeNames, String[] attributeValues, OutputStream out)throws IOException{
		writeNodeStart(null, nodeName, attributeNames, attributeValues, out);

	}

	public static final void writeNodeStart(String namespace, String nodeName, String[] attributeNames, String[] attributeValues, OutputStream out)throws IOException{

		write(out, "<");
		write(out, name(namespace, nodeName));

		if(!isEmpty(attributeNames)){

			if(attributeNames.length != attributeValues.length){
				throw new IOException("Mismatched number of attribute names and values ("+attributeNames.length+" != "+attributeValues.length+")");
			}

			for(int i=0;i<attributeNames.length;i++){
				String name = attributeNames[i];
				String value = attributeValues[i];
				if(name==null){
					throw new NullPointerException("attributeNames["+i+"] is null");
				}

				write(out, " ");
				write(out, name);
				write(out, "=\"");
				if(value!=null){
					write(out, value);
				}
				write(out, "\"");
			}

		}

		write(out, ">");

	}

	public static final void writeNodeStart(String nodeName, String attributeName, String attributeValue, OutputStream out)throws IOException{
		writeNodeStart(null, nodeName, attributeName, attributeValue, out);
	}

	public static final void writeNodeStart(String namespace, String nodeName, String attributeName, String attributeValue, OutputStream out)throws IOException{

		writeNodeStart(namespace, nodeName, new String[]{attributeName}, new String[]{attributeValue}, out);

		/*
		write("<", out);
		write(nodeName, out);
		if(attributeName!=null){
			write(" ", out);
			write(attributeName, out);
			write("=\"", out);
			if(attributeValue!=null){
				write(attributeValue, out);
			}
			write("\"", out);
		}
		write(">\r\n", out);//*/
	}

	public static final void writeNodeEnd(String nodeName, OutputStream out)throws IOException{
		writeNodeEnd(null, nodeName, out);
	}

	public static final void writeNodeEnd(String namespace, String nodeName, OutputStream out)throws IOException{
		write(out, "</");
		write(out, name(namespace, nodeName));
		write(out, ">\r\n");
	}

	public static final void writeCDATA(String value, OutputStream out)throws IOException{
		write(out, "<![CDATA[");
		write(out, value);
		write(out, "]]>");
	}

	public static final void writeComment(String comment, OutputStream out)throws IOException{
		write(out, "<!--\r\n");
		write(out, comment);
		write(out, "\r\n-->\r\n");
	}

	public static final void writeDateNode(String nodeName, Date date, String format, OutputStream out)throws IOException, Exception{
		writeDateNode(nodeName, date, null, format, out, false);
	}

	public static final void writeDateNode(String nodeName, Date date, OutputStream out)throws IOException, Exception{
		writeDateNode(nodeName, date, out, false);
	}

	public static final void writeDateNode(String nodeName, Date date, OutputStream out, boolean cdata)throws IOException, Exception{
		writeDateNode(nodeName, date, null, null, out, cdata);
	}

	public static final void writeDateNode(String nodeName, Date date, String formatName, String formatValue, OutputStream out, boolean cdata)throws IOException, Exception{
		if(formatValue==null){
			formatValue = DateUtils.MMDDYYYY;
		}
		if(formatName==null){
			formatName = DEFAULT_DATE_FORMAT_ATTRIBUTE_NAME;
		}
		writeNode(nodeName, date!=null ? DateUtils.formatDate(date, formatValue) : null, formatName, formatValue, out, cdata);
	}
	public static final void writeDateNode(String nodeName, Date date, String formatName, String formatValue, OutputStream out)throws IOException, Exception{
		writeDateNode(nodeName, date, formatName, formatValue, out, false);
	}

	public static final void writeNode(String nodeName, String nodeValue, String attributeName, String attributeValue, OutputStream out, boolean cdata)throws IOException{
		writeNode(null, nodeName, nodeValue, attributeName, attributeValue, out, cdata);

	}

	public static final void writeNode(String namespace, String nodeName, String nodeValue, String attributeName, String attributeValue, OutputStream out, boolean cdata)throws IOException{
		if(nodeName==null){
			return;
		}
		write(out, "<");
		write(out, name(namespace, nodeName));

		if(attributeName!=null){
			write(out, " ");
			write(out, attributeName);
			write(out, "=\"");

			if(attributeValue!=null){
				write(out, attributeValue);
			}
			write(out, "\"");
		}

		if(nodeValue==null){
			write(out, "/>\r\n");
			return;
		}

		write(out, ">");

		if(cdata){
			writeCDATA(nodeValue, out);
		}else{
			write(out, nodeValue);
		}

		write(out, "</");
		write(out, name(namespace, nodeName));
		write(out, ">\r\n");
	}

}
