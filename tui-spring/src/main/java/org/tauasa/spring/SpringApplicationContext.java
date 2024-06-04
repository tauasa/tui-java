/**
 * Copyright (c) 2012 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.spring;

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
 * 	&lt;bean id="myContext" class="org.tauasa.spring.SpringApplicationContext"/&gt;
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
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a> 
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
