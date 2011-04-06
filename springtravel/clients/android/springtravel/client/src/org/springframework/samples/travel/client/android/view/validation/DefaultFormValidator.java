package org.springframework.samples.travel.client.android.view.validation;

import android.content.Context;
import android.util.Log;
import android.view.View;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Ideally there'd be something pre-canned that I coudl use that woudl make this easier, but there doesn't seem to be so
 * here i am, rolling my own..
 * <p/>
 * This implementation allows you to reuse the same {@link DefaultFormValidator}, so long as you {@link DefaultFormValidator#reset()} before subsequent use.
 * Failure to do so simply accrues the messages.
 *
 * @author Josh Long
 */
public class DefaultFormValidator implements FormValidator {

	// kludge, but then were not likely to have much concurrence. its just that we *might* have some
	private Map<View, Collection<FormValidatorRule>> rules = Collections.synchronizedMap(new HashMap<View, Collection<FormValidatorRule>>());

	private Collection<MessageFeedback<?>> errors = new ArrayList<MessageFeedback<?>>();

	private Collection<MessageFeedback<?>> messages = new ArrayList<MessageFeedback<?>>();

	@Override
	public void reset() {
		this.clearAll();
	}

	public void clearMessages() {
		this.messages.clear();
	}

	public void clearErrors() {
		this.errors.clear();
	}

	// synchronized because the errors/messages collections arent thread safe and this is the only place we use 'em
	@Override
	@SuppressWarnings("unchecked")
	public synchronized void validate(Context c) {
		for (View v : this.rules.keySet()) {

			Collection<FormValidatorRule> rules = this.rules.get(v);

			DefaultMessageContext dmc = new DefaultMessageContext(c);

			for (FormValidatorRule r : rules) {

				r.validate(v, dmc);
			}
			errors.addAll(dmc.getErrors());
			messages.addAll(dmc.getMessages());
		}
	}

	public void clearAll() {
		clearErrors();
		clearMessages();
	}

	@Override
	public Collection<MessageFeedback<?>> getErrors() {
		return new ArrayList<MessageFeedback<?>>(errors);
	}

	@Override
	public Collection<MessageFeedback<?>> getMessages() {
		return new ArrayList<MessageFeedback<?>>(messages);
	}

	@Override
	public boolean hasFeedbackOrErrors() {
		return this.errors != null && this.errors.size() > 0 || this.messages != null && this.messages.size() > 0;
	}

	private static synchronized Collection<MessageFeedback<?>> messagesForWidget(Collection<MessageFeedback<?>> c, View v, boolean inErr) {

		Collection<MessageFeedback<?>> msgs = new ArrayList<MessageFeedback<?>>();
		for (MessageFeedback<?> mf : c) {
			if (mf.isError() == inErr && mf.getWidget().equals(v))
				msgs.add(mf);
		}
		return msgs;
	}

	@Override
	public Collection<MessageFeedback<?>> getErrorsFor(View v) {
		return messagesForWidget(this.errors, v, true);
	}

	@Override
	public Collection<MessageFeedback<?>> getMessagesFor(View v) {
		return messagesForWidget(this.messages, v, false);
	}

	@Override
	public <T extends View> T addValidationRule(T widget, FormValidatorRule<T> rule) {

		Assert.notNull(widget, "the view component against which this validation rule should be evaluated must be specified");
		Assert.notNull(rule, "the validation rule must be non-null, as well");

		if (rule instanceof InitializingBean) {
			try {
				((InitializingBean) rule).afterPropertiesSet();
			} catch (Exception e) {
				Log.e(getClass().getName(), "exception while trying to invoke #afterPropertiesSet() on " + rule.getClass(), e);
			}
		}

		if (!this.rules.containsKey(widget)) // double safety to avoid expensive CSLS creation
			this.rules.put(widget, Collections.synchronizedSet(new HashSet<FormValidatorRule>()));
		this.rules.get(widget).add(rule);
		return widget;
	}
}

/*

@Deprecated
		// mainly a sketchpad to see how it all comes together
class Client {
	public static void main(String[] a) {
		EditText editText = null;
		Spinner spinner = null;

		DefaultFormValidator formValidator = new DefaultFormValidator();

		formValidator.addValidationRule(editText, new FormValidatorRule<EditText>() {
			@Override
			public void validate(EditText editText, MessageContext mc) {

			}
		});

		formValidator.addValidationRule(spinner, new FormValidatorRule<Spinner>() {
			@Override
			public void validate(Spinner spinner, MessageContext mc) {
				if (spinner.getSelectedItem().toString() == null) {
					mc.registerFieldError(spinner, "");
				}
			}
		});

		Context context = null;
		formValidator.validate(context);
	}
}
*/
