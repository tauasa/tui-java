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

	

/**
 * Contract for tracking application-level metrics. Implementations are typically 
 * backed by a 3rd-party client library.
 *
 * @author <a href="mailto:tauasa@gmail dot com?subject=org.tauasa.commons.metrics.Metrics">tauasa@gmail dot com</a>
 */
public interface Metrics{
	
	/**
     * Adjusts the specified counter by a given delta.
     * 
     * @param name
     *     the name of the counter to adjust
     * @param delta
     *     the amount to adjust the counter by
     */
	public void count(String name, long delta);
	
	/**
     * Adjusts the specified counter by a given delta.
     * 
     * @param name
     *     the name of the counter to adjust
     * @param delta
     *     the amount to adjust the counter by
     * @param sampleRate
     *     the sampling rate being employed
     */
	public void count(String name, long delta, double sampleRate);
	
	/**
     * Increments the specified counter by one.
     * 
     * @param name
     *     the name of the counter to increment
     */
	public void increment(String name);
	
	/**
     * Decrements the specified counter by one.
     * 
     * @param name
     *     the name of the counter to decrement
     */
	public void decrement(String name);
	
	/**
     * Records the latest fixed value for the specified named gauge
     * 
     * @param aspect
     *     the name of the gauge
     * @param value
     *     the new reading of the gauge
     */
	public void gaugeValue(String name, long value);
	
	/*
     * Records the latest fixed value for the specified named gauge
     * 
     * @param aspect
     *     the name of the gauge
     * @param value
     *     the new reading of the gauge
     */
	//public void gaugeValue(String name, double value);
	
	/**
     * Records a change in the value of the specified named gauge
     * 
     * @param aspect
     *     the name of the gauge
     * @param value
     *     the new reading of the gauge
     * @param delta
     *     the +/- delta to apply to the gauge
     */
	public void gaugeDelta(String name, long delta);
	
	/*
     * Records a change in the value of the specified named gauge
     * 
     * @param aspect
     *     the name of the gauge
     * @param value
     *     the new reading of the gauge
     * @param delta
     *     the +/- delta to apply to the gauge
     */
	//public void gaugeDelta(String name, double delta);
	
	/**
     * Records an occurrence of the specified named event
     * 
     * @param name
     *     the name of the set
     * @param eventName
     *     the value to be added to the set
     */
	public void event(String name, String eventName);
	
	/**
     * Adjusts the specified counter by a given delta.
     *
     * @param name
     *     the name of the counter to adjust
     * @param delta
     *     the amount to adjust the counter by
     */
	public void time(String name, long timeMs);
	
	/**
     * Adjusts the specified counter by a given delta.
     *
     * @param name
     *     the name of the counter to adjust
     * @param delta
     *     the amount to adjust the counter by
     * @param sampleRate
     *     the sampling rate being employed
     */
	public void time(String name, long timeMs, double sampleRate);
	
	/**
     * Records an execution time in milliseconds for the specified named operation. The execution
     * time is calculated as the delta between the specified start time and the current system
     * time (using {@link System#currentTimeMillis()})
     *
     * @param name
     *     the name of the timed operation
     * @param startTimeMs
     *     the system time, in millis, at the start of the operation that has just completed
     */
	public void now(String name, long startTimeMs);
	
	/**
	 * Cleans up any resources being used
	 * */
	public void stop();

}

