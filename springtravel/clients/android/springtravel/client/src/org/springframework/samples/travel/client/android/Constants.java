package org.springframework.samples.travel.client.android;

/**
 * Place to stash the constants for things like action names
 *
 * @author Josh Long
 */
public class Constants {

	/**
	 * Used when dispatching from the {@link org.springframework.samples.travel.client.android.view.HotelSearch}
	 * page to the activity that shows the results of the search, {@link org.springframework.samples.travel.client.android.view.HotelSearchResults}
	 */
	public final static String ACTION_SHOW_HOTEL_LIST = "SHOW_HOTEL_LIST";

	/**
	 * Maps to the {@link org.springframework.samples.travel.client.android.view.BookHotel} activity
	 */
	public final static String ACTION_BOOK_HOTEL = "BOOK_HOTEL";

	/**
	 * Maps to the {@link org.springframework.samples.travel.client.android.view.SignIn} activity
	 */
	public final static String ACTION_SIGN_IN = "SIGN_IN";
}
