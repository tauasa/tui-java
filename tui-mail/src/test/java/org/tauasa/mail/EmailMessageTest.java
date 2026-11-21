package org.tauasa.mail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;

import org.junit.jupiter.api.Test;

class EmailMessageTest {

	private static final Session SESSION = Session.getInstance(new Properties());

	@Test
	void buildsAMessageWithItsSubjectAndHeaders() throws Exception {
		EmailMessage m = new EmailMessage("to@example.com", "from@example.com", "Hello", "body");
		m.addHeader("X-Test", "one");
		Message msg = m.toMessage(SESSION);
		assertEquals("Hello", msg.getSubject());
		assertEquals("one", msg.getHeader("X-Test")[0]);
		assertNull(msg.getHeader("Bcc"));
	}

	@Test
	void refusesALineBreakInTheSubject() throws Exception {
		EmailMessage m = new EmailMessage("to@example.com", "from@example.com", "Hi\r\nBcc: victim@example.com", "body");
		assertThrows(MessagingException.class, () -> m.toMessage(SESSION));
		m.setSubject("Hi\nBcc: victim@example.com");
		assertThrows(MessagingException.class, () -> m.toMessage(SESSION));
	}

	@Test
	void refusesALineBreakInARecipientNameOrTheFromAddress() throws Exception {
		assertThrows(javax.mail.internet.AddressException.class,
				() -> new Recipient("to@example.com", "Bob\r\nBcc: victim@example.com"));
		assertThrows(javax.mail.internet.AddressException.class,
				() -> new Recipient("to@example.com", "Bob\nBcc: victim@example.com", Message.RecipientType.CC));
		EmailMessage m = new EmailMessage("to@example.com", "from@example.com", "Hi", "body");
		m.setFrom("from@example.com\r\nBcc: victim@example.com");
		assertThrows(MessagingException.class, () -> m.toMessage(SESSION));
	}

	@Test
	void messagesFromATemplateDontShareRecipients() throws Exception {
		EmailMessage template = new EmailMessage("first@example.com", "from@example.com", "Hi", "body");
		template.addHeader("X-Test", "one");
		EmailMessage toB = new EmailMessage(template, "b@example.com");
		EmailMessage toC = new EmailMessage(template, "c@example.com");
		toB.addHeader("X-Only-B", "yes");

		assertEquals(1, template.getRecipients().size());
		assertEquals(2, toB.getRecipients().size());
		assertEquals(2, toC.getRecipients().size());
		assertEquals("c@example.com", toC.getRecipients().get(1).getAddress().toString());
		assertNull(template.getHeaders().get("X-Only-B"));
		assertNull(toC.getHeaders().get("X-Only-B"));
		assertEquals("one", toC.getHeaders().get("X-Test"));
	}

	@Test
	void refusesALineBreakInAHeaderNameOrValue() throws Exception {
		EmailMessage value = new EmailMessage("to@example.com", "from@example.com", "Hi", "body");
		value.addHeader("X-Test", "a\r\nBcc: victim@example.com");
		assertThrows(MessagingException.class, () -> value.toMessage(SESSION));

		EmailMessage name = new EmailMessage("to@example.com", "from@example.com", "Hi", "body");
		name.addHeader("X-Test\r\nBcc", "victim@example.com");
		assertThrows(MessagingException.class, () -> name.toMessage(SESSION));
	}
}
