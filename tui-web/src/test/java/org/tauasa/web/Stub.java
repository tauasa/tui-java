package org.tauasa.web;

import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.function.Function;

/**
 * A stand-in for a servlet API interface: each method answers from the map by
 * name, and anything else returns false, zero, an empty enumeration or null.
 */
public final class Stub {

	private Stub() {
	}

	public static <T> T of(Class<T> type, Map<String, Function<Object[], Object>> answers) {
		return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, (proxy, method, args) -> {
			Function<Object[], Object> a = answers.get(method.getName());
			if (a != null) {
				return a.apply(args);
			}
			switch (method.getName()) {
				case "hashCode": return System.identityHashCode(proxy);
				case "equals": return proxy == args[0];
				case "toString": return type.getSimpleName() + " stub";
				default:
			}
			Class<?> t = method.getReturnType();
			if (t == boolean.class) return false;
			if (t == int.class) return 0;
			if (t == long.class) return 0L;
			if (t == Enumeration.class) return Collections.emptyEnumeration();
			return null;
		}));
	}
}
