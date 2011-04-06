package org.springframework.samples.travel.client.android.view.booking;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import org.springframework.samples.travel.client.android.R;
import org.springframework.samples.travel.client.android.Utils;
import org.springframework.samples.travel.client.android.domain.Booking;
import org.springframework.samples.travel.client.android.domain.Hotel;
import org.springframework.samples.travel.client.android.services.BookingService;
import org.springframework.samples.travel.client.android.view.HotelView;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * This is the host activity
 *
 * @author Josh Long
 */
public class BookHotel extends Activity {
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		Utils.installStandardMenus(this, menu);

		return true;
	}

	protected Comparator<BookingView> viewComparator = new Comparator<BookingView>() {
		@Override
		public int compare(BookingView bookingView, BookingView bookingView1) {
			return bookingView.getPosition() - bookingView1.getPosition();
		}
	};
	private final View.OnClickListener nextButtonOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			displayNextViewGroup();
		}
	};
	private final View.OnClickListener backButtonOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			displayPreviousViewGroup();
		}
	};

	private ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	private String TAG = getClass().getName();
	private String currentState = null;
	private List<String> validStates = new ArrayList<String>();
	private String lastState;
	private Map<String, AbstractBookingView> viewGroups = new HashMap<String, AbstractBookingView>();
	private Booking booking;
	private BookingService bookingService;
	private AbstractBookingView creditCardVewGroup, details, dates, review;
	private Hotel hotel;
	private HotelView hotelView;
	private LinearLayout layout;

	private Button previousButton, nextButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		hotel = null;
		booking = new Booking();
		bookingService = Utils.springTravelApplication(this).getBookingService();

		// our forms
		dates = new StayDates(this);
		hotelView = new HotelView(this);
		creditCardVewGroup = new EnterCreditCardDetails(this);
		review = new ReviewBooking(this);

		details = new RoomDetails(this);
		layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setVerticalScrollBarEnabled( true );
		layout.setHorizontalScrollBarEnabled( true );
		layout.setHorizontalFadingEdgeEnabled( true );
		layout.setVerticalFadingEdgeEnabled( true );

		ScrollView scrollView = new ScrollView( this ) ;
		scrollView.addView( layout);
		setContentView(scrollView , Utils.buildLayoutParameters(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));



		nextButton = new Button(this);
		nextButton.setText(R.string.forward);
		nextButton.setOnClickListener(this.nextButtonOnClickListener);

		previousButton = new Button(this);
		previousButton.setText(R.string.backward);
		previousButton.setOnClickListener(this.backButtonOnClickListener);

		viewGroups.put(details.getViewName(), details);
		viewGroups.put(creditCardVewGroup.getViewName(), creditCardVewGroup);
		viewGroups.put(dates.getViewName(), dates);
		viewGroups.put(review.getViewName(), review);

		validStates.clear();

		ArrayList<AbstractBookingView> abv = new ArrayList<AbstractBookingView>();

		abv.addAll(viewGroups.values());
		Collections.sort(abv, viewComparator);

		for (AbstractBookingView v : abv) {
			validStates.add(v.getViewName());
		}

		doLayout(this);

		displayNextViewGroup();
	}


	protected void doLayout(Context c) {

		layout.addView(hotelView, layoutParams);

		for (String vs : validStates) {
			AbstractBookingView vg = viewGroups.get(vs);
			vg.onCreate(c);
			layout.addView(vg, layoutParams);
		}

		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		linearLayout.setGravity(Gravity.BOTTOM);

		for (Button b : new Button[]{previousButton, nextButton}) {
			linearLayout.addView(b, Utils.buildLayoutParameters(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			b.setGravity(Gravity.RIGHT);
		}

		layout.addView(linearLayout, Utils.buildLayoutParameters(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
	}

	private String previousState() {

		// nothings been selected yet
		if (!StringUtils.hasText(currentState)) {

			return validStates.get(0);
		}

		int pos = validStates.indexOf(currentState);
		if (pos == 0) {
			pos = validStates.size() - 1;
		} else pos += -1;
		return validStates.get(pos);
	}

	private String nextState() {

		// just starting the wizard
		if (!StringUtils.hasText(currentState)) {
			return validStates.get(0);
		}

		int pos = validStates.indexOf(currentState);

		if (pos == (validStates.size() - 1)) {
			pos = 0;
		} else {
			pos += 1;
		}

		return validStates.get(pos);
	}

	private void hideAllViewGroups() {
		for (ViewGroup cvg : this.viewGroups.values()) {
			cvg.setVisibility(View.GONE);		// hide them all
		}
	}

	private void dispatch(String state, boolean forward) {

		boolean canContinue = true;
		if (StringUtils.hasText(currentState)) {
			if (!viewGroups.get(currentState).onPageChange(this, forward)) {
				canContinue = false; // ie, if the onPageChange handler tells us to stay put, we do
			}
		}
		if (canContinue) {
			lastState = currentState;
			hideAllViewGroups();
			currentState = state;
			viewGroups.get(currentState).onActivation(this);
			viewGroups.get(currentState).setVisibility(View.VISIBLE);
		}
	}

	private void displayNextViewGroup() {
		dispatch(nextState(), true);
	}

	private void displayPreviousViewGroup() {
		dispatch(previousState(), false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.hotel = Utils.springTravelApplication(this).getSelectedHotel();
		this.booking = new Booking();
		this.booking.setHotel(this.hotel);
		this.booking.setUser(Utils.springTravelApplication(this).getLoggedInUser());
		this.hotelView.setHotel(this.hotel);

		for (AbstractBookingView view : this.viewGroups.values())
			view.doOnInit(this, this.booking);

		Log.d(TAG, "the onResume method inside BookHotel.");
	}
}
