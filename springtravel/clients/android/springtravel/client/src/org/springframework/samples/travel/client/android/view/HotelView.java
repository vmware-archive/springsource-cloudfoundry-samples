package org.springframework.samples.travel.client.android.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.springframework.samples.travel.client.android.Constants;
import org.springframework.samples.travel.client.android.R;
import org.springframework.samples.travel.client.android.Utils;
import org.springframework.samples.travel.client.android.domain.Hotel;

/**
 * A component to 'paint' a {@link org.springframework.samples.travel.client.android.domain.Hotel} -- in this case, the component simply renders
 * the relevant text and offers the user a chance to 'book' a hotel.
 *
 * @author Josh Long
 */
public class HotelView extends LinearLayout {

	private LinearLayout.LayoutParams llParams;
	private int margin = 10;
	private Button bookHotel;
	private TextView address1, price, name, address2;

	private boolean showBookButton;

	private String TAG = getClass().getName();

	private Hotel hotel;

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
		drawHotel(this.hotel);
	}

	private void drawHotel(Hotel h) {
		this.address1.setText(h.getAddress());
		String addyLine2 = String.format("%s, %s - %s", h.getCity(), h.getState(), h.getZip());
		this.address2.setText(addyLine2);
		this.name.setText(String.format("%s", h.getName()));
		this.price.setText(String.format(" ($%s)", h.getPrice().toPlainString()));
	}

	private View buildTopLineLabel(Context ctx) {
		LinearLayout linearLayout = new LinearLayout(ctx);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);

		price = (new TextView(ctx));//, 16f, Color.WHITE);
		price.setTextAppearance(ctx, R.style.category1);
		price.setGravity(Gravity.RIGHT);

		name = (new TextView(ctx));//, 16f, Color.WHITE);
		name.setTextAppearance(ctx, R.style.category1);

		LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		linearLayout.addView(name, llParams);
		linearLayout.addView(price, llParams);

		return linearLayout;
	}

	public HotelView(Context context) {
		super(context);
		init(context);
	}

	public HotelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public HotelView(final Context ctx, final Hotel hotel, boolean showBookButton, LinearLayout.LayoutParams llp) {
		super(ctx);
		this.hotel = hotel;
		this.showBookButton = showBookButton;
		this.llParams = llp;

		init(ctx);
	}

	public HotelView(final Context ctx, final Hotel hotel, boolean showBookButton) {
		super(ctx);
		this.hotel = hotel;
		this.showBookButton = showBookButton;

		init(ctx);
	}

	private void init(final Context ctx) {
		 if(llParams == null ){
			 llParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			 llParams.setMargins(0, margin / 5, 0, margin / 5);

		 }
		setOrientation(LinearLayout.VERTICAL);

		addView(buildTopLineLabel(ctx), llParams);

		bookHotel = new Button(ctx);
		bookHotel.setText(ctx.getString(R.string.bookit));
		bookHotel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "you clicked on the 'bookIt!' button for " + hotel.getName());
				Utils.springTravelApplication(v.getContext()).setSelectedHotel(hotel);
				Intent i = Utils.buildSignInAwareIntent(v.getContext(), new Intent(Constants.ACTION_BOOK_HOTEL));
				ctx.startActivity(i);
			}
		});

		address1 = new TextView(ctx);
		address1.setTextAppearance(ctx, R.style.bt);
		addView(address1, llParams);

		address2 = new TextView(ctx);
		address2.setTextAppearance(ctx, R.style.bt);
		addView(address2, llParams);

		if (showBookButton) {
			addView(bookHotel);
		}
		if (hotel != null)
			drawHotel(hotel);
	}
}
