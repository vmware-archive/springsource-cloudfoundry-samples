/*
 * Copyright 2002-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.amqp.rabbit.stocks.context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanDefinitionVisitor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

/**
 * <p>A Scope implementation that allows for beans to be refreshed dynamically at runtime (see {@link #refresh(String)}
 * and {@link #refreshAll()}). If a bean is refreshed then the next time the bean is accessed (i.e. a method is
 * executed) a new instance is created. All lifecycle methods are applied to the bean instances, so any destruction
 * callbacks that were registered in the bean factory are called when it is refreshed, and then the initialization
 * callbacks are invoked as normal when the new instance is created. A new bean instance is created from the original
 * bean definition, so any externalized content (property placeholders or expressions in string literals) is
 * re-evaluated when it is created.</p>
 * 
 * <p>Note that all beans in this scope are <em>only</em> initialized when first accessed, so the scope forces lazy
 * initialization semantics. The implementation involves creating a proxy for every bean in the scope, so there is a
 * flag {@link #setProxyTargetClass(boolean) proxyTargetClass} which controls the proxy creation, defaulting to JDK
 * dynamic proxies and therefore only exposing the interfaces implemented by a bean. If callers need access to other
 * methods then the flag needs to be set (and CGLib present on the classpath). Because this scope automatically proxies
 * all its beans, there is no need to add <code>&lt;aop:auto-proxy/&gt;</code> to any bean definitions.</p>
 * 
 * <p>The scoped proxy approach adopted here has a side benefit that bean instances are automatically
 * {@link Serializable}, and can be sent across the wire as long as the receiver has an identical application context on
 * the other side. To ensure that the two contexts agree that they are identical they have to have the same
 * serialization id. One will be generated automatically by default from the bean names, so two contexts with the same
 * bean names are by default able to exchange beans by name. If you need to override the default id then provide an
 * explicit {@link #setId(String) id} when the Scope is declared.</p>
 * 
 * @author Dave Syer
 * 
 * @since 3.1
 * 
 */
@ManagedResource
public class RefreshScope implements Scope, BeanFactoryPostProcessor, DisposableBean {

	private static final Log logger = LogFactory.getLog(RefreshScope.class);

	private ConcurrentMap<String, BeanCallbackWrapper> cache = new ConcurrentHashMap<String, BeanCallbackWrapper>();

	private String name = "refresh";

	private boolean proxyTargetClass = false;

	private ConfigurableListableBeanFactory beanFactory;

	private StandardEvaluationContext evaluationContext;

	private String id;

	/**
	 * Manual override for the serialization id that will be used to identify the bean factory. The default is a unique
	 * key based on the bean names in the bean factory.
	 * 
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * The name of this scope. Default "refresh".
	 * 
	 * @param name the name value to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Flag to indicate that proxies should be created for the concrete type, not just the interfaces, of the scoped
	 * beans.
	 * 
	 * @param proxyTargetClass the flag value to set
	 */
	public void setProxyTargetClass(boolean proxyTargetClass) {
		this.proxyTargetClass = proxyTargetClass;
	}

	public void destroy() throws Exception {
		refreshAll();
	}

	public Object get(String name, ObjectFactory<?> objectFactory) {

		BeanCallbackWrapper value = new BeanCallbackWrapper(name, objectFactory, proxyTargetClass);
		BeanCallbackWrapper result = cache.putIfAbsent(name, value);
		value = result == null ? value : result;
		return value.getBean();

	}

	public String getConversationId() {
		return name;
	}

	public void registerDestructionCallback(String name, Runnable callback) {
		BeanCallbackWrapper value = cache.get(name);
		if (value == null) {
			return;
		}
		value.setCallback(callback);
	}

	public Object remove(String name) {
		BeanCallbackWrapper value = cache.get(name);
		if (value == null) {
			return null;
		}
		return cache.remove(name, value);
	}

	public Object resolveContextualObject(String key) {
		Expression expression = parseExpression(key);
		return expression.getValue(evaluationContext, beanFactory);
	}

