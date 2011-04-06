package org.springframework.samples.travel.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@XmlRootElement
public class Bookings {
	private List<Booking> bookings ;

	public Bookings() {
		bookings = new ArrayList<Booking>();
	}

	public Bookings( Collection<Booking> bookings) {
		this.bookings = new ArrayList<Booking>(bookings);
	}

	public void addBooking(Booking b){
		bookings.add(b);
	}

	@XmlElement(name = "booking", required = true, nillable = false)
	public List<Booking> getBookings() {
		return bookings;
	}

	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings;
	}
}
