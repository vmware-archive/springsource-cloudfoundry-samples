package org.springframework.samples.travel.client.android.view.booking;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.DatePicker;
import android.widget.EditText;
import org.springframework.samples.travel.client.android.R;
import org.springframework.samples.travel.client.android.Utils;
import org.springframework.samples.travel.client.android.domain.Booking;
import org.springframework.samples.travel.client.android.view.MonthPicker;
import org.springframework.samples.travel.client.android.view.validation.validators.EditTextRequiredValueValidator;

import java.util.Calendar;
import java.util.Date;

/**
 * Enter credit card information including the credit card number and the name of the credit card holder.
 *
 * @author Josh Long
 */
public class EnterCreditCardDetails extends AbstractBookingView {

	private EditText ccCardNumberView, ccNameView;
	private String ccName, ccCard;
	private Date ccExpirationDate;

	//<EditText android:id="@+id/cc_name" android:layout_width="fill_parent" android:layout_height="wrap_content" android:autoText="false" android:inputType="text" android:singleLine="true" android:lines="1"/>
	private final DatePicker.OnDateChangedListener creditCardExpirationDatePickerOnDateChangeListener = new DatePicker.OnDateChangedListener() {
		@Override
		public void onDateChanged(DatePicker datePicker, int y, int m, int d) {
			ccExpirationDate = Utils.dateFromPrincipalIntegers(y, m, d);
			triggerSynchronization();
		}
	};

	private final TextWatcher creditCardTextChangedFieldWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
		}

		@Override
		public void afterTextChanged(Editable editable) {
			ccName = Utils.stringValueFor(ccNameView);
			ccCard = Utils.stringValueFor(ccCardNumberView);
			triggerSynchronization();
		}
	};

	@Override
	protected void onSynchronizeBookingData(Booking b) {

		Calendar c = Utils.getCalendar();
		c.setTime(ccExpirationDate);
		int ccMon = c.get(Calendar.MONTH);
		int ccYear = c.get(Calendar.YEAR);
		b.setCreditCardName(ccName);
		b.setCreditCard(ccCard);
		b.setCreditCardExpiryMonth(ccMon);
		b.setCreditCardExpiryYear(ccYear);
	}

	@Override
	public void onInit(Context c, Booking b) {

	}

	@Override
	public void onCreate(Context ctx) {
		ccNameView = new EditText(ctx);
		ccCardNumberView = new EditText(ctx);

		MonthPicker ccExpirationDatepicker = new MonthPicker(ctx);
		ccExpirationDate = new Date();
		// credit card expiration date picked

		Calendar c = Utils.getCalendar();
		c.setTime(ccExpirationDate);
		int m = c.get(Calendar.MONTH);
		int y = c.get(Calendar.YEAR);
		int d = c.get(Calendar.DAY_OF_MONTH);
		ccExpirationDatepicker.init(y, m, d, creditCardExpirationDatePickerOnDateChangeListener);

		// credit card changed
		for (EditText editText : new EditText[]{ccNameView, ccCardNumberView}) {
			editText.setInputType( InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE  | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS );
			editText.addTextChangedListener(creditCardTextChangedFieldWatcher);
		}

		addView(Utils.buildLabelAndField(ctx, ctx.getString(R.string.cc_name_label), ccNameView));
		addView(Utils.buildLabelAndField(ctx, ctx.getString(R.string.cc_number_label), ccCardNumberView));
		addView(Utils.buildLabelAndField(ctx, ctx.getString(R.string.cc_expiration_date_label), ccExpirationDatepicker));

		// add some basic validation
		EditTextRequiredValueValidator requiredValueForCreditCard = new EditTextRequiredValueValidator();
		requiredValueForCreditCard.setFeedbackMessage(R.string.cc_number_required);
		this.getFormValidator().addValidationRule(this.ccCardNumberView, requiredValueForCreditCard);

		EditTextRequiredValueValidator requiredValueValidator = new EditTextRequiredValueValidator();
		requiredValueValidator.setFeedbackMessage(R.string.cc_name_required);
		this.getFormValidator().addValidationRule(this.ccNameView, requiredValueValidator);
	}

	@Override
	public String getViewName() {
		return "creditCardDetails";
	}

	@Override
	public int getPosition() {
		return 0;
	}

	public EnterCreditCardDetails(Context context) {
		super(context);
	}

}
