package org.springframework.samples.travel.client.android.view.booking;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import org.springframework.samples.travel.client.android.Utils;
import org.springframework.samples.travel.client.android.domain.Booking;
import org.springframework.samples.travel.client.android.view.validation.DefaultFormValidator;
import org.springframework.samples.travel.client.android.view.validation.FormValidator;
import org.springframework.samples.travel.client.android.view.validation.MessageFeedback;

import java.util.Collection;

abstract public class AbstractBookingView extends LinearLayout implements BookingView {

	private Booking booking;

	protected String TAG = getClass().getName();

	private FormValidator formValidator;

	public FormValidator getFormValidator() {
		return formValidator;
	}

	protected String buildFeedbackMessage(Collection<MessageFeedback<?>> feedback) {
		StringBuffer msgToDisplay = new StringBuffer();
		for (MessageFeedback<?> m : feedback) {
			msgToDisplay.append(m.getMessage() + System.getProperty("line.separator"));
		}

		return msgToDisplay.toString();
	}

	public void showValidationFeedbackDialog() {
		if (formValidator.hasFeedbackOrErrors()) {
			AlertDialog alertDialog = Utils.buildFeedbackAlertDialog(this.getContext(), formValidator);
			if (null != alertDialog)
				alertDialog.show();
		}
	}

	/**
	 * override if u have certain callback logic to be executed when the 'next' or 'previous' buttons are hit.
	 * <p/>
	 * Pages should only continue if this method returns true. False indicates that something has vetoed changing pages.
	 */
	public boolean onPageChange(Context c, boolean forward) {
		try {
			this.formValidator.validate(c);
			boolean errors = this.formValidator.hasFeedbackOrErrors();
			if (errors) showValidationFeedbackDialog();
			return !errors;
		} finally {
			formValidator.reset();
		}
	}

	public AbstractBookingView(Context context) {
		super(context);
		localInit(context);
	}

	private void localInit(Context c) {
		this.formValidator = new DefaultFormValidator();

		setOrientation(LinearLayout.VERTICAL);
	}

	/**
	 * implement if u want a callback when the component is shown in the activity
	 */
	protected void onActivation(Context c) {
	}

	public AbstractBookingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		localInit(context);
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}

	/**
	 * users should implement any logic to bind the UI state to the backing {@link Booking} object here.
	 *
	 * @param b the booking object to which changes should be made. At the moment, the passed in parameter is the same reference as the local {@link #booking}, but that could change, so make your edits against this.
	 */
	protected abstract void onSynchronizeBookingData(Booking b);

	/**
	 * Used from event handlers to trigger any data binding
	 */
	protected void triggerSynchronization() {
		onSynchronizeBookingData(this.booking);
		//	Log.d(TAG, "the revised booking is: " + this.booking.toString());
	}

	public void doOnInit(Context ctx, Booking booking) {
		setBooking(booking);
		onInit(ctx, this.booking);
	}
}
