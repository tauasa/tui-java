package org.tauasa.web.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.tauasa.web.Stub;

class LogoutFilterTest {

	@Test
	void encodesParametersCopiedIntoTheRedirect() throws Exception {
		Map<String, String> init = new LinkedHashMap<>();
		init.put(LogoutFilter.PARM_REDIRECT, "true");
		Map<String, Function<Object[], Object>> cfg = new HashMap<>();
		cfg.put("getServletContext", a -> Stub.of(ServletContext.class, Map.of()));
		cfg.put("getInitParameterNames", a -> Collections.enumeration(init.keySet()));
		cfg.put("getInitParameter", a -> init.get(a[0]));
		FilterConfig config = Stub.of(FilterConfig.class, cfg);

		LogoutFilter filter = new LogoutFilter();
		filter.init(config);

		Map<String, String> params = new LinkedHashMap<>();
		params.put("a", "1&next=//evil.example");
		Map<String, Function<Object[], Object>> req = new HashMap<>();
		req.put("getParameterNames", a -> Collections.enumeration(params.keySet()));
		req.put("getParameter", a -> params.get(a[0]));
		req.put("getContextPath", a -> "/app");
		AtomicReference<String> location = new AtomicReference<>();
		HttpServletResponse res = Stub.of(HttpServletResponse.class,
				Map.of("sendRedirect", a -> { location.set((String) a[0]); return null; }));

		filter.doFilter(new FilterHelper(config, Stub.of(FilterChain.class, Map.of()),
				Stub.of(HttpServletRequest.class, req), res));

		assertEquals("/app/index.jsp?a=1%26next%3D%2F%2Fevil.example", location.get());
	}
}
