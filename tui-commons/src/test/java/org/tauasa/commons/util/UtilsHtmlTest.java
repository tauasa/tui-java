package org.tauasa.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class UtilsHtmlTest {

	@Test
	void escapesMarkupAndBothQuotes() {
		assertEquals("&lt;a href=&quot;x&quot; title=&#39;y&#39;&gt;&amp;&lt;/a&gt;",
				Utils.escapeHtml("<a href=\"x\" title='y'>&</a>"));
	}

	@Test
	void writesNonAsciiAsNumericReferences() {
		assertEquals("caf&#233; &#128512;", Utils.escapeHtml("café 😀"));
	}

	@Test
	void keepsWhitespaceAndEscapesOtherControlCharacters() {
		assertEquals("a\tb\r\nc&#0;", Utils.escapeHtml("a\tb\r\nc\u0000"));
	}

	@Test
	void nullStaysNull() {
		assertNull(Utils.escapeHtml(null));
	}

	@Test
	void toStringOfNamesEachValue() {
		Object o = new Object();
		String s = Utils.toStringOf(o, "a", 1, "b", null);
		assertTrue(s.startsWith("java.lang.Object@"), s);
		assertTrue(s.endsWith("[a=1,b=<null>]"), s);
	}

	@Test
	void toStringOfRefusesAnOddList() {
		assertThrows(IllegalArgumentException.class, () -> Utils.toStringOf(new Object(), "a"));
	}
}
