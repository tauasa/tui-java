package org.tauasa.commons.jdbc;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

class ThreadLocalDAOTest {

	/** A Connection that does nothing but remember whether it was closed. */
	private static Connection connection(AtomicBoolean closed) {
		return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class<?>[] {Connection.class},
				(proxy, method, args) -> {
					switch (method.getName()) {
						case "close": closed.set(true); return null;
						case "isClosed": return closed.get();
						case "hashCode": return System.identityHashCode(proxy);
						case "equals": return proxy == args[0];
						case "toString": return "conn@" + System.identityHashCode(proxy);
						default:
							Class<?> t = method.getReturnType();
							return t == boolean.class ? false : t == int.class ? 0 : null;
					}
				});
	}

	private static IConnectionFactory factory(Connection c) {
		return new IConnectionFactory() {
			@Override public Connection getConnection() { return c; }
			@Override public void release() { }
		};
	}

	@Test
	void twoDaosOnOneThreadNeverShareAConnection() throws SQLException {
		Connection a = connection(new AtomicBoolean());
		Connection b = connection(new AtomicBoolean());
		ThreadLocalDAO daoA = new ThreadLocalDAO(factory(a));
		ThreadLocalDAO daoB = new ThreadLocalDAO(factory(b));

		assertSame(a, daoA.getConnection());
		assertSame(b, daoB.getConnection());
		assertNotSame(daoA.getConnection(), daoB.getConnection());
	}

	@Test
	void closeAllClosesAndForgetsOnlyThisDaosConnection() throws SQLException {
		AtomicBoolean closedA = new AtomicBoolean();
		AtomicBoolean closedB = new AtomicBoolean();
		ThreadLocalDAO daoA = new ThreadLocalDAO(factory(connection(closedA)));
		ThreadLocalDAO daoB = new ThreadLocalDAO(factory(connection(closedB)));
		daoA.getConnection();
		Connection b = daoB.getConnection();

		daoA.closeAll();
		assertTrue(closedA.get());
		assertSame(b, daoB.getConnection());
	}
}
