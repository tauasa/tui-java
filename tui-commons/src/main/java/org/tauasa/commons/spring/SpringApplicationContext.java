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
package org.tauasa.commons.spring;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.tauasa.commons.util.Utils;

/**
 * Singleton {@link ApplicationContextAware} implementation that allows classes
 * outside of a Spring container to access the Spring {@link ApplicationContext}.
 * <p/>
 * Use of this class requires a single stanza in one of your application context
 * configuration files so the {@link ApplicationContext} can be properly injected
 * during application start-up.
 * <p/>
 * Example:
 * <code>
 * &lt;beans&gt;
 * 	...
 * 	&lt;bean id="myContext" class="org.tauasa.commons.spring.SpringApplicationContext"/&gt;
 * 	...
 * &lt;/beans&gt;
 * </code>
 * After application startup, classes outside of the Spring container can use this singleton
 * as a POJO factory:
 * 
 * <code>
 * MyBean bean = SpringApplicationContext.getBean("someBeanId", MyBean.class);
 * </code>
 * 
 * @author Tauasa Timoteo 
 *
 */
public class SpringApplicationContext implements ApplicationContextAware {

	private static ApplicationContext CONTEXT;

	public SpringApplicationContext() {
	}

	public static <T> List<T> getBeanList(Class<T> type){
		Map<String, T> map = getBeanMap(type);
		if(Utils.isEmpty(map)){
			return null;
		}
		return Collections.list(Collections.enumeration(map.values()));
	}

	public static <T> Map<String, T> getBeanMap(Class<T> type){
		return CONTEXT.getBeansOfType(type);
	}

	public static <T> T getBean(String name, Class<T> type){
		return CONTEXT.getBean(name, type);
	}

	public static Object getBean(String name){
		return CONTEXT.getBean(name);
	}

	public static Object getBean(String name, Object...args){
		return CONTEXT.getBean(name, args);
	}

	@Override
	public void setApplicationContext(ApplicationContext context)throws BeansException {
		CONTEXT=context;
	}

	public static ApplicationContext getContext(){
		return CONTEXT;
	}

}
