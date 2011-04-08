/*
 * Copyright 2009-2010 the original author or authors.
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
package org.springframework.batch.admin.sample.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class JsonWrapper {

	private final SpelExpressionParser parser;

	private final StandardEvaluationContext context;

	private Object target;

	private final String content;

	public JsonWrapper(String content) throws Exception {
		this.content = content;
		try {
			target = new MappingJsonFactory().createJsonParser(
					content.replace("\\", "/")).readValueAs(Map.class);
		} catch (JsonParseException e) {
			throw new JsonMappingException("Cannot create wrapper for:\n"
					+ content, e);
		} catch (JsonMappingException e) {
			try {
				List<?> list = new MappingJsonFactory().createJsonParser(
						content.replace("\\", "/")).readValueAs(List.class);
				HashMap<String, Object> map = new HashMap<String, Object>();
				if (list.isEmpty()) {
					target = map;
				} else {
					Object object = list.get(0);
					@SuppressWarnings("unchecked")
					boolean isNamedMap = object instanceof Map && ((Map<String,?>)object).containsKey("name");
					if (isNamedMap) {
						target = map;
						for (Object item : list ) {
							@SuppressWarnings("unchecked")
							String key = (String) ((Map<String,?>)item).get("name");
							map.put(key, item);
						}
					}
				}
				System.err.println(list.get(0).getClass());
			} catch (JsonParseException ex) {
				throw new JsonMappingException("Cannot create wrapper for:\n"
						+ content, ex);
			}
		}
		context = new StandardEvaluationContext();
		context.addPropertyAccessor(new MapAccessor());
		parser = new SpelExpressionParser();
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getMap() {
		return (Map<String, Object>) target;
	}

	public Object get(String expression) throws Exception {
		return get(expression, Object.class);
	}

	public <T> T get(String expression, Class<T> type) throws Exception {
		return parser.parseExpression(expression).getValue(context, target,
				type);
	}

	@Override
	public String toString() {
		return content;
	}

}