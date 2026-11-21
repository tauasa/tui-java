package org.tauasa.commons.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class XPathUtilsTest {

	@Test
	void evaluatesPlainXmlFromEveryInput() throws Exception {
		String xml = "<a><b>hello</b></a>";
		assertEquals("hello", XPathUtils.evaluate(xml, "/a/b"));
		assertEquals("hello", XPathUtils.evaluate(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), "/a/b"));
		assertEquals("hello", XPathUtils.evaluate(new InputSource(new StringReader(xml)), "/a/b"));
	}

	@Test
	void refusesExternalEntitiesFromAStreamOrSource(@TempDir Path dir) throws Exception {
		Path secret = dir.resolve("secret.txt");
		Files.writeString(secret, "TOP-SECRET");
		String xml = "<!DOCTYPE a [<!ENTITY e SYSTEM \"" + secret.toUri() + "\">]><a><b>&e;</b></a>";

		assertThrows(SAXException.class, () -> XPathUtils.evaluate(
				new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), "/a/b"));
		assertThrows(SAXException.class, () -> XPathUtils.evaluate(new InputSource(new StringReader(xml)), "/a/b"));
		assertThrows(SAXException.class, () -> XPathUtils.evaluate(xml, "/a/b"));
	}
}
