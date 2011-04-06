package org.springframework.beans.factory;

/**
 * I wish this was available ;-(
 *
 * @author Rod Johnson (original author, from whom I've borrowed this ..)
 */
public interface InitializingBean {
	void afterPropertiesSet() throws Exception;
}
