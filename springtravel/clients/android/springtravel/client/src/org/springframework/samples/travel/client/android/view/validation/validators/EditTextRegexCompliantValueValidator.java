package org.springframework.samples.travel.client.android.view.validation.validators;

import android.util.Log;
import android.widget.EditText;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.samples.travel.client.android.Utils;
import org.springframework.samples.travel.client.android.view.validation.FormValidatorRule;
import org.springframework.samples.travel.client.android.view.validation.MessageContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * @author Josh Long
 */
public class EditTextRegexCompliantValueValidator implements FormValidatorRule<EditText>, InitializingBean {

	/**
	 * The regular expression mask
	 */
	private String regularExpressionMask;
	private Pattern pattern;
	private int patternFlags = -1;

	public void setPatternFlags(int patternFlags) {
		this.patternFlags = patternFlags;
	}

	public void setRegularExpression(String regularExpressionMask) {
		this.regularExpressionMask = regularExpressionMask;
	}

	public void setRegularExpressionPattern(Pattern regularExpressionPatternMask) {
		this.pattern = regularExpressionPatternMask;
	}

	@Override
	public void validate(EditText editText, MessageContext mc) {

		Assert.notNull(editText, "the editText component can't be null");

		String value = Utils.stringValueFor(editText);
		if (StringUtils.hasText(value)) {

			if (pattern != null) {

			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		Assert.isTrue(this.pattern != null || (StringUtils.hasText(this.regularExpressionMask) && this.patternFlags > -1) ||
				StringUtils.hasText(this.regularExpressionMask),
				"you must either specify a 'regularExpression', " +
						"or specify a 'regularExpressionPattern'");

		if (StringUtils.hasText(regularExpressionMask) && this.pattern == null) {
			Pattern p;
			if (this.patternFlags > -1) {
				p = Pattern.compile(this.regularExpressionMask, patternFlags);
			} else {
				p = Pattern.compile(this.regularExpressionMask);
			}
			this.pattern = p;
		} else {
			Log.d(getClass().getName(), "the 'pattern' was already specified and will be used. Ignoring configured regularExpressionMask configuration");
		}
	}
}
