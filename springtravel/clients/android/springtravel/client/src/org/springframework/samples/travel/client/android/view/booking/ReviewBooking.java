package org.springframework.samples.travel.client.android.view.booking;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import org.springframework.samples.travel.client.android.R;
import org.springframework.samples.travel.client.android.Utils;
import org.springframework.samples.travel.client.android.domain.Amenity;
import org.springframework.samples.travel.client.android.domain.Booking;
import org.springframework.samples.travel.client.android.services.BookingService;
import org.springframework.samples.travel.client.android.view.UserHome;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Give the user a chance to review selections and go back to make changes.
 *
 * @author Josh Long
 */

public class ReviewBooking extends AbstractBookingView {

	/// update these on form display as theyll be rendered in the table
	private Executor executor = Executors.newSingleThreadExecutor();
	private TextView ccExpiryTextView, checkinDateTextView, checkoutDateTextView;

	private TextView smokingTextView, ccNameTextView, ccNumberTextView, amenitiesTextView, bedsView;

	private DateFormat dateFormat = Utils.buildDefaultDateFormat();

	private Booking bookingToDisplay;

	public ReviewBooking(Context context) {
		super(context);
	}

	public ReviewBooking(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private final Handler submitBookingHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			submissionProgressDialog.dismiss();
			Intent intent = new Intent(getContext(), UserHome.class);
			getContext().startActivity(intent);
		}
	};

	/**
	 * used to notify the user that we're submitting the new reservation and that we'll be back shortly...
	 */
	private ProgressDialog submissionProgressDialog;

	private class SubmitBookingRunnable implements Runnable {
		// the booking we want to submit
		private Booking booking;

		private BookingService bookingService;

		public SubmitBookingRunnable(BookingService bookingService, Booking booking) {
			this.bookingService = bookingService;
			this.booking = booking;
		}

		@Override
		public void run() {
			this.bookingService.createBooking(this.booking);
			submitBookingHandler.sendEmptyMessage(0); /// tell the handler we're done an that it can now send us to the first page or, ultimately, the 'user bookings' page
		}
	}

	@Override
	public boolean onPageChange(Context c, boolean forward) {

		if (forward) {
			ProgressDialog progressDialog = ProgressDialog.show(c, c.getString(R.string.working), c.getString(R.string.working), true, false);
			this.submissionProgressDialog = progressDialog;
			progressDialog.show();
			executor.execute(new SubmitBookingRunnable(Utils.springTravelApplication(c).getBookingService(), this.bookingToDisplay));
			return false;
		}
		return true; // ie, u can always go back and review, but u cant go any more 'forward' than this screen
	}

	@Override
	protected void onSynchronizeBookingData(Booking b) {
	}

	private TableLayout tableLayoutFromMap(Context ctx, Map<String, TextView> keysAndVals) {
		TableLayout amenitiesLayout = new TableLayout(ctx);
		amenitiesLayout.setColumnStretchable(0, false);
		amenitiesLayout.setColumnShrinkable(1, true);

		for (String k : keysAndVals.keySet()) {
			TextView keyTextView = new TextView(ctx);
			String key = k.trim();
			if (!key.endsWith(":"))
				key = key + ":";
			keyTextView.setText(key);
			keyTextView.setTextAppearance(ctx, R.style.bt);
			keyTextView.setGravity(Gravity.RIGHT);
			keyTextView.setPadding(0, 0, 10, 0);

			TextView valueTxtView = (keysAndVals.get(k));
			valueTxtView.setTextAppearance(ctx, R.style.bt);

			TableRow tableRow = new TableRow(ctx);
			TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(0, 0, 0, 0);
			tableRow.addView(keyTextView, layoutParams);
			tableRow.addView(valueTxtView);

			amenitiesLayout.addView(tableRow, layoutParams);
		}

		return amenitiesLayout;
	}

	@Override
	protected void onActivation(Context c) {
		Booking b = this.bookingToDisplay;

		StringBuilder stringBuilder = new StringBuilder();

		if (b.getAmenities() != null) {
			for (Amenity amenity : b.getAmenities()) {
				stringBuilder.append(Utils.lookupStringResourceForKey(c, amenity.name())).append(", ");
			}
		}

		String amenitiesString = stringBuilder.toString().trim();
		if (amenitiesString.endsWith(",")) {
			amenitiesString = amenitiesString.substring(0, amenitiesString.length() - 1);
		}

		// room extras

		String bedWithCardinality = c.getString(b.getBeds() > 1 ? R.string.beds : R.string.bed);
		bedsView.setText(b.getBeds() + " " + bedWithCardinality);
		amenitiesTextView.setText(amenitiesString);
		smokingTextView.setText(c.getString(b.isSmoking() ? R.string.yes : R.string.no));
		// dates
		String ciStr = null == b.getCheckinDate() ? "" : dateFormat.format(b.getCheckinDate());
		String coStr = null == b.getCheckoutDate() ? "" : dateFormat.format(b.getCheckoutDate());
		checkinDateTextView.setText(ciStr);
		checkoutDateTextView.setText(coStr);

		// credit card information
		ccNameTextView.setText(b.getCreditCardName());
		ccNumberTextView.setText(b.getCreditCard());
		ccExpiryTextView.setText((1 + b.getCreditCardExpiryMonth()) + "/" + b.getCreditCardExpiryYear());
	}

	@Override
	public void onInit(Context ctx, Booking b) {
		bookingToDisplay = b;
	}

	@Override
	public void onCreate(Context c) {
		ccExpiryTextView = new TextView(c);
		ccNameTextView = new TextView(c);
		ccNumberTextView = new TextView(c);
		amenitiesTextView = new TextView(c);
		// the labels that will
		checkinDateTextView = new TextView(c);
		checkoutDateTextView = new TextView(c);
		smokingTextView = new TextView(c);
		bedsView = new TextView(c);

		Map<String, TextView> datesValuesMaps = new HashMap<String, TextView>();
		datesValuesMaps.put(c.getString(R.string.checkin_label), checkinDateTextView);
		datesValuesMaps.put(c.getString(R.string.checkout_label), checkoutDateTextView);
		TableLayout tableLayout = tableLayoutFromMap(c, datesValuesMaps);

		Map<String, TextView> stayTableMap = new HashMap<String, TextView>();
		stayTableMap.put(c.getString(R.string.review_beds), this.bedsView);
		stayTableMap.put(c.getString(R.string.review_amenities), this.amenitiesTextView);
		TableLayout stayTableLayout = tableLayoutFromMap(c, stayTableMap);

		Map<String, TextView> ccViewMap = new HashMap<String, TextView>();
		ccViewMap.put(c.getString(R.string.review_cc_name), this.ccNameTextView);
		ccViewMap.put(c.getString(R.string.review_cc_number), this.ccNumberTextView);
		ccViewMap.put(c.getString(R.string.review_expiration), ccExpiryTextView);
		TableLayout ccTableLayout = tableLayoutFromMap(c, ccViewMap);


		LinearLayout linearLayout = new LinearLayout(c);
		linearLayout.setOrientation(LinearLayout.VERTICAL);

		TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, 9, 0, 0);
		linearLayout.addView(Utils.buildLabelAndField(c, c.getString(R.string.dates), tableLayout), layoutParams);
		linearLayout.addView(Utils.buildLabelAndField(c, c.getString(R.string.cc_review), ccTableLayout), layoutParams);
		linearLayout.addView(Utils.buildLabelAndField(c, c.getString(R.string.room_details), stayTableLayout), layoutParams);

		addView(linearLayout, layoutParams);
	}

	@Override
	public String getViewName() {
		return "review";
	}

	@Override
	public int getPosition() {
		return 3;
	}
}
