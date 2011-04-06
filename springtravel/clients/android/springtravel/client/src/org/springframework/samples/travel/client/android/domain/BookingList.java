package org.springframework.samples.travel.client.android.domain;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * *
 * <p/>
 * Returns a list of the current bookings
 *
 * @author Josh Long
 */
@Root(name = "bookings")
public class BookingList {

	@ElementList(inline = true, required = false)
	private List<Booking> bookings;

	public BookingList(List<Booking> bookings) {
		this.bookings = bookings;
	}

	public BookingList() {
	}

	public List<Booking> getBookings() {
		return bookings;
	}

	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings;
	}
}
