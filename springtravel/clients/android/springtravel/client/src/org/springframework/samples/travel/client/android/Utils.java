package org.springframework.samples.travel.client.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.springframework.samples.travel.client.android.view.HotelSearch;
import org.springframework.samples.travel.client.android.view.SignIn;
import org.springframework.samples.travel.client.android.view.UserHome;
import org.springframework.samples.travel.client.android.view.validation.FormValidator;
import org.springframework.samples.travel.client.android.view.validation.MessageFeedback;
import org.springframework.util.Assert;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.InputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

/**
 * convenience calls to simplify lookups and other tasks commonly done in the {@link Activity} code.
 *
 * @author Josh Long
 */
public abstract class Utils {
	static public DateFormat buildDefaultDateFormat (){ return  	 DateFormat.getDateInstance(DateFormat.FULL);  }
	//// validation

	static private String buildFeedbackMessage(Collection<MessageFeedback<?>> feedback) {
		StringBuffer msgToDisplay = new StringBuffer();
		for (MessageFeedback<?> m : feedback) {
			msgToDisplay.append(m.getMessage() + System.getProperty("line.separator"));
		}

		return msgToDisplay.toString();
	}

	static public AlertDialog buildFeedbackAlertDialog(Context ctx, FormValidator formValidator) {

		if (formValidator.hasFeedbackOrErrors()) {

			Collection<MessageFeedback<?>> msgs = formValidator.getMessages();
			Collection<MessageFeedback<?>> errs = formValidator.getErrors();

			String toDisplay;
			if (errs.size() > 0) {
				toDisplay = buildFeedbackMessage(errs);
			} else {
				toDisplay = buildFeedbackMessage(msgs);
			}

			AlertDialog alertDialog = new AlertDialog.Builder(ctx)
					.setTitle(ctx.getString(R.string.alert_label))
					.setMessage(toDisplay)
					.setPositiveButton(ctx.getString(R.string.ok), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// noop
						}
					}).create();
			return alertDialog;
		}
		return null;
	}

	//////////////////////

	/**
	 * the Spring travel application has several menus that are visible throughout the Android application's experience.
	 * <p/>
	 * This method installs standard menu options for:
	 * <p/>
	 * <UL>
	 * <LI> Sign In (which is only visible when the user's not signed in)</LI>
	 * <LI> Sign Out (which is only visible when the user's signed in) </LI>
	 * <LI>Search</LI>
	 * <LI>User Home</li>
	 * </ul>
	 */
	static public void installStandardMenus(Context ctx, Menu menu) {

		MenuItem newSearchMenu = menu.add(0, NEW_SEARCH_MENU_ID, 0, R.string.search_menu_label /*ctx.getString(R.string.search_label)*/);
		newSearchMenu.setOnMenuItemClickListener(new DispatchingMenuItemOnClickListener(ctx, HotelSearch.class));

		MenuItem userHomeMenu = menu.add(0, USER_HOME_MENU_ID, 0, R.string.user_home_menu_label); //ctx.getString(R.string.search_label));
		userHomeMenu.setOnMenuItemClickListener(new DispatchingMenuItemOnClickListener(ctx, UserHome.class, true));
	}

	/**
	 * for use in the {@link Utils#installStandardMenus(android.content.Context, android.view.Menu)} method.
	 */
	private static final class DispatchingMenuItemOnClickListener implements MenuItem.OnMenuItemClickListener {

		private Class activityClassToDispatchTo;
		private Context context;
		private boolean userShouldSignIn;

		@Override
		public boolean onMenuItemClick(MenuItem menuItem) {
			Intent intent = new Intent(context, activityClassToDispatchTo);
			if (userShouldSignIn) intent = buildSignInAwareIntent(context, intent);

			context.startActivity(intent);
			return true;
		}

		private DispatchingMenuItemOnClickListener(Context context, Class<? extends Activity> actd, boolean ss) {
			this.context = context;
			this.activityClassToDispatchTo = actd;
			this.userShouldSignIn = ss;
		}

		private DispatchingMenuItemOnClickListener(Context context, Class<? extends Activity> activityClassToDispatchTo) {
			this.activityClassToDispatchTo = activityClassToDispatchTo;
			this.context = context;
		}
	}

	static public final int USER_HOME_MENU_ID = 32;

	static public final int SIGN_IN_MENU_ID = 31;

	static public final int NEW_SEARCH_MENU_ID = 33;

	//////////////////////

	private final static ThreadLocal<Calendar> currentCalendar = new ThreadLocal<Calendar>();

	static private final String WELL_KNOWN_INTENT_EXTRA_ORIGINAL_INTENT = "signIn_orignal_intent";

	static public SpringTravelApplication springTravelApplication(Context c) {
		return SpringTravelApplication.getInstance(c);
	}

	@Deprecated
	static public Bitmap bitmapFromUrl(String urlStr) throws Exception {
		Bitmap bitmap = null;
		InputStream in = null;
		try {
			SocketFactory socketFactory = SSLSocketFactory.getDefault();
			Socket socket = socketFactory.createSocket(urlStr, 80);
			in = socket.getInputStream();
			bitmap = BitmapFactory.decodeStream(in);
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return bitmap;
	}

	static public View buildLabelAndField(Context ctx, String label, View component) {
		LinearLayout relativeLayout = new LinearLayout(ctx);
		relativeLayout.setOrientation(LinearLayout.VERTICAL);
		TextView textView = new TextView(ctx);
		textView.setText(label);
		int lableStyle = R.style.label;
		//ctx.getResources().
		//textView.setTextAppearance( );
		textView.setTextAppearance(ctx, lableStyle);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		relativeLayout.addView(textView, lp);

		if (component.getParent() != null) {
			Assert.isInstanceOf(ViewGroup.class, component.getParent());
			ViewGroup viewGroup = (ViewGroup) component.getParent();
			viewGroup.removeView(component);
		}

		relativeLayout.addView(component, lp);

		return relativeLayout;
	}

	static public void redirectToOriginalIntent(Context ctx, Intent i) {

		Assert.isTrue(i.getExtras().containsKey(WELL_KNOWN_INTENT_EXTRA_ORIGINAL_INTENT), "this intent must have the key '" + WELL_KNOWN_INTENT_EXTRA_ORIGINAL_INTENT + "'");

		Intent ogIntention = (Intent) i.getExtras().get(WELL_KNOWN_INTENT_EXTRA_ORIGINAL_INTENT);

		ctx.startActivity(ogIntention);
	}

	/**
	 * some actions require that we redirect to the sign in screen and then back to the original screen:
	 * <p/>
	 * two examples: booking a room when not signed in, or clicking on the 'my bookings' button when not signed in.
	 * <p/>
	 * This method wraps the original {@link Intent} and forwards to the {@link org.springframework.samples.travel.client.android.view.SignIn} activity and gives it a chance to
	 * log the user in and then forwards to the original Intent, hopefully with all context data intact.
	 */
	static public Intent buildSignInAwareIntent(Context ctx, Intent i) {
		Assert.notNull(i, "the intent can't be null");
		if (!springTravelApplication(ctx).isUserLoggedIn()) {
			Intent siIntent = new Intent(i);
			siIntent.setClass(ctx, SignIn.class);
			//siIntent.setAction(Constants.ACTION_SIGN_IN);
			siIntent.putExtra(WELL_KNOWN_INTENT_EXTRA_ORIGINAL_INTENT, i);
			return siIntent;
		}
		return i;
	}

	static public ViewGroup.LayoutParams buildLayoutParameters(int w, int h) {
		return new ViewGroup.LayoutParams(w, h);
	}

	public static void configureDatePicker(DatePicker datePicker, Date date, DatePicker.OnDateChangedListener onDateChangedListener) {
		Calendar calendar = Utils.getCalendar();
		calendar.setTime(date);
		int m = calendar.get(Calendar.MONTH);
		int y = calendar.get(Calendar.YEAR);
		int d = calendar.get(Calendar.DAY_OF_MONTH);
		datePicker.init(y, m, d, onDateChangedListener);
	}

	static public String lookupStringResourceForKey(Context ctx, String k) {
		int id = ctx.getResources().getIdentifier(ctx.getPackageName() + ":string/" + k, null, null);
		return ctx.getString(id);
	}

	public static Date dateFromPrincipalIntegers(int y, int m, int d) {
		Calendar calendar = Utils.getCalendar();
		calendar.set(Calendar.YEAR, y);
		calendar.set(Calendar.MONTH, m);
		calendar.set(Calendar.DAY_OF_MONTH, d);
		return calendar.getTime();
	}

	public static Date dateFromDatePicker(DatePicker datePicker) {
		return dateFromPrincipalIntegers(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
	}

	public static Calendar getCalendar() {
		if (currentCalendar.get() == null)
			currentCalendar.set(Calendar.getInstance());
		return currentCalendar.get();
	}

	@SuppressWarnings("unchecked")
	static public <T extends View> T viewById(Activity view, int id) {
		View v = view.findViewById(id);
		return (T) v;
	}

	static public String stringValueFor(TextView v) {
		if (v.getText() != null) {
			return v.getText().toString().trim();
		}
		return null;
	}

	public static String stringValueFor(Editable editable) {
		char[] cs = new char[editable.length()];
		editable.getChars(0, editable.length(), cs, 0);
		return new String(cs);
	}

	public static String stringValueFor(EditText editable) {
		return stringValueFor(editable.getText());
	}
}
