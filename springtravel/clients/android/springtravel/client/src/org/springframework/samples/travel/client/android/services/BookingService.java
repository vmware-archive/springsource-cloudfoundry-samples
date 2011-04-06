package org.springframework.samples.travel.client.android.services;

import org.springframework.samples.travel.client.android.domain.Amenity;
import org.springframework.samples.travel.client.android.domain.Booking;
import org.springframework.samples.travel.client.android.domain.Hotel;
import org.springframework.samples.travel.client.android.domain.User;

import java.util.Collection;
import java.util.Date;

/**
 * Interface defining the Java contract for our client side proixy to the RESTful services defined by the Spring Travel application.
 *
 * @author Josh Long
 * @see RestfulBookingServiceClient
 */
public interface BookingService {

	User login(String u, String p);

	Hotel getHotelById(long id);

	void cancelConfirmation(String user, long bookingId);

	Booking createBooking(String userId, long hotelId, Collection<Amenity> ams, Date checkinDate, Date checkoutDate,
												String creditCard, String creditCardName,
												int creditCardExpiryMonth, int creditCardExpiryYear,
												boolean smoking, int beds);

	Booking createBooking(Booking booking);

	User getUserById(String usr);

	/**
	 * provides a convenient way to search for all hotels that have a certain string in their name and to also limit searches by a maximum
	 * per-night price. If the price is 0 it won't be considered in the search on the server side.
	 *
	 * @param query		the query (e.g., "hilton")
	 * @param maxPrice the price (a decimal, e.g, 300.00)
	 * @return a colleciton of {@link Hotel} records that match the search criteria
	 */
	Collection<Hotel> searchHotels(String query, double maxPrice);

	/**
	 * Returns all the booking information for a given user (by their User ID, specifically)
	 *
	 * @param user the ID of the user (e.g., "keith")
	 * @return a collection of all the {@link Booking}s belonging to this user
	 */
	Collection<Booking> getUserBookings(String user);
}
