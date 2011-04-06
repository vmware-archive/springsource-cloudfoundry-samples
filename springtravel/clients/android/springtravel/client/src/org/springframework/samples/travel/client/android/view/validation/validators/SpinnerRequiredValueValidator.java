package org.springframework.samples.travel.client.android.view.validation.validators;

import android.widget.Spinner;
import org.springframework.samples.travel.client.android.view.validation.MessageContext;

/**
 * @author Josh Long
 */
public class SpinnerRequiredValueValidator extends AbstractValidatorRule<Spinner> {

	@Override
	public void validate(Spinner spinner, MessageContext mc) {
		if (spinner.getSelectedItem() == null)
			mc.registerFieldError(spinner, getFormattedFeedbackMessageString(spinner.getContext()));
	}
}
