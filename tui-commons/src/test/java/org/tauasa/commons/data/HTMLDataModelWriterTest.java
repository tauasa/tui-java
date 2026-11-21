package org.tauasa.commons.data;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.tauasa.commons.data.writer.HTMLDataModelWriter;

class HTMLDataModelWriterTest {

	@Test
	void escapesColumnNamesValuesAndTitle() throws Exception {
		TableDataModel data = new TableDataModel(new String[] {"<b>name</b>"},
				new Object[][] {{"<script>alert(1)</script>"}, {null}});
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new HTMLDataModelWriter(out, "<i>title</i>").write(data);
		String html = out.toString(StandardCharsets.UTF_8);

		assertFalse(html.contains("<script>"), html);
		assertFalse(html.contains("<b>name</b>"), html);
		assertFalse(html.contains("<i>title</i>"), html);
		assertTrue(html.contains("&lt;script&gt;alert(1)&lt;/script&gt;"), html);
		assertTrue(html.contains("&lt;b&gt;name&lt;/b&gt;"), html);
	}
}
