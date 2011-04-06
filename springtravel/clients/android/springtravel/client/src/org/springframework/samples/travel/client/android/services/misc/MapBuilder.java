package org.springframework.samples.travel.client.android.services.misc;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility to make building {@link org.springframework.web.client.RestTemplate} template parameters easier.
 *
 * @param <K>
 * @param <V>
 * @author Josh Long
 */
public class MapBuilder<K, V> {
	private Map<K, V> params = new HashMap<K, V>();

	public static <K, V> MapBuilder<K, V> map() {
		return new MapBuilder<K, V>();
	}

	public MapBuilder<K, V> param(K k, V v) {
		this.params.put(k, v);
		return this;
	}

	public Map<K, V> build() {
		return this.params;
	}
}

