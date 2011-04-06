package org.springframework.samples.travel.services;

import org.springframework.samples.travel.domain.Booking;
import org.springframework.samples.travel.domain.Hotel;
import org.springframework.samples.travel.domain.User;

import java.util.Date;
import java.util.List;

/**
 * A service interface for retrieving hotels and bookings from a backing repository. Also supports the ability to cancel
 * a booking.
 */
public interface BookingService {
	/**
	 *
	 * @param username the user name
	 * @return the user
	 */
	User findUser(String username);

	User login (String u, String pw) ;
	/**
	 * Find bookings made by the given user
	 *
	 * @param username the user's name
	 * @return their bookings
	 */
	List<Booking> findBookings(String username);

	/**
	 * Find hotels available for booking by some criteria.
	 *
	 * @param criteria the search criteria
	 * @return a list of hotels meeting the criteria
	 */
	List<Hotel> findHotels(SearchCriteria criteria);

	/**
	 * Find hotels by their identifier.
	 *
	 * @param id the hotel id
	 * @return the hotel
	 */
	Hotel findHotelById(Long id);

	/**
	 * Create a new, transient hotel booking instance for the given user.
	 *
	 * @param hotelId	the hotelId
	 * @param userName the user name
	 * @return the new transient booking instance
	 */
	Booking createBooking(Long hotelId, String userName);

	/**
	 * Cancel an existing booking.
	 *
	 * @param id the booking id
	 */
	void cancelBooking(Long id);

	/**
	 * persists the detached booking object
	 *
	 * @param bo booking to persist
	 */
	void persistBooking(Booking bo);
}

