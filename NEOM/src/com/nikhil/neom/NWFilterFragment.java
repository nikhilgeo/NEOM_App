package com.nikhil.neom;

import java.util.ArrayList;
import java.util.List;

import com.nikhil.neom.MonitorFragment.MyListAdapter;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class NWFilterFragment extends ListFragment implements
		OnItemClickListener {

	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";
	public MyFilterListAdapter myFilterListAdapter;

	private List<Model_Apps> filter_applist = new ArrayList<Model_Apps>();
	private int abc;

	//
	// // TODO: Rename and change types of parameters
	// private String mParam1;
	// private String mParam2;

	// TODO: Rename and change types of parameters
	public static NWFilterFragment newInstance() {
		NWFilterFragment fragment = new NWFilterFragment();
		// Bundle args = new Bundle();
		// args.putString(ARG_PARAM1, param1);
		// args.putString(ARG_PARAM2, param2);
		// fragment.setArguments(args);
		return fragment;
	}

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public NWFilterFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		// if (getArguments() != null) {
		// // mParam1 = getArguments().getString(ARG_PARAM1);
		// // mParam2 = getArguments().getString(ARG_PARAM2);
		// }

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		getAllInstalledApps();
		View rootView = inflater.inflate(R.layout.frgament_nwfilter, container,
				false);

		// pidinodepname();

		// pid_inode_pname();

		// region monitoring : NG

		return rootView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		myFilterListAdapter = new MyFilterListAdapter(getActivity(),
				R.layout.nwfilter_row, filter_applist);

		getListView().setOnItemClickListener(this);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// try {
		// mListener = (OnFragmentInteractionListener) activity;
		// } catch (ClassCastException e) {
		// throw new ClassCastException(activity.toString()
		// + " must implement OnFragmentInteractionListener");
		// }
	}

	@Override
	public void onDetach() {
		super.onDetach();
		// mListener = null;
	}

	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(Uri uri);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT)
				.show();

	}

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

				appName.setText(appItem.appName);
				packageName.setText(appItem.uid);
				//iconview.setImageDrawable(appItem.icon);

				// TextView txtappname = (TextView) row
				// .findViewById(R.id.txtappname);
				// txtappname.setText(processItem.AppName);
				// TextView txtpid = (TextView) row.findViewById(R.id.txtpid);
				// txtpid.setText(Integer.toString(processItem.processID));
				// TextView pname = (TextView) row.findViewById(R.id.txtpname);
				// pname.setText(processItem.processName);
				// TextView uid = (TextView) row.findViewById(R.id.txtuid);
				// uid.setText(Integer.toString(processItem.userID));
				// TextView txtpss = (TextView) row.findViewById(R.id.txtpss);
				// txtpss.setText(Float.toString(processItem.memInfo[0]) +
				// "MB");
				// TextView txtpvtdirty = (TextView) row
				// .findViewById(R.id.txtpvtdirty);
				// txtpvtdirty.setText(Float.toString(processItem.memInfo[1])
				// + "MB");
				// TextView txtshrdirty = (TextView) row
				// .findViewById(R.id.txtshrdirty);
				// txtshrdirty.setText(Float.toString(processItem.memInfo[2])
				// + "MB");
			}
			return row;
		}
	}

	void getAllInstalledApps() {
		// final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		// mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		// final List pkgAppsList = getActivity().getPackageManager()
		// .queryIntentActivities(mainIntent, 0);

		PackageManager pm = getActivity().getPackageManager();
		List<ApplicationInfo> installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
		for (ApplicationInfo appInfo : installedApps) {
			Model_Apps apps = new Model_Apps();
			apps.appName = appInfo.loadLabel(pm).toString();
			apps.uid = appInfo.uid;
			//apps.icon = appInfo.loadIcon(pm);
			filter_applist.add(apps);
		}
		abc = filter_applist.size();
	}

}
