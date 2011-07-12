/*
 * Copyright 2002-2010 the original author or authors.
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

package org.springframework.amqp.rabbit.stocks.config.server;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures RabbitTemplate for the server.
 * 
 * @author Mark Pollack
 * @author Mark Fisher
 */
@Configuration
public class RabbitServerConfiguration {

	/**
	 * Shared topic exchange used for publishing any market data (e.g. stock quotes) 
	 */
	protected static String MARKET_DATA_EXCHANGE_NAME = "app.stock.marketdata";

	/**
	 * The server-side consumer's queue that provides point-to-point semantics for stock requests.
	 */
	protected static String STOCK_REQUEST_QUEUE_NAME = "app.stock.request";

	/**
	 * Key that clients will use to send to the stock request queue via the default direct exchange.
	 */
	protected static String STOCK_REQUEST_ROUTING_KEY = STOCK_REQUEST_QUEUE_NAME;

	@Autowired
	private ConnectionFactory connectionFactory;
	
	@Bean
	public MessageConverter jsonMessageConverter() {
		return new JsonMessageConverter();
	}
	
	@Bean
	public TopicExchange marketDataExchange() {
		return new TopicExchange(MARKET_DATA_EXCHANGE_NAME);
	}

	/**
	 * @return the admin bean that can declare queues etc.
	 */
	@Bean
	public AmqpAdmin amqpAdmin() {
		RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
		return rabbitAdmin ;
	}

	/**
	 * The server's template will by default send to the topic exchange named
	 * {@link AbstractStockAppRabbitConfiguration#MARKET_DATA_EXCHANGE_NAME}.
	 */
	@Bean
	public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setExchange(MARKET_DATA_EXCHANGE_NAME);
		rabbitTemplate.setMessageConverter(jsonMessageConverter());
		return rabbitTemplate;
	}

	/**
	 * We don't need to define any binding for the stock request queue, since it's relying
	 * on the default (no-name) direct exchange to which every queue is implicitly bound.
	 */
	@Bean
	public Queue stockRequestQueue() {		
		return new Queue(STOCK_REQUEST_QUEUE_NAME);	
	}

}
