package org.springframework.samples.travel.client.android.view;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.springframework.samples.travel.client.android.R;
import org.springframework.samples.travel.client.android.Utils;
import org.springframework.samples.travel.client.android.domain.Hotel;
import org.springframework.samples.travel.client.android.services.BookingService;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * this class is responsible for displaying the search results in a clean way
 * <p/>
 * todo implement paging
 *
 * @author Josh Long
 */
public class HotelSearchResults extends ListActivity {

	private List<Hotel> results = new ArrayList<Hotel>();
	private TextView empty;
	private ProgressDialog progressDialog;
	private final String TAG = getClass().getName();
	private Executor executor = Executors.newFixedThreadPool(2);
	private BookingService bookingService;

	private Handler reviewsHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
			if (results == null || results.size() == 0) {
				empty.setText(getString(R.string.empty));
			} else {
				setListAdapter(new HotelListAdapter(HotelSearchResults.this, results));
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.bookingService = Utils.springTravelApplication(this).getBookingService();

		setContentView(R.layout.hotel_search_results);

		empty = Utils.viewById(this, R.id.empty);
		final ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setEmptyView(this.empty);
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadResults();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		Utils.installStandardMenus(this, menu);

		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Hotel hotel = this.results.get(position);
		Log.d(TAG, "you clicked the item " + hotel);
	}

	private void loadResults() {
		this.progressDialog = ProgressDialog.show(this, getString(R.string.working), getString(R.string.retrieving_results), true, false);
		SearchCriteria criteria = Utils.springTravelApplication(this).getSearchCriteria();
		executor.execute(new InvokeHotelSearchServiceRunnable(criteria));
	}

	private class InvokeHotelSearchServiceRunnable implements Runnable {
		private SearchCriteria criteria;

		public InvokeHotelSearchServiceRunnable(SearchCriteria criteria) {
			this.criteria = criteria;
		}

		@Override
		public void run() {
			results.clear();
			Collection<Hotel> bookings = bookingService.searchHotels(criteria.getQuery(), criteria.getMaxPrice());
			if (bookings != null)
				results.addAll(bookings);
			reviewsHandler.sendEmptyMessage(0);
		}
	}

	/**
	 * adapts the {@link Hotel} objects into the view that's displayed on screen.
	 *
	 * @author Josh Long
	 */
	private static class HotelListAdapter extends BaseAdapter {

		private List<Hotel> hotels;
		private Activity context;

		public HotelListAdapter(Activity ctx, Collection<Hotel> h) {
			this.context = ctx;
			this.hotels = new ArrayList<Hotel>(h);
			Assert.notNull(this.context, "the context can't be null");
			Assert.notEmpty(h, "there need to be results to display");
		}

		@Override
		public int getCount() {
			return this.hotels.size();
		}

		@Override
		public Object getItem(int position) {
			return hotels.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Hotel h = this.hotels.get(position);
			return new HotelView(this.context, h, true);
		}
	}
}

