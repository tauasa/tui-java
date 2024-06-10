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
 * @author Tauasa Timoteo
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
