package com.nikhil.neom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link ConnectionFragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link ConnectionFragment#newInstance} factory method to create an instance
 * of this fragment.
 * 
 */
public class ConnectionFragment extends Fragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private OnFragmentInteractionListener mListener;

	private ExpandListAdapter expandListAdapter;
	private ArrayList<ConnectionGroup> conGroups;
	Handler connectionHandler;
	Thread connectionThread;

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @param param1
	 *            Parameter 1.
	 * @param param2
	 *            Parameter 2.
	 * @return A new instance of fragment ConnectionFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static ConnectionFragment newInstance(String param1, String param2) {
		ConnectionFragment fragment = new ConnectionFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public ConnectionFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		expandListAdapter = new ExpandListAdapter(getActivity(), conGroups);
		View v = inflater.inflate(R.layout.fragment_connection, container,
				false);
		ExpandableListView elv = (ExpandableListView) v
				.findViewById(R.id.exp_list);
		elv.setAdapter(expandListAdapter);

		connectionHandler = new Handler() {

			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle mbb = msg.getData();
				if (mbb != null) {
					conGroups = (ArrayList<ConnectionGroup>) mbb
							.getSerializable("networkKey");
				}
				// txtview.setText("Monitor Fragment : " +
				// Integer.toString(mbb.getInt("Num")));
				// if (mbb.getBoolean("DataChanged")) {
				// myListAdapter.clear();
				// getAllPID();
				expandListAdapter.notifyDataSetChanged();
				// }
			}
		};

		connectionThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (!Thread.currentThread().isInterrupted()) {
						while (true) {

							// getAllPID();

							ArrayList<ConnectionGroup> networkData = getConnections();

							Bundle mb = new Bundle();
							mb.putSerializable("networkKey",
									(Serializable) networkData);
							Message mmsg = Message.obtain();
							mmsg.setData(mb);
							connectionHandler.sendMessage(mmsg);

							Thread.sleep(2000);

						}
					}
				} catch (Exception e) {
					e.getLocalizedMessage();
				}

			}// end of run
		});
		connectionThread.start();

		return v;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
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
		public void onFragmentInteraction(Uri uri);
	}

	public class ExpandListAdapter extends BaseExpandableListAdapter {

		private Context context;
		private ArrayList<ConnectionGroup> groups;

		public ExpandListAdapter(Context context,
				ArrayList<ConnectionGroup> groups) {
			this.context = context;
			this.groups = groups;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			ArrayList<ConnectionChild> chList = groups.get(groupPosition)
					.getItems();
			return chList.get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			ConnectionChild child = (ConnectionChild) getChild(groupPosition,
					childPosition);
			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) context
						.getSystemService(context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(R.layout.con_child, null);
			}
			TextView txtsrc = (TextView) convertView.findViewById(R.id.txtsrc);
			txtsrc.setText(child.src);

			TextView txtspt = (TextView) convertView.findViewById(R.id.txtspt);
			txtspt.setText(child.spt);

			TextView txtdst = (TextView) convertView.findViewById(R.id.txtdst);
			txtdst.setText(child.dst);

			TextView txtpro = (TextView) convertView.findViewById(R.id.txtpro);
			txtpro.setText(child.pro);

			TextView txtstatus = (TextView) convertView
					.findViewById(R.id.txtstatus);
			txtstatus.setText(child.status);

			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			ArrayList<ConnectionChild> chList = groups.get(groupPosition)
					.getItems();
			return chList.size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return groups.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return groups.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			ConnectionGroup group = (ConnectionGroup) getGroup(groupPosition);
			if (convertView == null) {
				LayoutInflater inf = (LayoutInflater) context
						.getSystemService(context.LAYOUT_INFLATER_SERVICE);
				convertView = inf.inflate(R.layout.con_group, null);
			}
			TextView txtappname = (TextView) convertView
					.findViewById(R.id.txtappname);
			txtappname.setText(group.AppName);

			TextView txtpid = (TextView) convertView.findViewById(R.id.txtpid);
			txtpid.setText(group.processID);

			TextView txtsnd = (TextView) convertView.findViewById(R.id.txtsnd);
			txtsnd.setText(group.dataSND);

			TextView txtrev = (TextView) convertView.findViewById(R.id.txtrev);
			txtrev.setText(group.dataRCV);

			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

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
			ConnectionGroup connectionGroup = new ConnectionGroup();
			connectionGroup.processID = pidsTask.get(i).pid;
			pid_arlist.add(connectionGroup.processID);
			connectionGroup.setItems(util.getPIDConnections(Integer.toString(connectionGroup.processID)));
//			connectionObj.connections = "";
//			for (Utilities.Connection conObj : connectionObj.con) {
//				connectionObj.connections = connectionObj.connections
//						+ conObj.src + ":" + conObj.spt + "|" + conObj.dst
//						+ ":" + conObj.dpt + " " + conObj.pro + "\n";
//			}
//			connectionObj.connections = connectionObj.connections.trim();// trim
//																			// traling
//																			// white
//																			// and
//																			// newline
			CharSequence app_name;
//			if (connectionObj.connections == null) {
//				connectionObj.connections = "No Network Connection";
//			}
			try {
				app_name = pm.getApplicationLabel(pm.getApplicationInfo(
						pidsTask.get(i).processName,
						PackageManager.GET_META_DATA));
				// app_name = pm.getNameForUid(pidsTask.get(i).uid);

			} catch (Exception ex) {
				// Log.w("NEOM:Error", ex.toString(), ex);
				String[] split_dot = pidsTask.get(i).processName.split("\\.");
				app_name = split_dot[split_dot.length - 1];
				String[] split_colon = app_name.toString().split("\\:");
				app_name = split_colon[split_colon.length - 1];

				// app_name = "Error";
			} // End of Catch
			connectionGroup.AppName = app_name.toString();
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


	
	
}
