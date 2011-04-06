package org.springframework.samples.travel.client.android.view.validation;

import android.view.View;

/**
 * @author Josh Long
 */
public interface MessageContext {
	void registerFieldMessage(View view, int msgResId, Object... parms);

	void registerFieldMessage(View view, String msg, Object... parms);

	void registerFieldError(View view, int msgResId, Object... parms);

	void registerFieldError(View view, String msg, Object... parms);
}
