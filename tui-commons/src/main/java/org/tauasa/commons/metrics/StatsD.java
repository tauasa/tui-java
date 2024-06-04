/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.metrics;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
	

/**
 * Statsd {@link Metrics} implementation backed by the {@link StatsDClient}
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=org.tauasa.commons.metrics.StatsD">tauasa@gmail.com</a>
 */
public class StatsD implements Metrics{
	
	private final StatsDClient client; 

	public StatsD(StatsDClient client) {
		this.client=client;
	}
	
	public StatsD(String prefix, String host, int port) {
		this.client = new NonBlockingStatsDClient(prefix, host, port);
	}
	
	public StatsDClient getClient(){
		return client;
	}

	@Override
	public void count(String name, long delta) {
		client.count(name, delta);
	}

	@Override
	public void count(String name, long delta, double sampleRate) {
		client.count(name, delta, sampleRate);
	}

	@Override
	public void increment(String name) {
		client.increment(name);
	}

	@Override
	public void decrement(String name) {
		client.decrement(name);
	}

	@Override
	public void gaugeValue(String name, long value) {
		client.recordGaugeValue(name, value);
	}
	
	@Override
	public void gaugeDelta(String name, long delta) {
		client.recordGaugeDelta(name, delta);
	}
	
	/*
	@Override
	public void gaugeValue(String name, double value) {
		client.recordGaugeValue(name, value);
	}
	
	@Override
	public void gaugeDelta(String name, double delta) {
		client.recordGaugeDelta(name, delta);
	}//*/

	@Override
	public void event(String name, String eventName) {
		client.recordSetEvent(name, eventName);
	}

	@Override
	public void time(String name, long timeMs) {
		client.recordExecutionTime(name, timeMs);
	}

	@Override
	public void time(String name, long timeMs, double sampleRate) {
		client.recordExecutionTime(name, timeMs, sampleRate);
	}

	@Override
	public void now(String name, long startTimeMs) {
		client.recordExecutionTimeToNow(name, startTimeMs);
	}
	
	@Override
	public void stop(){
		client.stop();
	}
}
