/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.xml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Static XPath utility methods
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public class XPathUtils extends XmlUtils {

	public static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();

	/**
	 * Create a new XPath object
	 * */
	public static XPath newXPath(){
		return XPATH_FACTORY.newXPath();
	}

	/**
	 * Compile the specified expression into an XPathExpression
	 * */
	public static XPathExpression compile(String expression)throws XPathExpressionException{
		return newXPath().compile(expression);
	}

	/**
	 * Shortcut method for creating and evaluating an XPath expression against the specified Node
	 * */
	public static String evaluate(Node node, String expression)throws XPathExpressionException{
		return compile(expression).evaluate(node);
	}

	/**
	 * Shortcut method for creating and evaluating an XPath expression against the specified InputSource
	 * */
	public static String evaluate(InputSource in, String expression)throws XPathExpressionException{
		return compile(expression).evaluate(in);
	}

	/**
	 * Shortcut method for creating and evaluating an XPath expression against the specified InputStream
	 * */
	public static String evaluate(InputStream in, String expression)throws XPathExpressionException{
		return compile(expression).evaluate(in);
	}

	/**
	 * Shortcut method for creating and evaluating an XPath expression against the specified XML string
	 * */
	public static String evaluate(String xml, String expression)throws XPathExpressionException, ParserConfigurationException,
		IOException, SAXException{
		return compile(expression).evaluate(createDocument(xml));
	}
	
}
