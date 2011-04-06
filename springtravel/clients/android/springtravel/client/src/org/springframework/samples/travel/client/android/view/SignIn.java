package org.springframework.samples.travel.client.android.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import org.springframework.samples.travel.client.android.R;
import org.springframework.samples.travel.client.android.Utils;
import org.springframework.samples.travel.client.android.domain.User;
import org.springframework.samples.travel.client.android.services.BookingService;
import org.springframework.samples.travel.client.android.view.validation.DefaultFormValidator;
import org.springframework.samples.travel.client.android.view.validation.FormValidator;
import org.springframework.samples.travel.client.android.view.validation.FormValidatorRule;
import org.springframework.samples.travel.client.android.view.validation.MessageContext;
import org.springframework.samples.travel.client.android.view.validation.validators.EditTextRequiredValueValidator;
import org.springframework.util.StringUtils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Simple activity that lets users sign into their travel app
 * as one of the registered users (they won't have to sign in during the booking flow, this way).
 *
 * @author Josh Long
 */
public class SignIn extends Activity {
	private FormValidator formValidator;
	private ProgressDialog progressDialog;
	private Executor executor = Executors.newFixedThreadPool(2);
	private BookingService bookingService;
	private EditText userEditText, pwEditText;
	private String TAG = getClass().getName();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		Utils.installStandardMenus(this, menu);
		return true;
	}

	private boolean isValid() {
		formValidator.reset();
		formValidator.validate(this);

		return !formValidator.hasFeedbackOrErrors();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		bookingService = Utils.springTravelApplication(this).getBookingService();
		setContentView(R.layout.sign_in);
		userEditText = Utils.viewById(this, R.id.username);
		pwEditText = Utils.viewById(this, R.id.password);

		LinearLayout linearLayout = Utils.viewById(this, R.id.sign_in_form_layout);
		linearLayout.addView(Utils.buildLabelAndField(this, getString(R.string.username_label), userEditText));
		linearLayout.addView(Utils.buildLabelAndField(this, getString(R.string.password_label), pwEditText));

		Button signInButton = Utils.viewById(this, R.id.signin);
		signInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "inside signInButton.OnClickListener#onClick. ");
				doSignIn();
			}
		});

		formValidator = new DefaultFormValidator();
		EditTextRequiredValueValidator requiredValueValidator = new EditTextRequiredValueValidator();
		requiredValueValidator.setFeedbackMessage(R.string.usr_and_pw_invalid);
		formValidator.addValidationRule(userEditText, new FormValidatorRule<EditText>() {
			@Override
			public void validate(EditText editText, MessageContext mc) {
				String usrTxt = Utils.stringValueFor(userEditText);
				String pwTxt = Utils.stringValueFor(pwEditText);
				if (!StringUtils.hasText(usrTxt) || !StringUtils.hasText(pwTxt))
					mc.registerFieldError(userEditText, R.string.usr_and_pw_invalid);
			}
		});
	}

	private void signIn(String u, String p) {
		this.progressDialog = ProgressDialog.show(this, getString(R.string.working), getString(R.string.signing_in), true, false);
		progressDialog.show();
		executor.execute(new SignInRunnable(bookingService, u, p));
	}

	private void doSignIn() {
		if (isValid()) {
			String usrTxt = Utils.stringValueFor(this.userEditText);
			String pwTxt = Utils.stringValueFor(this.pwEditText);
			signIn(usrTxt, pwTxt);
		} else {
			new AlertDialog.Builder(this)
					.setTitle(getString(R.string.alert_label))
					.setMessage(getString(R.string.usr_and_pw_invalid))
					.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					}).show();
		}
	}

	private final Handler signedInHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
			Utils.redirectToOriginalIntent(SignIn.this, getIntent());
		}
	};

	class SignInRunnable implements Runnable {
		private BookingService bookingService;
		private String userName, password;

		public SignInRunnable(BookingService bookingService, String usr, String pw) {
			this.bookingService = bookingService;
			this.userName = usr;
			this.password = pw;
		}

		@Override
		public void run() {
			User usr = bookingService.login(userName, password);

			Utils.springTravelApplication(SignIn.this).login(usr);

			signedInHandler.sendEmptyMessage(0);
		}
	}
}
