package org.springframework.samples.travel.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.samples.travel.domain.*;
import org.springframework.samples.travel.services.BookingService;
import org.springframework.samples.travel.services.SearchCriteria;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * Provides RESTful endpoint for accessing this application's booking functionality.
 * <p/>
 * This can be used from zimbra, from Android, etc.
 * <p/>
 * todo support paging
 * <p/>
 * todo how can we secure this for remote clients?
 *
 * @author Josh Long
 */
@Controller
@RequestMapping(value = "/ws/", headers = HotelsRestController.acceptHeader)
public class HotelsRestController {

	static private final String acceptHeader = "Accept=application/json, application/xml";

	private Log log = LogFactory.getLog(getClass());

	@Autowired
	private BookingService bookingService;


	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
	@ResponseBody
	public User user( @PathVariable("id") String userId) {
		return this.bookingService.findUser(userId);
	}

	@RequestMapping(value = "/bookings/{user}", method = RequestMethod.GET)
	@ResponseBody
	public Bookings bookingsForUser( @PathVariable("user") String user) {
		return fromResults(this.bookingService.findBookings(user));
	}

	//http://localhost:8080/ws/hotel/1
	@RequestMapping(value = "/hotel/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Hotel hotel(@PathVariable("id") long id) {
		return this.bookingService.findHotelById(id);
	}

	// todo whats the right way to handle this? currently its being handled using SPring Webflow on the web tier, the passwords are in the config. we need to make the config share the database jst like th rest of the service code, then make it so that REST clients can login as well.
	@RequestMapping(value = "/users/login", method = RequestMethod.POST)
	@ResponseBody
	public User login(@RequestBody User u ) {

		String usrname = u.getUsername();
		String pw = u.getPassword();
		return bookingService.login(usrname, pw);
	}

	//	http://localhost:8080/ws/bookings/josh/327680
	@RequestMapping(value = "/bookings/{user}/{bookingId}", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void cancelWithDelete(@PathVariable("user") String user,
															 @PathVariable("bookingId") Long bookingId) {

		bookingService.cancelBooking(bookingId);
	}

	// todo need to figure out how to setup the HttpHiddenMethod filter to work for regular AJAX clients
	// so that we can simply send the HTTP POST url to a DELETE method with an additional param in the JSON body or something
	@RequestMapping(value = "/bookings/delete/{user}/{bookingId}", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void cancelWithPost(@PathVariable("user") String user,
														 @PathVariable("bookingId") Long bookingId) {

		bookingService.cancelBooking(bookingId);
	}

	//http://localhost:8080/ws/hotels/search/?q=hilton&price=2000
	@RequestMapping(value = "/hotels/search/", method = RequestMethod.GET)
	@ResponseBody
	public Hotels search(@RequestParam("q") String query, @RequestParam("price") double maxPrice) {
		SearchCriteria searchCriteria = new SearchCriteria();
		searchCriteria.setMaximumPrice(maxPrice);
		searchCriteria.setSearchString(query);
		return fromResults(bookingService.findHotels(searchCriteria));
	}

	/*

		POST:
		XML
		<booking total="700.00" smoking="false" nights="2"  creditCardName="Josh long" creditCardExpiryYear="5"
			creditCardExpiryMonth="1" creditCard="2422242224222422" checkoutDate="2011-09-31T00:00:00-07:00" checkinDate="2011-09-29T00:00:00-07:00" beds="1">
	<amenities/>
	<hotel  id="24" />
	<user username="josh"  />
	</booking>

	 or

	JSON:  /bookings/josh (specify content-type=application/json)

	{
			"user":{ "username":"josh" },
			"checkinDate":1333004400000,
			"hotel":{  "id":24 },
			"checkoutDate":1329980400000,
			"creditCard":"2422242224222422",
			"smoking":false,
			"beds":1,
			"creditCardName":"Josh long",
			"creditCardExpiryMonth":1,
			"creditCardExpiryYear":5

	}

		* */
	@RequestMapping(value = "/bookings/{user}", method = RequestMethod.POST)
	@ResponseBody
	public Booking create(@RequestBody Booking booking) {
		String usr = booking.getUser().getUsername();
		long hotelId = booking.getHotel().getId();

		Booking b = bookingService.createBooking(hotelId, usr);
		b.setCheckoutDate(booking.getCheckoutDate());
		b.setCheckinDate(booking.getCheckinDate());
		b.setAmenities(booking.getAmenities());
		b.setBeds(booking.getBeds());
		b.setSmoking(booking.isSmoking());
		b.setCreditCard(booking.getCreditCard());
		b.setCreditCardExpiryMonth(booking.getCreditCardExpiryMonth());
		b.setCreditCardName(booking.getCreditCardName());
		b.setCreditCardExpiryYear(booking.getCreditCardExpiryYear());

		bookingService.persistBooking(b);

		return b;
	}

	private Bookings fromResults(Collection<Booking> bookingCollection) {
		return new Bookings(bookingCollection);
	}

	private Hotels fromResults(Collection<Hotel> hotelsCollection) {
		return new Hotels(hotelsCollection);
	}
}
