package org.tauasa.web.smoke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tauasa.web.Stub;

/** Uses src/test/resources/META-INF/smoketest.properties, which requires the role "ops". */
class SmoketestServletTest {

	private SmoketestServlet servlet;
	private final AtomicReference<Integer> error = new AtomicReference<>();
	private final StringWriter body = new StringWriter();

	@BeforeEach
	void init() throws Exception {
		CountingSmoketest.RUNS.set(0);
		servlet = new SmoketestServlet();
		servlet.init(Stub.of(ServletConfig.class, Map.of()));
	}

	private HttpServletRequest request(boolean inOpsRole, Map<String, String> params) {
		Map<String, Function<Object[], Object>> a = new HashMap<>();
		a.put("isUserInRole", args -> inOpsRole && "ops".equals(args[0]));
		a.put("getRequestURI", args -> "/smoke");
		a.put("getRequestURL", args -> new StringBuffer("http://localhost/smoke"));
		a.put("getRemoteAddr", args -> "127.0.0.1");
		a.put("getParameter", args -> params.get(args[0]));
		a.put("getParameterNames", args -> Collections.enumeration(params.keySet()));
		return Stub.of(HttpServletRequest.class, a);
	}

	private HttpServletResponse response() {
		PrintWriter writer = new PrintWriter(body, true);
		Map<String, Function<Object[], Object>> a = new HashMap<>();
		a.put("sendError", args -> { error.set((Integer) args[0]); return null; });
		a.put("getWriter", args -> writer);
		return Stub.of(HttpServletResponse.class, a);
	}

	@Test
	void refusesAUserWithoutTheRoleAndRunsNothing() throws Exception {
		servlet.doGet(request(false, Map.of()), response());
		assertEquals(HttpServletResponse.SC_FORBIDDEN, error.get());
		assertEquals(0, CountingSmoketest.RUNS.get());
		assertEquals("", body.toString());
	}

	@Test
	void runsTheTestsForAUserWithTheRole() throws Exception {
		servlet.doGet(request(true, Map.of()), response());
		assertNull(error.get());
		assertEquals(1, CountingSmoketest.RUNS.get());
	}

	@Test
	void testsAreNotVerboseUnlessAsked() throws Exception {
		servlet.doGet(request(true, Map.of()), response());
		assertEquals(Boolean.FALSE, CountingSmoketest.lastVerbose);
	}

	@Test
	void theResourceDownloadIsGone() throws Exception {
		servlet.doGet(request(true, Map.of("_res", "/META-INF/smoketest.properties")), response());
		assertFalse(body.toString().contains("smoketest.required.role"), body.toString());
	}
}
