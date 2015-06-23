package com.nikhil.neom;

import java.util.ArrayList;
import java.util.List;

import com.nikhil.neom.iptablesDBContract.iptblrule;

import android.R.integer;
import android.app.Activity;
import android.app.ListFragment;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link FilterFragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link FilterFragment#newInstance} factory method to create an instance of
 * this fragment.
 * 
 */
public class FilterFragment extends ListFragment {

	// class Blocked {
	// String appName;
	// String uid;
	//
	// }

	public MyFilterListAdapter myFilterListAdapter;
	private List<Model_Apps> filter_applist = new ArrayList<Model_Apps>();
	private List<Integer> blocked_uid = new ArrayList<Integer>();
	private Boolean dbWriteStat = true;

	// ListView apps;

	public static FilterFragment newInstance() {
		FilterFragment fragment = new FilterFragment();
		return fragment;
	}

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public FilterFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		try {
			getAllInstalledApps();
			ExecuteCMD executeCMD = new ExecuteCMD();
			// String cmd[] = { "iptables -L" };
			// String output;
			// output = executeCMD.RunAsRoot(cmd);
			// Toast.makeText(getActivity(), "Output " + output,
			// Toast.LENGTH_SHORT).show();
		} catch (Exception ex) {
			Log.w("NEOM:", ex.toString(), ex);
		}
		View rootView = inflater.inflate(R.layout.fragment_filter, container,
				false);
		Button updateButton = (Button) rootView.findViewById(R.id.updateButton);
		updateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// Clear all current rules from DB
				flushRules();
				if (blocked_uid != null && blocked_uid.size() != 0) {
					ArrayList<String> block_rules_arlist = new ArrayList<String>();
					for (int uid : blocked_uid) {

						String rule = "iptables -A OUTPUT -m owner --uid-owner "
								+ Integer.toString(uid) + " -j DROP";
						block_rules_arlist.add(rule);
						long newRowID = writeRulestoDB(uid, rule);

						// del all existing rules
						// apply new rules
						// return back verification
						// If sucess show a sucess msg
					}

					String block_rules_ar[] = block_rules_arlist
							.toArray(new String[block_rules_arlist.size()]);

					ExecuteCMD executeCMD = new ExecuteCMD();
					executeCMD.RunAsRoot(block_rules_ar);

					Toast.makeText(getActivity(), "Button clicked",
							Toast.LENGTH_SHORT).show();
				}
			}

		});
		return rootView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		myFilterListAdapter = new MyFilterListAdapter(getActivity(),
				R.layout.nwfilter_row, filter_applist);

		setListAdapter(myFilterListAdapter);

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(Uri uri);
	}

	// @Override
	// public void onItemClick(AdapterView<?> parent, View view, int position,
	// long id) {
	// // TODO Auto-generated method stub
	// Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT)
	// .show();
	//
	// }

	/*
	 * Custom Adapter to bind custom row layout Change the ArrayAdapter<T>
	 * according to the type you want to populate the row
	 */
	public class MyFilterListAdapter extends ArrayAdapter<Model_Apps> {

		Context myContext;

		public MyFilterListAdapter(Context context, int textViewResourceId,
				List<Model_Apps> objects) {
			super(context, textViewResourceId, objects);
			myContext = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final int pos = position;
			LayoutInflater inflater = (LayoutInflater) myContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row = inflater.inflate(R.layout.nwfilter_row, parent, false);

			// For alternate row color
			if (position % 2 == 1) {
				row.setBackgroundColor(Color.TRANSPARENT);
			} else {
				row.setBackgroundColor(Color.WHITE);
			}

			if (filter_applist != null) {
				final Model_Apps appItem = filter_applist.get(position);

				TextView appName = (TextView) row.findViewById(R.id.app_name);
				TextView packageName = (TextView) row
						.findViewById(R.id.app_paackage);
				ImageView iconview = (ImageView) row
						.findViewById(R.id.app_icon);
				CheckBox checkbox = (CheckBox) row.findViewById(R.id.checkBox1);

				appName.setText(appItem.appName);
				packageName.setText("UID:" + appItem.uid.toString());
				iconview.setImageDrawable(appItem.icon);

				checkbox.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						if (((CheckBox) v).isChecked()) {
							blocked_uid.add(appItem.uid);
							Toast.makeText(getActivity(),
									"Checked item " + Integer.toString(pos),
									Toast.LENGTH_SHORT).show();
						} else {
							blocked_uid.remove(appItem.uid);
							Toast.makeText(getActivity(),
									"Uncecked item " + Integer.toString(pos),
									Toast.LENGTH_SHORT).show();
						}

					}
				});

			}
			return row;
		}
	}

	void getAllInstalledApps() {
		// final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		// mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		// final List pkgAppsList = getActivity().getPackageManager()
		// .queryIntentActivities(mainIntent, 0);
		try {
			PackageManager pm = getActivity().getPackageManager();
			List<ApplicationInfo> installedApps = pm
					.getInstalledApplications(PackageManager.GET_META_DATA);
			for (ApplicationInfo appInfo : installedApps) {
				Model_Apps apps = new Model_Apps();
				apps.appName = appInfo.loadLabel(pm).toString();
				apps.uid = appInfo.uid;
				apps.icon = appInfo.loadIcon(pm);
				filter_applist.add(apps);
			}
		} catch (Exception ex) {
			Log.w("NEOM:", ex.toString(), ex);
		}

	}

	long writeRulestoDB(int uid, String rule) {

		neomDbHelper mDbHelper = new neomDbHelper(getActivity());
		// Gets the data repository in write mode
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(iptblrule.COLUMN_NAME_UID, uid);
		values.put(iptblrule.COLUMN_NAME_RULE, rule);

		long newRowId;
		newRowId = db.insert(iptblrule.TABLE_NAME, null, values);
		return newRowId;

	}

	private void flushRules() {
		// TODO Auto-generated method stub

		// Get all uid of past blocked apps.
		ExecuteCMD executeCMD = new ExecuteCMD();
		List<String> uidinDBLst = getAllRulesFrmDB();
		ArrayList<String> del_rules_arlist = new ArrayList<String>();
		// Create delete iptable rules
		for (String blkuid : uidinDBLst) {
			String rule = "iptables -D OUTPUT -m owner --uid-owner " + blkuid
					+ " -j DROP";
			del_rules_arlist.add(rule);
		}

		String del_rules[] = del_rules_arlist
				.toArray(new String[del_rules_arlist.size()]);

		// Delete all existing rules from iptables
		executeCMD.RunAsRoot(del_rules);

		// Delete all existing rules from Database
		neomDbHelper mDbHelper = new neomDbHelper(getActivity());
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.execSQL("delete from " + iptblrule.TABLE_NAME);

	}

	private List<String> getAllRulesFrmDB() {
		List<String> uidLst = new ArrayList<String>();
		neomDbHelper mDbHelper = new neomDbHelper(getActivity());
		SQLiteDatabase db = mDbHelper.getReadableDatabase();

		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = { iptblrule.COLUMN_NAME_UID };
		Cursor cursor = db.query(iptblrule.TABLE_NAME, // The table to query
				projection, // The columns to return
				null, // The columns for the WHERE clause
				null,// The sort order
				null, null, null);
		if (cursor.moveToFirst()) {
			do {
				String uid = cursor.getString(cursor
						.getColumnIndexOrThrow(iptblrule.COLUMN_NAME_UID));
				uidLst.add(uid);
			} while (cursor.moveToNext());
		}
		return uidLst;

	}
}
