package org.tauasa.commons.xml;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

class XmlUtilsTest {

	@Test
	void theHardenedParserFactoryCantBeReachedFromOutside() throws Exception {
		Field f = XmlUtils.class.getDeclaredField("DOCUMENT_BUILDER_FACTORY");
		assertFalse(Modifier.isPublic(f.getModifiers()));
	}

	@Test
	void createDocumentRefusesADoctype() {
		assertThrows(SAXException.class,
				() -> XmlUtils.createDocument("<!DOCTYPE a [<!ENTITY e \"x\">]><a>&e;</a>"));
	}
}
