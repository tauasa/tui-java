package org.tauasa.commons.net;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

/** Runs against a real local server, so it tests the HTTP the class actually sends. */
class HTTPUtilsTest {

	private HttpServer server;
	private String base;
	private final AtomicInteger hits = new AtomicInteger();
	private final AtomicReference<String> lastBody = new AtomicReference<>();
	private final AtomicReference<String> lastAuth = new AtomicReference<>();
	private final AtomicReference<String> lastType = new AtomicReference<>();

	@BeforeEach
	void start() throws IOException {
		server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
		server.createContext("/echo", ex -> {
			hits.incrementAndGet();
			lastBody.set(new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
			lastAuth.set(ex.getRequestHeaders().getFirst("Authorization"));
			lastType.set(ex.getRequestHeaders().getFirst("Content-Type"));
			byte[] out = "pong".getBytes(StandardCharsets.UTF_8);
			ex.getResponseHeaders().add("X-Test", "yes");
			ex.sendResponseHeaders(200, out.length);
			try (OutputStream os = ex.getResponseBody()) {
				os.write(out);
			}
		});
		server.createContext("/slow", ex -> {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			ex.sendResponseHeaders(200, -1);
			ex.close();
		});
		server.start();
		base = "http://127.0.0.1:" + server.getAddress().getPort();
	}

	@AfterEach
	void stop() {
		server.stop(0);
	}

	@Test
	void getReturnsStatusBodyAndHeaders() throws Exception {
		HTTPUtils.HttpResponse res = HTTPUtils.doGet(base + "/echo");
		assertEquals(200, res.getStatus());
		assertEquals("pong", res.getBodyAsString());
		assertEquals("yes", res.getHeader("x-test"));
		assertNull(lastAuth.get());
	}

	@Test
	void postSendsAnEncodedForm() throws Exception {
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("name", "a b&c=d");
		params.put("empty", null);
		HTTPUtils.doPost(base + "/echo", params);
		assertEquals("name=a+b%26c%3Dd&empty=", lastBody.get());
		assertTrue(lastType.get().startsWith("application/x-www-form-urlencoded"), lastType.get());
	}

	@Test
	void postPayloadSendsBytesAndContentType() throws Exception {
		HTTPUtils.postPayload(base + "/echo", new HTTPUtils.Payload("<x/>", "text/xml"));
		assertEquals("<x/>", lastBody.get());
		assertEquals("text/xml", lastType.get());
	}

	@Test
	void refusesBasicCredentialsOverPlainHttpWithoutSendingAnything() {
		assertThrows(IllegalArgumentException.class, () -> HTTPUtils.doGet(base + "/echo", "user", "pass"));
		assertEquals(0, hits.get());
	}

	@Test
	void everyRequestHasATimeout() {
		assertThrows(HttpTimeoutException.class, () -> HTTPUtils.doGet(base + "/slow", 300));
		assertTrue(HTTPUtils.DEFAULT_TIMEOUT > 0);
	}
}
