package org.springframework.samples.travel.client.android.services;

import org.springframework.http.ResponseEntity;
import org.springframework.samples.travel.client.android.domain.*;
import org.springframework.samples.travel.client.android.services.misc.MapBuilder;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Client-side facade for the server side REST endpoints
 * todo why does the {@link Booking} returned from the create* methods not reflect the derived fields "total" and "nights" from the server-side?
 *
 * @author Josh Long
 */
public class RestfulBookingServiceClient implements BookingService {

	// this should come from values/strings.xml
	private String baseServiceUrl;

	// Spring Android-provided {@link RestTemplate}
	private RestTemplate restTemplate = new RestTemplate();

	public void setBaseServiceUrl(String url) {
		String u = url;

		if (!u.endsWith("/"))
			u = u + "/";

		this.baseServiceUrl = u;
	}

	public RestfulBookingServiceClient(final String url) {
		setBaseServiceUrl(url);
	}

	public Booking createBooking(String userId, long hotelId, Collection<Amenity> ams, Date checkinDate, Date checkoutDate,
															 String creditCard, String creditCardName,
															 int creditCardExpiryMonth, int creditCardExpiryYear,
															 boolean smoking, int beds) {

		User usr = getUserById(userId);
		Hotel hotel = getHotelById(hotelId);

		Assert.notNull(usr, "the user must not be null");
		Assert.notNull(hotel, "the hotel must not be null");

		Booking b = new Booking();
		b.setAmenities(new java.util.ArrayList<Amenity>(ams));
		b.setUser(usr);
		b.setHotel(hotel);

		b.setBeds(beds);
		b.setCreditCard(creditCard);
		b.setCheckoutDate(checkoutDate);
		b.setCheckinDate(checkinDate);
		b.setSmoking(smoking);
		b.setCreditCardName(creditCardName);
		b.setCreditCardExpiryYear(creditCardExpiryYear);
		b.setCreditCardExpiryMonth(creditCardExpiryMonth);

		return createBooking(b);
	}

	public Collection<Hotel> searchHotels(final String query, final double maxPrice) {
		ResponseEntity<HotelList> listOfHotels = restTemplate.getForEntity(
				urlForPath("/ws/hotels/search/?q={q}&price={p}"),
				HotelList.class,
				MapBuilder.<String, Object>map().param("q", query).param("p", maxPrice).build()
		);
		return extractResponse(listOfHotels).getHotels();
	}

	@Override	 /// "/users/login"
	public User login(String u, String p) {
		return extractResponse(restTemplate.postForEntity(urlForPath("/ws/users/login"), new User(u, p), User.class));
	}

	@Override
	public Hotel getHotelById(long id) {
		return extractResponse(
				restTemplate.getForEntity(urlForPath("/ws/hotel/{id}"), Hotel.class,
						MapBuilder.<String, Object>map().param("id", id).build()));
	}

	@Override
	public void cancelConfirmation(String user, long bookingId) {
		String httpDeleteUrl = "/bookings/{user}/{bookingId}";
		Map<String, Object> map = MapBuilder.<String, Object>map().param("user", user).param("bookingId", bookingId).build();
		this.restTemplate.delete(httpDeleteUrl, map);
	}

	@Override
	public Booking createBooking(Booking booking) {
		String postCreateBookingUrl = urlForPath("/ws/bookings/{user}");
		return extractResponse(restTemplate.postForEntity(postCreateBookingUrl,
				booking, Booking.class,
				MapBuilder.<String, Object>map().param("user", booking.getUser().getUsername())));
	}

	@Override
	public User getUserById(String usr) {
		return extractResponse(
				restTemplate.getForEntity(urlForPath("/ws/user/{id}"), User.class,
						MapBuilder.<String, Object>map().param("id", usr).build()));
	}

	public Collection<Booking> getUserBookings(final String user) {
		ResponseEntity<BookingList> listOfBookingsEntity =
				restTemplate.getForEntity(
						urlForPath("/ws/bookings/{user}"),
						BookingList.class,
						MapBuilder.<String, Object>map().param("user", user).build());
		return extractResponse(listOfBookingsEntity).getBookings();
	}

	private String urlForPath(final String p) {
		String inputPath = p;
		if (inputPath.startsWith("/"))
			inputPath = inputPath.substring(1); // redundant since the base already ends with a '/'
		return this.baseServiceUrl + inputPath;
	}

	private <T> T extractResponse(final ResponseEntity<T> tResponseEntity) {
		if (tResponseEntity != null && tResponseEntity.getStatusCode().value() == 200) {
			return tResponseEntity.getBody();
		}
		throw new RuntimeException("couldn't extract response.");
	}
}
