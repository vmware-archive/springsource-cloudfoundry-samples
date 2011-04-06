package org.springframework.samples.travel.client.android.view.booking;

import android.content.Context;
import org.springframework.samples.travel.client.android.domain.Booking;

/**
 * @author Josh Long
 */
public interface BookingView {
	void onInit(Context c, Booking b);

	void onCreate(Context c);

	String getViewName();

	int getPosition();
}
