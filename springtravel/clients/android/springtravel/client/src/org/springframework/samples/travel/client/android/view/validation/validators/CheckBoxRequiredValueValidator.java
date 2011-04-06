package org.springframework.samples.travel.client.android.view.validation.validators;

import android.widget.CheckBox;
import org.springframework.samples.travel.client.android.view.validation.MessageContext;

/**
 * @author Josh Long
 */
public class CheckBoxRequiredValueValidator extends AbstractValidatorRule<CheckBox> {

	@Override
	public void validate(CheckBox checkBox, MessageContext mc) {
		if (!checkBox.isChecked())
			mc.registerFieldError(checkBox, getFormattedFeedbackMessageString(checkBox.getContext()));
	}
}
