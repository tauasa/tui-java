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
package org.tauasa.commons.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import com.wcohen.ss.Level2JaroWinkler;
import com.wcohen.ss.api.StringDistance;

/**
 * Collection of static methods for calculating string-distance. This implementation 
 * currently uses the Jaro-Winkler algorithm but could easily be modified to use any 
 * other algorithm (e.g. Diff-Helman, etc)
 *
 * @author Tauasa Timoteo
 * 
 */
public class StringDistanceUtils {

	private static final Logger log = LoggerFactory.getLogger(StringDistanceUtils.class);

	private StringDistanceUtils() {

	}

	public static double score(final String s, final String t){
		return score(s, t, false);
	}

	/**
	 * Returns a string distance score using the
	 * <a href="http://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance">Jaro-Winkler Level2 scoring algorithm</a>
	 * */
	public static double score(final String s, final String t, boolean caseInsensitive){
		//if both arguments are null we need to
		//return a perfect match right away
		if(s==null && t==null){
			if(log.isTraceEnabled()){
				log.trace("Perfect score for null arguments");
			}
			return 1;
		}
		//if one but not both arguments are null we need to
		//return a 0 to represent a perfect mismatch
		if(s==null || t==null){
			if(log.isTraceEnabled()){
				log.trace(String.format("Perfect mismatch due to a null argument (s=%s, t=%s)", s, t));
			}
			return 0;
		}

		//convert both strings to the same case for insensitive comparisons
		String _s = caseInsensitive?s.toUpperCase():s;
		String _t = caseInsensitive?t.toUpperCase():t;

		//if arguments are equal return a perfect score
		if(Objects.equals(_s, _t)){
			return 1;
		}

		//calculate the distance score
		Level2JaroWinkler jaro = new Level2JaroWinkler();
		StringDistance distance = jaro.getDistance();
		if(log.isDebugEnabled()){
			log.debug(distance.explainScore(_s, _t));
		}
		return distance.score(s, t);
	}

}
