package org.tauasa.web.smoke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.tauasa.commons.data.TableDataModel;

class SmoketestMessageTest {

	private static final String XSS = "<script>alert(1)</script>";

	@Test
	void defaultFormatterEscapesText() {
		String html = new SmoketestMessage.DefaultFormatter().format(new SmoketestMessage(XSS));
		assertEquals("&lt;script&gt;alert(1)&lt;/script&gt;", html);
	}

	@Test
	void onlyHttpUrlsBecomeLinksAndTheyAreEscaped() {
		assertEquals("<a href=\"https://example.com/?a=1&amp;b=2\">https://example.com/?a=1&amp;b=2</a>",
				SmoketestMessage.formatLink("https://example.com/?a=1&b=2"));
		assertFalse(SmoketestMessage.formatLink("http\"><script>x</script>").contains("<script>"));
		assertFalse(SmoketestMessage.formatLink("javascript:alert(1)").contains("<a "));
	}

	@Test
	void preformattedTextIsEscaped() {
		String html = new SmoketestMessage.PreFormattedTextFormatter().format(new SmoketestMessage(XSS));
		assertFalse(html.contains("<script>"), html);
	}

	@Test
	void dataModelColumnsAndValuesAreEscaped() {
		TableDataModel data = new TableDataModel(new String[] {"<b>col</b>"}, new Object[][] {{XSS}});
		String html = new SmoketestMessage.DataModelFormatter("<i>t</i>").format(new SmoketestMessage(data));
		assertFalse(html.contains("<script>"), html);
		assertFalse(html.contains("<b>col</b>"), html);
		assertFalse(html.contains("<i>t</i>"), html);
		assertTrue(html.contains("&lt;script&gt;"), html);
	}

	@Test
	void dataModelFormatterEscapesAPlainValue() {
		String html = new SmoketestMessage.DataModelFormatter().format(new SmoketestMessage(XSS));
		assertFalse(html.contains("<script>"), html);
	}

	@Test
	void propertiesKeysAndValuesAreEscaped() {
		String html = new SmoketestMessage.HTMLPropertiesFormatter().format(
				new SmoketestMessage("<b>key</b>=" + XSS));
		assertFalse(html.contains("<script>"), html);
		assertFalse(html.contains("<b>key</b>"), html);
	}
}
