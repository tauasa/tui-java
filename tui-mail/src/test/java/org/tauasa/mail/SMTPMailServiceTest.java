package org.tauasa.mail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import javax.mail.MessagingException;
import javax.mail.Session;

import org.junit.jupiter.api.Test;

class SMTPMailServiceTest {

	private static final String HOST = SMTPMailService.Property.HOST.key();

	@Test
	void hasNoDefaultServer() {
		assumeTrue(System.getProperty(HOST) == null, HOST + " is set in this JVM");
		assertThrows(MessagingException.class, () -> new SMTPMailService().createSession());
		assertNull(SMTPMailService.Property.HOST.def());
		assertNull(SMTPMailService.Property.FROM.def());
		assertNull(SMTPMailService.Property.USER.def());
	}

	@Test
	void usesItsOwnHostWithoutChangingSystemProperties() throws Exception {
		assumeTrue(System.getProperty(HOST) == null, HOST + " is set in this JVM");
		Session s = new SMTPMailService("smtp.example.com").createSession();
		assertEquals("smtp.example.com", s.getProperty(HOST));
		assertNull(System.getProperty(HOST));
	}

	@Test
	void twoServicesOnOneThreadKeepTheirOwnSessions() throws Exception {
		SMTPMailService a = new SMTPMailService("a.example.com");
		SMTPMailService b = new SMTPMailService("b.example.com");
		try {
			Session sa = a.getLocalSession();
			Session sb = b.getLocalSession();
			assertNotSame(sa, sb);
			assertEquals("a.example.com", sa.getProperty(HOST));
			assertEquals("b.example.com", sb.getProperty(HOST));
		} finally {
			a.close();
			b.close();
		}
	}
}