	private Expression parseExpression(String input) {
		if (StringUtils.hasText(input)) {
			ExpressionParser parser = new SpelExpressionParser();
			try {
				return parser.parseExpression(input);
			} catch (ParseException e) {
				throw new IllegalArgumentException("Cannot parse expression: " + input, e);
			}

		} else {
			return null;
		}
	}

	@ManagedOperation(description = "Dispose of the current instance of bean name provided and force a refresh on next method execution.")
	public void refresh(String name) {
		if (!name.startsWith("scopedTarget.")) {
			name = "scopedTarget."+name;
		}
		BeanCallbackWrapper wrapper = cache.remove(name);
		if (wrapper != null) {
			wrapper.destroy();
		}
	}

	@ManagedOperation(description = "Dispose of the current instance of all beans in this scope and force a refresh on next method execution.")
	public void refreshAll() {
		List<Throwable> errors = new ArrayList<Throwable>();
		Collection<BeanCallbackWrapper> wrappers = new HashSet<BeanCallbackWrapper>(cache.values());
		cache.clear();
		for (BeanCallbackWrapper wrapper : wrappers) {
			try {
				wrapper.destroy();
			} catch (RuntimeException e) {
				errors.add(e);
			}
		}
		if (!errors.isEmpty()) {
			throw wrapIfNecessary(errors.get(0));
		}
	}

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

		beanFactory.registerScope(name, this);
		setSerializationId(beanFactory);

		this.beanFactory = beanFactory;

		evaluationContext = new StandardEvaluationContext();
		evaluationContext.addPropertyAccessor(new BeanFactoryAccessor());

		Assert.state(beanFactory instanceof BeanDefinitionRegistry,
				"BeanFactory was not a BeanDefinitionRegistry, so RefreshScope cannot be used.");
		BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;

