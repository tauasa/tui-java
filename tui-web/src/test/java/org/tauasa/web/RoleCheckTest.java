package org.tauasa.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;

class RoleCheckTest {

	/** A signed-in user who holds every role they're asked about. */
	private static final HttpServletRequest ANYONE = Stub.of(HttpServletRequest.class,
			Map.of("isUserInRole", a -> true));
	private static final HttpServletRequest OPS_ONLY = Stub.of(HttpServletRequest.class,
			Map.of("isUserInRole", a -> "ops".equals(a[0])));

	@Test
	void noRolesMeansNobodyPasses() {
		assertFalse(HttpServletRequestHelper.isUserInRole(ANYONE));
		assertFalse(HttpServletRequestHelper.isUserInRole(ANYONE, new String[0]));
		assertFalse(JSTL.isUserInRole(ANYONE, new String[0]));
		assertFalse(JSTL.isUserInRole(ANYONE, (String[]) null));
	}

	@Test
	void aListedRolePasses() {
		assertTrue(HttpServletRequestHelper.isUserInRole(OPS_ONLY, "admin", "ops"));
		assertFalse(HttpServletRequestHelper.isUserInRole(OPS_ONLY, "admin"));
		assertTrue(JSTL.isUserInRole(OPS_ONLY, new String[] {"ops"}));
		assertFalse(JSTL.isUserInRole(OPS_ONLY, new String[] {"admin"}));
	}
}
