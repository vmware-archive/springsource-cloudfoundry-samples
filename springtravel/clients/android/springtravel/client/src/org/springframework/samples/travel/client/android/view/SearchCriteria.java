package org.springframework.samples.travel.client.android.view;

import org.springframework.util.Assert;

/**
 * A simple object to store client-side state for the search dialog
 *
 * @author Josh Long
 */
public class SearchCriteria {

	private double maxPrice;

	private String query;

	public SearchCriteria(String query, double maxPrice) {
		Assert.notNull(query,  "the query can't be null!");
		this.query = query.trim();
		this.maxPrice = maxPrice;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public double getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(double maxPrice) {
		this.maxPrice = maxPrice;
	}
}
