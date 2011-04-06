package org.springframework.samples.travel.client.android.view.validation;

import android.view.View;

/**
 * Represents a rule as provided by the programmer to specifically handle a single widget error.
 *
 * @author Josh Long
 */
public interface FormValidatorRule<T extends View> {
	void validate(T t, MessageContext mc);
}
