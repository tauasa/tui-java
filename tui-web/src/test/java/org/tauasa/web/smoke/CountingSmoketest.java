package org.tauasa.web.smoke;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tauasa.web.ServletHelper;

/** Counts its runs and records whether it was verbose, for SmoketestServletTest. */
public class CountingSmoketest extends AbstractSmoketest {

	static final AtomicInteger RUNS = new AtomicInteger();
	static volatile Boolean lastVerbose;

	@Override
	public void init(Properties smoketestProperties) throws SmoketestConfigException {
		super.init(smoketestProperties);
		lastVerbose = verbose;
	}

	@Override
	public void doExecute(ServletHelper helper) {
		RUNS.incrementAndGet();
	}

	@Override
	public Logger getLogger() {
		return LoggerFactory.getLogger(CountingSmoketest.class);
	}
}
