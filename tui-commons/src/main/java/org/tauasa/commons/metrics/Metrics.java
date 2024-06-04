/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.metrics;

	

/**
 * Contract for tracking application-level metrics. Implementations are typically 
 * backed by a 3rd-party client library.
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=org.tauasa.commons.metrics.Metrics">tauasa@gmail.com</a>
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

