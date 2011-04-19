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

/**
 * @author Jon Brisbin <jbrisbin@vmware.com>
 */
public class TickerEvent {

	private String symbol;
	private Float price;
	private Integer volume;

	public TickerEvent() {
	}

	public TickerEvent(String symbol, Float price, Integer volume) {
		this.symbol = symbol;
		this.price = price;
		this.volume = volume;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Integer getVolume() {
		return volume;
	}

	public void setVolume(Integer volume) {
		this.volume = volume;
	}

	@Override public String toString() {
		return new StringBuffer().append("[").append(symbol).append(": price=").append(price).append(", volume=").append(volume).append("]").toString();
	}
}
