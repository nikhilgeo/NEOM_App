package com.nikhil.neom;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link SetContextFragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link SetContextFragment#newInstance} factory method to create an instance
 * of this fragment.
 * 
 */
public class SetContextFragment extends ListFragment {

	String[] numbers_text = new String[] { "one", "two", "three", "four",
			"two", "three", "two", "three", "two", "three", "five" };

	public static SetContextFragment newInstance() {
		SetContextFragment fragment = new SetContextFragment();
		return fragment;
	}

	public SetContextFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		View rootView = inflater.inflate(R.layout.fragment_set_context,
				container, false);
		Button btnAddSSID = (Button) rootView.findViewById(R.id.btnAddSSID);
		final TextView txtSSID = (TextView) rootView.findViewById(R.id.txtSSID);
		btnAddSSID.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String ssid = txtSSID.getText().toString();
				

				if (ssid.isEmpty())
					Toast.makeText(getActivity(), "Need WiFi SSID" + ssid,
							Toast.LENGTH_SHORT).show();
				else
				{
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager
							.beginTransaction()
							.replace(R.id.container,
									FilterFragment.newInstance(ssid))
							.addToBackStack("FrmFilter").commit();
				}
			}
		});
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, numbers_text);

		setListAdapter(adapter);

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

	}

	@Override
	public void onDetach() {
		super.onDetach();

	}

}
