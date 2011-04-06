package org.springframework.samples.travel.client.android.view.booking;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import org.springframework.samples.travel.client.android.R;
import org.springframework.samples.travel.client.android.Utils;
import org.springframework.samples.travel.client.android.domain.Amenity;
import org.springframework.samples.travel.client.android.domain.Booking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * User enters which amenities should be added to the room, as well as how many beds.
 *
 * @author Josh Long
 */
public class RoomDetails extends AbstractBookingView {
	private int beds = 1;
	private Set<Amenity> amenities = new HashSet<Amenity>();

	public RoomDetails(Context context) {
		super(context);
	}

	public RoomDetails(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private TableRow buildAmenityTableRow(Context ctx, Amenity amenity) {
		TextView textView = new TextView(ctx);
		String amName = amenity.name();
		int id = getResources().getIdentifier(ctx.getPackageName() + ":string/" + amName, null, null);
		String value = ctx.getString(id);
		textView.setText(value);
		textView.setGravity(Gravity.RIGHT);
		CheckBox checkBox = new CheckBox(ctx);
		checkBox.setChecked(false);
		checkBox.setOnCheckedChangeListener(new AmenityOnCheckChangeListener(this.amenities, amenity));
		TableRow tableRow = new TableRow(ctx);
		TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
		layoutParams.setMargins(0, 0, 10, 0);
		tableRow.addView(textView, layoutParams);
		tableRow.addView(checkBox);
		return tableRow;
	}

	@Override
	protected void onSynchronizeBookingData(Booking booking) {
		booking.setBeds(this.beds);
		booking.setSmoking(this.amenities.contains(Amenity.SMOKING));
		ArrayList<Amenity> ams = new ArrayList<Amenity>(this.amenities);
		ams.remove(Amenity.SMOKING);
		booking.setAmenities(new ArrayList<Amenity>(ams));
	}

	@Override
	public void onInit(Context c, Booking b) {
	}

	@Override
	public void onCreate(Context ctx) {

		LinearLayout linearLayout = new LinearLayout(ctx);
		linearLayout.setOrientation(LinearLayout.VERTICAL);

		// beds
		Spinner spinnerTextField = new Spinner(ctx);
		ArrayAdapter<Integer> aa = new ArrayAdapter<Integer>(ctx, android.R.layout.simple_spinner_item, new Integer[]{1, 2, 3});
		aa.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
		spinnerTextField.setAdapter(aa);
		spinnerTextField.setOnItemSelectedListener(bedCountSpinnerItemSelectionListener);

		// options
		TableLayout amenitiesLayout = new TableLayout(ctx);
		amenitiesLayout.setColumnStretchable(0, false);
		amenitiesLayout.setColumnShrinkable(1, true);
		Amenity[] ams = Amenity.values();
		for (Amenity amenity : ams) {
			TableRow tableRow = buildAmenityTableRow(ctx, amenity);
			amenitiesLayout.addView(tableRow);
		}

		linearLayout.addView(Utils.buildLabelAndField(ctx, ctx.getString(R.string.beds_label), spinnerTextField));
		linearLayout.addView(Utils.buildLabelAndField(ctx, ctx.getString(R.string.amenities_label), amenitiesLayout));

		addView(linearLayout);
	}

	@Override
	public String getViewName() {
		return "details";
	}

	@Override
	public int getPosition() {
		return 1;
	}

	class AmenityOnCheckChangeListener implements CompoundButton.OnCheckedChangeListener {

		private Amenity amenity;
		private Set<Amenity> amenities;

		public AmenityOnCheckChangeListener(Set<Amenity> ams, Amenity amenity) {
			this.amenity = amenity;
			this.amenities = ams;
		}

		@Override
		public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
			boolean alreadyInCollection = amenities.contains(this.amenity);
			if (!b) {
				if (alreadyInCollection)
					amenities.remove(this.amenity);
			} else {
				if (!alreadyInCollection) amenities.add(this.amenity);
			}
			triggerSynchronization();
		}
	}

	private final AdapterView.OnItemSelectedListener bedCountSpinnerItemSelectionListener = new AdapterView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
			beds = (Integer) adapterView.getItemAtPosition(i);
			triggerSynchronization();
		}

		@Override
		public void onNothingSelected(AdapterView<?> adapterView) {
		}
	};
}
