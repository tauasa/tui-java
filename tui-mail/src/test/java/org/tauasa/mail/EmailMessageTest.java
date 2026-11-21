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
	void refusesALineBreakInAHeaderNameOrValue() throws Exception {
		EmailMessage value = new EmailMessage("to@example.com", "from@example.com", "Hi", "body");
		value.addHeader("X-Test", "a\r\nBcc: victim@example.com");
		assertThrows(MessagingException.class, () -> value.toMessage(SESSION));

		EmailMessage name = new EmailMessage("to@example.com", "from@example.com", "Hi", "body");
		name.addHeader("X-Test\r\nBcc", "victim@example.com");
		assertThrows(MessagingException.class, () -> name.toMessage(SESSION));
	}
}
