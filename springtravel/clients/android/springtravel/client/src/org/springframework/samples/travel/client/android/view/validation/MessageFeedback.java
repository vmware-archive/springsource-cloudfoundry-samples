package org.springframework.samples.travel.client.android.view.validation;

import android.view.View;

/**
 * place in which to store the feedback. users don't typically create these themselves. Invoking {@link MessageContext#registerFieldError(android.view.View, int, Object...)},
 * ultimately creates these and registers them with the context.
 *
 * @author Josh Long
 */
public class MessageFeedback<T extends View> {
	private T widget;
	private String message;

	private boolean error = false;

	public boolean isError() {
		return error;
	}

	public MessageFeedback(T widget, String message, boolean err) {
		this.widget = widget;
		this.message = message;
		this.error = err;
	}

	public T getWidget() {
		return widget;
	}

	public String getMessage() {
		return message;
	}
}
