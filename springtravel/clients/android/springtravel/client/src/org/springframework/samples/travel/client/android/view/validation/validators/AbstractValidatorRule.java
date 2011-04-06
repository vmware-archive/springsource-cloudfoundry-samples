package org.springframework.samples.travel.client.android.view.validation.validators;

import android.content.Context;
import android.view.View;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.samples.travel.client.android.view.validation.FormValidatorRule;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author Josh Long
 */
public abstract class AbstractValidatorRule<T extends View> implements FormValidatorRule<T>, InitializingBean {
	protected String feedbackMessage;
	protected Object[] messageFormatParameters;
	protected int feedbackMessageId;

	public void setFeedbackMessage(String feedbackMessage) {
		this.feedbackMessage = feedbackMessage;
	}

	public void setMessageFormatParameters(Object[] messageFormatParameters) {
		this.messageFormatParameters = messageFormatParameters;
	}

	protected String getFormattedFeedbackMessageString(Context ctx) {
		if (!StringUtils.hasText(feedbackMessage) && feedbackMessageId > 0)
			return buildErrorMessage(ctx, ctx.getString(this.feedbackMessageId), this.messageFormatParameters);
		if (StringUtils.hasText(this.feedbackMessage))
			return buildErrorMessage(ctx, this.feedbackMessage, this.messageFormatParameters);
		return null;
	}

	public void setFeedbackMessage(int feedbackMessageId) {
		this.feedbackMessageId = feedbackMessageId;
	}

	protected String buildErrorMessage(Context context, String msg, Object... parms) {
		String x = null;
		if (StringUtils.hasText(msg)) {
			x = parms != null && parms.length > 0 ? String.format(msg, parms) : msg;
		}
		return x;
	}

	protected String buildErrorMessage(View v, String msg, Object... p) {
		return this.buildErrorMessage(v.getContext(), msg, p);
	}

	@Override
	public void afterPropertiesSet() {
		Assert.isTrue(StringUtils.hasText(this.feedbackMessage) || this.feedbackMessageId > 0,
				"the 'feedbackMessage' or 'feedbackMessageId' properties must be set.");
	}
}
