package org.springframework.samples.travel.client.android.view.validation;

import android.content.Context;
import android.view.View;

import java.util.Collection;

/**
 * Represents an object used to collect validation feedback on a form in an Android {@link android.app.Activity}
 * <p/>
 * todo add form-global validation messages
 * todo add jsr303 support?
 *
 * @author Josh Long
 */
public interface FormValidator {

	void reset();

	/**
	 * runs validation, invoking all rules as appropriate.
	 * After this is run, errors may be accessed by calling the appropriate methods on this classs (e.g., {@link #getMessagesFor(android.view.View)} and {@link #getErrors()}, etc.)
	 *
	 * @param c context so we can access string messages in case somebody invokes {@link MessageContext#registerFieldError(android.view.View, int, Object...)} or {@link MessageContext#registerFieldMessage(android.view.View, int, Object...)}
	 */
	void validate(Context c);

	/**
	 * widget specific errrors
	 *
	 * @param v the widget against which the errors are registered
	 * @return errors for a specific widget
	 */
	Collection<MessageFeedback<?>> getErrorsFor(View v);

	/**
	 * widget specific messages
	 *
	 * @param v the widget against which the messages are registered
	 * @return messages for a specific widget
	 */
	Collection<MessageFeedback<?>> getMessagesFor(View v);

	/**
	 * gets all widget errors
	 *
	 * @return all widget errors
	 */
	Collection<MessageFeedback<?>> getErrors();

	/**
	 * returns all messages (ie, feedback that's not necessarily problematic)
	 *
	 * @return all messages for widgets
	 */
	Collection<MessageFeedback<?>> getMessages();

	boolean hasFeedbackOrErrors();

	/**
	 * add a rule to a given widget. at validation time, this rule will be invoked and given a chance to register feedback.
	 *
	 * @param widget w
	 * @param rule	 r
	 * @param <T>    t
	 * @return the widget on which the rule is registered. conceptually, users should add this
	 *         to the layout and not the input as we might decorate it or
	 *         something to paint validation in. At the moment, this simply returns the input, however.
	 */
	<T extends View> T addValidationRule(T widget, FormValidatorRule<T> rule);
}
