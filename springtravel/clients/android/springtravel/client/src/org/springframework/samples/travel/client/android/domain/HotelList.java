package org.springframework.samples.travel.client.android.domain;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Simple wrapper for the hotel searchHotels results
 *
 * @author Josh Long
 */
@Root(name = "hotels")
public class HotelList {
	@ElementList(inline = true, required = false)
	private List<Hotel> hotels;

	public HotelList() {
	}

	public HotelList(List<Hotel> hotels) {
		this.hotels = hotels;
	}

	public List<Hotel> getHotels() {
		return hotels;
	}

	public void setHotels(List<Hotel> hotels) {
		this.hotels = hotels;
	}

}
