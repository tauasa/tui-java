package org.tauasa.commons.util;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class UtilsThrowablesTest {

	@Test
	void rootCauseIsTheInnermost() {
		IOException root = new IOException("disk");
		assertSame(root, Utils.getRootCause(new RuntimeException(new IllegalStateException(root))));
		assertSame(root, Utils.getRootCause(root));
	}

	@Test
	void propagateRethrowsUncheckedAndWrapsChecked() {
		IllegalStateException unchecked = new IllegalStateException("x");
		assertSame(unchecked, assertThrows(IllegalStateException.class, () -> Utils.propogate(unchecked)));
		IOException checked = new IOException("y");
		RuntimeException wrapped = assertThrows(RuntimeException.class, () -> Utils.propogate(checked));
		assertSame(checked, wrapped.getCause());
	}

	@Test
	void propagateIfPossibleThrowsTheGivenTypeAndLetsOthersPass() throws Exception {
		IOException checked = new IOException("z");
		assertSame(checked, assertThrows(IOException.class, () -> Utils.propogate(checked, IOException.class)));
		Utils.propogate(new Exception("not an IOException"), IOException.class); // no throw
		Utils.propogate(null, IOException.class); // no throw
	}

	@Test
	void stackTraceHoldsTheMessage() {
		assertTrue(Utils.getStackTrace(new IllegalStateException("marker-123")).contains("marker-123"));
	}
}
