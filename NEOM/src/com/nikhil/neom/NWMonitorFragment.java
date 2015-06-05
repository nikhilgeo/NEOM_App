package com.nikhil.neom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.ListFragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nikhil.neom.MonitorFragment.MyListAdapter;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class NWMonitorFragment extends ListFragment implements
		OnItemClickListener {

	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private OnFragmentInteractionListener mListener;

	private List<Model_Connection> connection_list = new ArrayList<Model_Connection>();
	private static final String ARG_SECTION_NUMBER = "section_number";
	private String section_number;
	private Thread monitorNWThread, execmdThread;
	private Handler nwhandler;
	private int counter = 0; // For test TBR

	private MyListAdapter myNWListAdapter;

	// TODO: Rename and change types of parameters
	public static NWMonitorFragment newInstance() {
		NWMonitorFragment fragment = new NWMonitorFragment();
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
	public NWMonitorFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_nwmonitor,
				container, false);
		return rootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// if (getArguments() != null) {
		// mParam1 = getArguments().getString(ARG_PARAM1);
		// mParam2 = getArguments().getString(ARG_PARAM2);
		// }
		//
		// // TODO: Change Adapter to display your content
		// setListAdapter(new
		// ArrayAdapter<DummyContent.DummyItem>(getActivity(),
		// android.R.layout.simple_list_item_1, android.R.id.text1,
		// DummyContent.ITEMS));
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

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT)
				.show();
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(String id);
	}

	@Override
	public void onPause() {
		super.onPause();
		monitorNWThread.interrupt();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		/*
		 * Commented to bring in custom adapter for custom row layout
		 * ArrayAdapter<String> adapter = new
		 * ArrayAdapter<String>(getActivity(),
		 * android.R.layout.simple_list_item_1, values);
		 */
		// getJavaProcessPID();

		// endregion monitoring : NG

		myNWListAdapter = new MyListAdapter(getActivity(),
				R.layout.nw_list_item, connection_list);

		setListAdapter(myNWListAdapter);
		getListView().setOnItemClickListener(this);

		nwhandler = new Handler() {

			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle mbb = msg.getData();
				if (mbb != null) {
					connection_list = (List<Model_Connection>) mbb
							.getSerializable("networkKey");
				}
				// txtview.setText("Monitor Fragment : " +
				// Integer.toString(mbb.getInt("Num")));
				// if (mbb.getBoolean("DataChanged")) {
				// myListAdapter.clear();
				// getAllPID();
				myNWListAdapter.notifyDataSetChanged();
				// }
			}
		};

		monitorNWThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (!Thread.currentThread().isInterrupted()) {
						while (true) {

							// getAllPID();

							List<Model_Connection> networkData = getConnections();

							Bundle mb = new Bundle();
							mb.putSerializable("networkKey",
									(Serializable) networkData);
							Message mmsg = Message.obtain();
							mmsg.setData(mb);
							nwhandler.sendMessage(mmsg);

							Thread.sleep(2000);

						}
					}
				} catch (Exception e) {
					e.getLocalizedMessage();
				}

			}// end of run
		});
		monitorNWThread.start();
	}

	private List<Model_Connection> getConnections() {
		// TODO Auto-generated method stub
		Utilities util = new Utilities();
		final PackageManager pm = getActivity().getPackageManager();
		ActivityManager activityManager = (ActivityManager) getActivity()
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> pidsTask = activityManager
				.getRunningAppProcesses();
		ArrayList<Integer> pid_arlist = new ArrayList<Integer>();
		for (int i = 0; i < pidsTask.size(); i++) {
			Model_Connection connectionObj = new Model_Connection();
			connectionObj.processID = pidsTask.get(i).pid;
			pid_arlist.add(connectionObj.processID);
			connectionObj.con = util.getPIDConnections(connectionObj.processID
					.toString());
			for (Utilities.Connection conObj : connectionObj.con) {
				connectionObj.connections = "SRC:" + conObj.src + ":"
						+ conObj.spt + " DST:" + conObj.dst + ":" + conObj.dpt
						+ " " + conObj.pro + "\n";
			}
			connectionObj.connections = connectionObj.connections.trim();// trim
																			// traling
																			// white
																			// and
																			// newline
			CharSequence app_name;
			if (connectionObj.connections == null) {
				connectionObj.connections = "No Network Connection";
			}
			try {
				app_name = pm.getApplicationLabel(pm.getApplicationInfo(
						pidsTask.get(i).processName,
						PackageManager.GET_META_DATA));
				// app_name = pm.getNameForUid(pidsTask.get(i).uid);

			} catch (Exception ex) {
				// Log.w("NEOM:Error", ex.toString(), ex);
				String[] split = pidsTask.get(i).processName.split("\\.");
				app_name = split[split.length - 1];

				// app_name = "Error";
			} // End of Catch
			connectionObj.appName = app_name.toString();
			if (connection_list != null && replaceDup(connectionObj))
				continue;
			connection_list.add(connectionObj);

		}// end of For loop
		removeExpiredPID(pid_arlist);
		return connection_list;
	} // end of function getConnections

	/*
	 * Check if PID already exist in the List before adding
	 */
	private Boolean replaceDup(Model_Connection connDup) {
		int replacePos = -1;
		for (Model_Connection conn : connection_list) {

			if (connDup.processID.equals(conn.processID)) {
				replacePos = connection_list.indexOf(conn);
				break;
			}
		}
		if (replacePos != -1) {

			connection_list.set(replacePos, connDup);
			return true;
		}
		return false;

	}

	/*
	 * Remove the stopped process from the process list
	 */
	private void removeExpiredPID(ArrayList<Integer> pids) {

		for (Iterator<Model_Connection> iterator = connection_list.iterator(); iterator
				.hasNext();) {
			Model_Connection con = iterator.next();
			if (!pids.contains(con.processID))
				iterator.remove();
		}

	}

	/*
	 * Custom Adapter to bind custom row layout Change the ArrayAdapter<T>
	 * according to the type you want to populate the row
	 */
	public class MyListAdapter extends ArrayAdapter<Model_Connection> {

		Context myContext;

		public MyListAdapter(Context context, int textViewResourceId,
				List<Model_Connection> objects) {
			super(context, textViewResourceId, objects);
			myContext = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) myContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row = inflater.inflate(R.layout.nw_list_item, parent, false);
			try {

				// View condetrow = inflater.inflate(R.layout.connection_layout,
				// parent, false);

				// For alternate row color
				if (position % 2 == 1) {
					row.setBackgroundColor(Color.TRANSPARENT);
				} else {
					row.setBackgroundColor(Color.WHITE);
				}

				if (connection_list != null) {
					Model_Connection conItem = connection_list.get(position);
					TextView txtappname = (TextView) row
							.findViewById(R.id.txtappname);
					txtappname.setText(conItem.appName);
					TextView txtpid = (TextView) row.findViewById(R.id.txtpid);
					txtpid.setText(Integer.toString(conItem.processID));
					// TextView pname = (TextView) row.findViewById(R.id.);
					// pname.setText(processItem.processName);
					// TextView uid = (TextView) row.findViewById(R.id.txtuid);
					// uid.setText(Integer.toString(processItem.userID));
					// TextView txtpss = (TextView)
					// row.findViewById(R.id.txtpss);
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
					TextView txtnwcon = (TextView) row
							.findViewById(R.id.txtnwcon);
					txtnwcon.setText(conItem.connections);

				}
			} catch (Exception ex) {
				Log.w("NetworkLog", ex.toString(), ex);
			}
			return row;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}

}
