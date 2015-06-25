package com.nikhil.neom;

import android.app.Activity;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link Filter_home_Fragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link Filter_home_Fragment#newInstance} factory method to create an instance
 * of this fragment.
 *
 */
public class Filter_home_Fragment extends Fragment {

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 *
	 * @param param1
	 *            Parameter 1.
	 * @param param2
	 *            Parameter 2.
	 * @return A new instance of fragment Filter_home_Fragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static Filter_home_Fragment newInstance() {
		Filter_home_Fragment fragment = new Filter_home_Fragment();

		return fragment;
	}

	public Filter_home_Fragment() {
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
		View rootView = inflater.inflate(R.layout.fragment_filter_home,
				container, false);
		Button btnBlackList = (Button) rootView.findViewById(R.id.btnBlck);
		Button btnCntx = (Button) rootView.findViewById(R.id.btnCntx);

		btnBlackList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "Black List button clicked",
						Toast.LENGTH_SHORT).show();

				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager
						.beginTransaction()
						.replace(R.id.container,
								FilterFragment.newInstance(false))
						.addToBackStack("FrmFilter").commit();
			}
		});

		btnCntx.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager
						.beginTransaction()
						.replace(R.id.container,
								FilterFragment.newInstance(true))
						.addToBackStack("Frm2Filter").commit();

				Toast.makeText(getActivity(), "Context button clicked",
						Toast.LENGTH_SHORT).show();
			}
		});

		return rootView;
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
