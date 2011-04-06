package org.springframework.samples.travel.client.android.view.validation.validators;

import android.widget.EditText;
import org.springframework.samples.travel.client.android.Utils;
import org.springframework.samples.travel.client.android.view.validation.MessageContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Simple validator to perform a variety of tests for text fields.
 *
 * @author Josh Long
 */
public class EditTextRequiredValueValidator extends AbstractValidatorRule<EditText> {

	@Override
	public void validate(EditText editText, MessageContext mc) {
		Assert.notNull(editText, "the UI widget subclass must not be null");
		String formValue = Utils.stringValueFor(editText);

		if (!StringUtils.hasText(formValue))
			mc.registerFieldError(editText, getFormattedFeedbackMessageString(editText.getContext()));
	}
}
