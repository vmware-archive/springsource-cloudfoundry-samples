package org.springframework.samples.travel.client.android.view;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import org.springframework.samples.travel.client.android.R;
import org.springframework.samples.travel.client.android.Utils;
import org.springframework.samples.travel.client.android.domain.Booking;
import org.springframework.samples.travel.client.android.domain.Hotel;
import org.springframework.samples.travel.client.android.domain.User;
import org.springframework.samples.travel.client.android.services.BookingService;
import org.springframework.util.Assert;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Simple activity that displays a users details as well as the bookings they've got.
 *
 * @author Josh Long
 */
public class UserHome extends ListActivity {
	private DateFormat dateFormat = Utils.buildDefaultDateFormat();
	private List<Booking> bookings = new ArrayList<Booking>();
	private TextView textView, empty;
	private User user;
	private BookingService bookingService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.bookingService = Utils.springTravelApplication(this).getBookingService();

		this.user = Utils.springTravelApplication(this).getLoggedInUser();

		setContentView(R.layout.user_home);

		// the salutation label
		textView = Utils.viewById(this, R.id.welcome);

		// the list
		empty = Utils.viewById(this, R.id.empty);

		final ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_NONE); // view only
		listView.setEmptyView(this.empty);
	}

	private ProgressDialog progressDialog;

	// when results come in, this is triggered to display the results
	private Handler bookingsHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
			if (bookings == null || bookings.size() == 0) {
				empty.setText(getString(R.string.empty));
			} else {
				setListAdapter(new BookingListAdapter(UserHome.this, bookings));
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		Utils.installStandardMenus(this, menu);
		return true;
	}

	// load the bookings asynchronously
	private class LoadUserBookings implements Runnable {

		private BookingService bookingService;
		private User user;

		public LoadUserBookings(BookingService bookingService, User user) {
			this.bookingService = bookingService;
			this.user = user;
		}

		@Override
		public void run() {
			bookings = new ArrayList<Booking>(bookingService.getUserBookings(this.user.getUsername()));
			bookingsHandler.sendEmptyMessage(0);// tell the list adapter to repaint
		}
	}

	private Executor executor = Executors.newSingleThreadExecutor();

	@Override
	protected void onResume() {
		super.onResume();
		this.user = Utils.springTravelApplication(this).getLoggedInUser();
		this.textView.setText(getString(R.string.hello_user) + " " + this.user.getName());
		this.progressDialog = ProgressDialog.show(this, getString(R.string.working), getString(R.string.retrieving_results), true, false);

		executor.execute(new LoadUserBookings(bookingService, user));
	}

	private class BookingListAdapter extends BaseAdapter {

		private List<Booking> bookings;
		private Context context;

		public BookingListAdapter(Activity ctx, Collection<Booking> h) {
			this.context = ctx;
			this.bookings = new ArrayList<Booking>(h);
			Assert.notNull(this.context, "the context can't be null");
			Assert.notEmpty(h, "there need to be results to display");
		}

		@Override
		public int getCount() {
			return this.bookings.size();
		}

		@Override
		public Object getItem(int position) {
			return bookings.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Booking h = this.bookings.get(position);
			return new BookingView(this.context, position, h);
		}
	}

// this is the renderer used for each row.

	class BookingView extends LinearLayout {

		private Booking booking;

		public BookingView(final Context ctx, int rowNo, final Booking b) {
			super(ctx);
			this.booking = b;

			setOrientation(LinearLayout.HORIZONTAL); // irrelevant, theres only one

			// left column is the number
			TextView leftCol = new TextView(ctx);
			leftCol.setText("#" + (rowNo+1));// the number given is 0-indexed
			leftCol.setTextAppearance(ctx, R.style.bt);
			leftCol.setGravity(Gravity.TOP);
			leftCol.setPadding(0, 5, 5, 0);

			/// right column is a 2 cell vertical LL

			LinearLayout rightCol = new LinearLayout(ctx);
			rightCol.setOrientation(LinearLayout.VERTICAL);

			// right column: hotel
			Hotel h = this.booking.getHotel();
			LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			llParams.setMargins(0, 0, 0, 0);
			HotelView hotelView = new HotelView(ctx, h, false, llParams);

			// right column: dates
			TextView datesView = new TextView(ctx);
			datesView.setTextAppearance(ctx, R.style.bt);
			datesView.setText(dateFormat.format(b.getCheckinDate()) + " - " + dateFormat.format(b.getCheckoutDate()));

			rightCol.addView(hotelView);
			rightCol.addView(datesView);

			addView(leftCol);
			addView(rightCol);
		}
	}
}
