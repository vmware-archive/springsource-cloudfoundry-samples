package org.springframework.samples.travel.client.android.view.validation.validators;

import android.widget.EditText;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.samples.travel.client.android.Utils;
import org.springframework.samples.travel.client.android.view.validation.MessageContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author Josh Long
 */
public class EditTextValueLengthValidator extends AbstractValidatorRule<EditText> implements InitializingBean {

	private int minimumLength = -1, maximumLength = -1;

	@Override
	public void validate(EditText editText, MessageContext mc) {
		String value = Utils.stringValueFor(editText);
		if (StringUtils.hasText(value)) {
			if (value.length() < minimumLength)
				mc.registerFieldError(editText, getFormattedFeedbackMessageString(editText.getContext()));
		}
	}

	@Override
	public void afterPropertiesSet() {
		if (this.minimumLength != -1)
			Assert.isTrue(this.minimumLength >= 0);

		if (this.maximumLength != -1)
			Assert.isTrue(this.maximumLength >= 0);
	}
}
