package com.nikhil.neom;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link FilterFragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link FilterFragment#newInstance} factory method to create an instance of
 * this fragment.
 * 
 */
public class FilterFragment extends ListFragment {

	public MyFilterListAdapter myFilterListAdapter;
	private List<Model_Apps> filter_applist = new ArrayList<Model_Apps>();

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
			// getAllInstalledApps();
			ExecuteCMD executeCMD = new ExecuteCMD();
			String cmd[] = { "iptables -L" };
			String output;
			output = executeCMD.RunAsRoot(cmd);
			// Toast.makeText(getActivity(), "Output " + output,
			// Toast.LENGTH_SHORT).show();
		} catch (Exception ex) {
			Log.w("NEOM:", ex.toString(), ex);
		}
		View rootView = inflater.inflate(R.layout.fragment_filter, container,
				false);
		return rootView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		myFilterListAdapter = new MyFilterListAdapter(getActivity(),
				R.layout.nwfilter_row, filter_applist);

		setListAdapter(myFilterListAdapter);

		// apps = getListView();
		// getListView().setOnItemClickListener(this);

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
				Model_Apps appItem = filter_applist.get(position);

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
						if (((CheckBox) v).isChecked())
							Toast.makeText(getActivity(),
									"Checked item " + Integer.toString(pos),
									Toast.LENGTH_SHORT).show();
						else
							Toast.makeText(getActivity(),
									"Uncecked item " + Integer.toString(pos),
									Toast.LENGTH_SHORT).show();

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

}
