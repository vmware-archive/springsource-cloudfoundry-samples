package org.springframework.samples.travel.client.android.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import org.springframework.samples.travel.client.android.Constants;
import org.springframework.samples.travel.client.android.R;
import org.springframework.samples.travel.client.android.Utils;
import org.springframework.samples.travel.client.android.view.validation.DefaultFormValidator;
import org.springframework.samples.travel.client.android.view.validation.FormValidator;
import org.springframework.samples.travel.client.android.view.validation.FormValidatorRule;
import org.springframework.samples.travel.client.android.view.validation.MessageContext;
import org.springframework.samples.travel.client.android.view.validation.validators.EditTextRequiredValueValidator;
import org.springframework.util.StringUtils;

/**
 * Simple activity to searchHotels for Hotels using the Spring Travel webservice.
 * <p/>
 * todo setup the icon for the dialog
 * todo setup the background image
 *
 * @author Josh Long
 */
public class HotelSearch extends Activity {

	private LinearLayout linearLayout;

	private FormValidator formValidator;

	private final int MENU_SEARCH_HOTELS = Menu.FIRST;

	private final String TAG = getClass().getName();

	private EditText queryTextField, priceTextField;

	private boolean validate() {

		formValidator.validate(this);
		try {

			if (formValidator.hasFeedbackOrErrors()) {
				AlertDialog alertDialog = Utils.buildFeedbackAlertDialog(this, formValidator);
				if (null != alertDialog)
					alertDialog.show();

				return false;
			}
			return true;
		} finally {
			formValidator.reset();
		}
	}

	private void doPerformSearch() {
		if (validate()) {
			String query = Utils.stringValueFor(queryTextField);
			String maxPriceString = Utils.stringValueFor(priceTextField);
			Double valueForPrice = StringUtils.hasText(maxPriceString) ? Double.parseDouble(maxPriceString) : 0;
			performSearch(query, valueForPrice);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		Utils.installStandardMenus(this, menu);

		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hotel_search);

		linearLayout = Utils.viewById(this, R.id.search_form);

		queryTextField = Utils.viewById(this, R.id.edit_search_query);
		priceTextField = Utils.viewById(this, R.id.edit_search_price);

		linearLayout.addView(Utils.buildLabelAndField(this, getString(R.string.search_query_label), queryTextField));
		linearLayout.addView(Utils.buildLabelAndField(this, getString(R.string.search_price_label), priceTextField));

		Utils.viewById(this, R.id.hotel_search).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				doPerformSearch();
			}
		});

		formValidator = new DefaultFormValidator();

		EditTextRequiredValueValidator etrvv = new EditTextRequiredValueValidator();
		etrvv.setFeedbackMessage(R.string.query_invalid_msg);

		formValidator.addValidationRule(this.queryTextField, etrvv);
		formValidator.addValidationRule(this.priceTextField, new FormValidatorRule<EditText>() {
			@Override
			public void validate(EditText editText, MessageContext mc) {
				// so, first, this fields optional
				String valueForPrice = Utils.stringValueFor(editText);
				if (StringUtils.hasText(valueForPrice)) { // otherwise we dont care since we have a default value in play
					valueForPrice = valueForPrice.trim();
					Double aDouble = null;
					try {
						aDouble = Double.parseDouble(valueForPrice);
					} catch (Throwable x) {
						/* dont care */
					}
					if (!(aDouble != null && aDouble > 0.0))
						mc.registerFieldError(editText, R.string.price_invalid);
				}
			}
		});
	}

	private void performSearch(String query, double price) {
		Utils.springTravelApplication(this).updateSearchCriteria(query, price);
		Intent intent = new Intent(Constants.ACTION_SHOW_HOTEL_LIST);
		startActivity(intent);
	}
}
