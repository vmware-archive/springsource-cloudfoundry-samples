package org.springframework.samples.travel.client.android.view.booking;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import org.springframework.samples.travel.client.android.R;
import org.springframework.samples.travel.client.android.Utils;
import org.springframework.samples.travel.client.android.domain.Booking;
import org.springframework.samples.travel.client.android.view.validation.FormValidatorRule;
import org.springframework.samples.travel.client.android.view.validation.MessageContext;

import java.util.Date;

/**
 * User specifies from which date to which date the stay will be
 *
 * @author Josh Long
 */
public class StayDates extends AbstractBookingView {
	public StayDates(Context context) {
		super(context);
	}

	private Date checkinDate = new Date(), checkoutDate = new Date(checkinDate.getTime());
	private DatePicker checkinDatePicker;
	private DatePicker checkoutDatePicker;

	public StayDates(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onPageChange(Context c, boolean fw) {
		triggerSynchronization();
		return super.onPageChange(c, fw);
	}

	@Override
	protected void onSynchronizeBookingData(Booking b) {
		b.setCheckinDate(checkinDate);
		b.setCheckoutDate(checkoutDate);
	}

	private final DatePicker.OnDateChangedListener onDateChangedListener = new DatePicker.OnDateChangedListener() {
		@Override
		public void onDateChanged(DatePicker datePicker, int y, int m, int d) {
			if (datePicker == checkinDatePicker) {
				checkinDate = Utils.dateFromPrincipalIntegers(y, m, d);
			}
			if (datePicker == checkoutDatePicker) {
				checkoutDate = Utils.dateFromPrincipalIntegers(y, m, d);
			}
			triggerSynchronization();
		}
	};

	@Override
	public void onInit(Context c, Booking b) {
	}

	@Override
	public void onCreate(Context ctx) {

		LinearLayout linearLayout = new LinearLayout(ctx);
		linearLayout.setOrientation(LinearLayout.VERTICAL);

		checkinDatePicker = new DatePicker(ctx);
		checkoutDatePicker = new DatePicker(ctx);

		Utils.configureDatePicker(checkinDatePicker, checkinDate, onDateChangedListener);
		Utils.configureDatePicker(checkoutDatePicker, checkoutDate, onDateChangedListener);

		linearLayout.addView(Utils.buildLabelAndField(ctx, ctx.getString(R.string.checkin_label), checkinDatePicker));
		linearLayout.addView(Utils.buildLabelAndField(ctx, ctx.getString(R.string.checkout_label), checkoutDatePicker));

		addView(linearLayout);

		getFormValidator().addValidationRule(this.checkinDatePicker, new FormValidatorRule<DatePicker>() {
			@Override
			public void validate(DatePicker datePicker, MessageContext mc) {
				if (checkinDate.getTime() == checkoutDate.getTime() || checkinDate.after(checkoutDate))
					mc.registerFieldError(checkinDatePicker, R.string.checkin_date_should_be_before_checkout);
			}
		});
	}

	@Override
	public String getViewName() {
		return "stayDates";
	}

	@Override
	public int getPosition() {
		return 0;
	}
}
