/*
 * Copyright (c) 2011 by the original author(s).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.samples.tickeranalysis;

import org.springframework.data.annotation.Id;
import org.springframework.data.document.mongodb.mapping.Document;

/**
 * @author Jon Brisbin <jbrisbin@vmware.com>
 */
@Document(collection = "tickersummary")
public class Summary {

	@Id
	private final String symbol;
	private final Long timestamp;
	private Float total = new Float(0);
	private Integer samples = 0;
	private Float min = Float.MAX_VALUE;
	private Float average = new Float(0);
	private Float max = Float.MIN_VALUE;
	private Integer volume = 0;

	public Summary(String symbol, Long timestamp) {
		this.symbol = symbol;
		this.timestamp = timestamp;
	}

	public String getSymbol() {
		return symbol;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public Float getMin() {
		return min;
	}

	public void setMin(Float min) {
		this.min = min;
	}

	public Float getAverage() {
		return average;
	}

	public void setAverage(Float average) {
		this.average = average;
	}

	public Float getMax() {
		return max;
	}

	public void setMax(Float max) {
		this.max = max;
	}

	public Integer getVolume() {
		return volume;
	}

	public void setVolume(Integer volume) {
		this.volume = volume;
	}

	public void addTickerEvent(TickerEvent tickerEvent) {
		if (tickerEvent.getPrice() > max) {
			max = tickerEvent.getPrice();
		}
		if (tickerEvent.getPrice() < min) {
			min = tickerEvent.getPrice();
		}

		total += tickerEvent.getPrice();
		this.volume += tickerEvent.getVolume();
		this.average = (total / ++samples);
	}
}
