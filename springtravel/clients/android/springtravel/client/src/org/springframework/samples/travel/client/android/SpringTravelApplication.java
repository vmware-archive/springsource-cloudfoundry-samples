package org.springframework.samples.travel.client.android;

import android.content.Context;
import org.springframework.samples.travel.client.android.domain.Hotel;
import org.springframework.samples.travel.client.android.domain.User;
import org.springframework.samples.travel.client.android.services.BookingService;
import org.springframework.samples.travel.client.android.services.RestfulBookingServiceClient;
import org.springframework.samples.travel.client.android.view.SearchCriteria;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Place to stash state between searches
 *
 * @author Josh Long
 */
public class SpringTravelApplication {

	// bleargh i wish i had full IoC here...
	private static final AtomicReference<SpringTravelApplication> instance = new AtomicReference<SpringTravelApplication>();

	public static SpringTravelApplication getInstance(Context c) {
		instance.compareAndSet(null, new SpringTravelApplication(c.getString(R.string.base_uri)));
		return instance.get();
	}

	private Hotel currentlySelectedHotel;
	private User loggedInUser;
	private BookingService bookingServiceClient;
	private SearchCriteria criteria;

	public synchronized void updateSearchCriteria(String q, double mp) {
		this.criteria = new SearchCriteria(q, mp);
	}

	public SearchCriteria getSearchCriteria() {
		return criteria;
	}

	public SpringTravelApplication(String baseUrl) {
		//context.getString(R.string.base_uri)
		bookingServiceClient = new RestfulBookingServiceClient(baseUrl);
	}

	public boolean isUserLoggedIn() {
		return getLoggedInUser() != null;
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}

	public synchronized void setSelectedHotel(Hotel h) {
		this.currentlySelectedHotel = h;
	}

	public Hotel getSelectedHotel() {
		return this.currentlySelectedHotel;
	}

	public void login(User u) {
		loggedInUser = this.bookingServiceClient.getUserById(u.getUsername());
	}
/*
	public void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
	}*/

	public BookingService getBookingService() {
		return this.bookingServiceClient;
	}
}