		for (String beanName : beanFactory.getBeanDefinitionNames()) {
			BeanDefinition definition = beanFactory.getBeanDefinition(beanName);
			// Replace this or any of its inner beans with scoped proxy if it
			// has this scope
			boolean scoped = name.equals(definition.getScope());
			Scopifier scopifier = new Scopifier(registry, name, proxyTargetClass, scoped);
			scopifier.visitBeanDefinition(definition);
			if (scoped) {
				createScopedProxy(beanName, definition, registry, proxyTargetClass);
			}
		}

	}

	/**
	 * If the bean factory is a DefaultListableBeanFactory then it can serialize scoped beans and deserialize them in
	 * another context (even in another JVM), as long as the ids of the bean factories match. This method sets up the
	 * serialization id to be either the id provided to the scope instance, or if that is null, a hash of all the bean
	 * names.
	 * 
	 * @param beanFactory the bean factory to configure
	 */
	private void setSerializationId(ConfigurableListableBeanFactory beanFactory) {

		if (beanFactory instanceof DefaultListableBeanFactory) {

			String id = this.id;
			if (id == null) {
				String names = Arrays.asList(beanFactory.getBeanDefinitionNames()).toString();
				logger.debug("Generating bean factory id from names: " + names);
				id = UUID.nameUUIDFromBytes(names.getBytes()).toString();
			}

			logger.info("BeanFactory id=" + id);
			((DefaultListableBeanFactory) beanFactory).setSerializationId(id);

		} else {
			logger.warn("BeanFactory was not a DefaultListableBeanFactory, so RefreshScope beans "
					+ "cannot be serialized reliably and passed to a remote JVM.");
		}

	}

	private static RuntimeException wrapIfNecessary(Throwable throwable) {
		if (throwable instanceof RuntimeException) {
			return (RuntimeException) throwable;
		}
		if (throwable instanceof Error) {
			throw (Error) throwable;
		}
		return new IllegalStateException(throwable);
	}

	private static BeanDefinitionHolder createScopedProxy(String beanName, BeanDefinition definition,
			BeanDefinitionRegistry registry, boolean proxyTargetClass) {
		BeanDefinitionHolder proxyHolder = ScopedProxyUtils.createScopedProxy(new BeanDefinitionHolder(definition,
				beanName), registry, proxyTargetClass);
		registry.registerBeanDefinition(beanName, proxyHolder.getBeanDefinition());
		return proxyHolder;
	}

	/**
	 * Helper class to scan a bean definition hierarchy and force the use of auto-proxy for scoped beans.
	 * 
	 * @author Dave Syer
	 * 
	 */
	private static class Scopifier extends BeanDefinitionVisitor {

		private final boolean proxyTargetClass;

		private final BeanDefinitionRegistry registry;

		private final String scope;

		private final boolean scoped;

		public Scopifier(BeanDefinitionRegistry registry, String scope, boolean proxyTargetClass, boolean scoped) {
			super(new StringValueResolver() {
				public String resolveStringValue(String value) {
					return value;
				}
			});
			this.registry = registry;
			this.proxyTargetClass = proxyTargetClass;
			this.scope = scope;
			this.scoped = scoped;
		}

		@Override
		protected Object resolveValue(Object value) {

			BeanDefinition definition = null;
			String beanName = null;
			if (value instanceof BeanDefinition) {
				definition = (BeanDefinition) value;
				beanName = BeanDefinitionReaderUtils.generateBeanName(definition, registry);
			} else if (value instanceof BeanDefinitionHolder) {
				BeanDefinitionHolder holder = (BeanDefinitionHolder) value;
				definition = holder.getBeanDefinition();
				beanName = holder.getBeanName();
			}

			if (definition != null) {
				boolean nestedScoped = scope.equals(definition.getScope());
				boolean scopeChangeRequiresProxy = !scoped && nestedScoped;
				if (scopeChangeRequiresProxy) {
					// Exit here so that nested inner bean definitions are not
					// analysed
					return createScopedProxy(beanName, definition, registry, proxyTargetClass);
				}
			}

			// Nested inner bean definitions are recursively analysed here
			value = super.resolveValue(value);
			return value;

		}

	}

	/**
	 * Wrapper for a bean instance and any destruction callback (DisposableBean etc.) that is registered for it. If the
	 * bean is disposable, the wrapper also guards access to the bean: a read lock (allowing concurrent access) is taken
	 * for all method executions except the destruction callback, which uses a write lock.
	 * 
	 * @author Dave Syer
	 * 
	 */
	private static class BeanCallbackWrapper {

		private Object bean;

		private Runnable callback;

		private ReadWriteLock lock;

		private final String name;

		private final ObjectFactory<?> objectFactory;

		private final boolean proxyTargetClass;

		public BeanCallbackWrapper(String name, ObjectFactory<?> objectFactory, boolean proxyTargetClass) {
			this.name = name;
			this.objectFactory = objectFactory;
			this.proxyTargetClass = proxyTargetClass;
		}

		public void setCallback(Runnable callback) {
			this.callback = callback;
		}

		public Object getBean() {
			if (bean == null) {
				bean = objectFactory.getObject();
				if (callback != null) {
					lock = new ReentrantReadWriteLock();
					bean = getDisposalLockProxy(bean, lock.readLock());
				}
			}
			return bean;
		}

		public void destroy() {

			if (callback == null) {
				return;
			}

			Lock lock = this.lock.writeLock();
			lock.lock();
			try {
				callback.run();
			} catch (Throwable e) {
				throw wrapIfNecessary(e);
			} finally {
				lock.unlock();
			}
			
			bean = null;

		}

		/**
		 * Apply a lock (preferably a read lock allowing multiple concurrent access) to the bean. Callers should replace
		 * the bean input with the output.
		 * 
		 * @param bean the bean to lock
		 * @param lock the lock to apply
		 * @return a proxy that locks while its methods are executed
		 */
		private Object getDisposalLockProxy(Object bean, final Lock lock) {
			ProxyFactory factory = new ProxyFactory(bean);
			factory.setProxyTargetClass(proxyTargetClass);
			factory.addAdvice(new MethodInterceptor() {
				public Object invoke(MethodInvocation invocation) throws Throwable {
					lock.lock();
					try {
						return invocation.proceed();
					} finally {
						lock.unlock();
					}
				}
			});
			return factory.getProxy();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			BeanCallbackWrapper other = (BeanCallbackWrapper) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

	}

}
