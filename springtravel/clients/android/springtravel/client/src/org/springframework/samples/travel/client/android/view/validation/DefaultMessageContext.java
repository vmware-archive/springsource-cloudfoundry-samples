package org.springframework.samples.travel.client.android.view.validation;

import android.content.Context;
import android.view.View;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This is the default implementation of {@link MessageContext}. It provides access to {@link #errors} and {@link #messages},
 * <p/>
 * User do not need to know about this class since they're provided a reference through {@link FormValidatorRule#validate(android.view.View, MessageContext)}.
 *
 * @author Josh Long
 */
public class DefaultMessageContext implements MessageContext {

	private ConcurrentLinkedQueue<MessageFeedback<?>> errors = new ConcurrentLinkedQueue<MessageFeedback<?>>();
	private ConcurrentLinkedQueue<MessageFeedback<?>> messages = new ConcurrentLinkedQueue<MessageFeedback<?>>();
	private Context context;

	public Collection<MessageFeedback<?>> getErrors() {
		return errors;
	}

	public Collection<MessageFeedback<?>> getMessages() {
		return messages;
	}

	public DefaultMessageContext(Context c) {
		this.context = c;
	}

	@Override
	public void registerFieldMessage(View view, int msgResId, Object... parms) {
		registerFieldMessage(view, this.context.getString(msgResId, parms));
	}

	@Override
	public void registerFieldMessage(View view, String msg, Object... parms) {
		messages.add(new MessageFeedback<View>(view, String.format(msg, parms), false));
	}

	@Override
	public void registerFieldError(View view, int msgResId, Object... parms) {
		registerFieldError(view, context.getString(msgResId), parms);
	}

	@Override
	public void registerFieldError(View view, String msg, Object... parms) {
		errors.add(new MessageFeedback<View>(view, String.format(msg, parms), true));
	}
}
