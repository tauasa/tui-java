/*
 * Copyright 2012 Tauasa Timoteo
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation 
 * files (the “Software”), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-
 * INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN 
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF 
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 */
package org.tauasa.commons.metrics;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
	

/**
 * Statsd {@link Metrics} implementation backed by the {@link StatsDClient}
 *
 * @author <a href="mailto:tauasa@gmail dot com?subject=org.tauasa.commons.metrics.StatsD">tauasa@gmail dot com</a>
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
