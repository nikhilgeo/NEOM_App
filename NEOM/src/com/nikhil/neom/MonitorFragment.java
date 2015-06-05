package com.nikhil.neom;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ListFragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug.MemoryInfo;
import android.os.Handler;
import android.os.Message;
import android.app.Fragment;
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
import android.widget.TextView;
import android.widget.Toast;

import com.nikhil.neom.Utilities.Connection;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.Command;
import com.stericson.RootTools.execution.CommandCapture;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link MonitorFragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link MonitorFragment#newInstance} factory method to create an instance of
 * this fragment.
 */

public class MonitorFragment extends ListFragment implements
		OnItemClickListener {

	private List<Model_Process> process_list = new ArrayList<Model_Process>();
	private static final String ARG_SECTION_NUMBER = "section_number";
	private String section_number;
	private Thread monitorThread, execmdThread;
	private Handler handler;
	private int counter = 0; // For test TBR

	private MyListAdapter myListAdapter;

	/*
	 * static { System.loadLibrary("ndksetup"); // ndksetup is the LOCAL_MODULE
	 * string. }
	 * 
	 * // Call to native function private native void pidinodepname();
	 * 
	 * private native int fibonacci(int value);
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_monitor, container,
				false);

		// pidinodepname();

		// pid_inode_pname();

		// region monitoring : NG

		return rootView;

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

		myListAdapter = new MyListAdapter(getActivity(),
				R.layout.process_list_item, process_list);

		setListAdapter(myListAdapter);
		getListView().setOnItemClickListener(this);

		handler = new Handler() {

			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle mbb = msg.getData();
				if (mbb != null) {
					process_list = (List<Model_Process>) mbb
							.getSerializable("processKey");
				}
				// txtview.setText("Monitor Fragment : " +
				// Integer.toString(mbb.getInt("Num")));
				// if (mbb.getBoolean("DataChanged")) {
				// myListAdapter.clear();
				// getAllPID();
				myListAdapter.notifyDataSetChanged();
				// }
			}
		};

		monitorThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (!Thread.currentThread().isInterrupted()) {
						while (true) {

							// getAllPID();

							List<Model_Process> processData = getJavaProcessPID();

							Bundle mb = new Bundle();
							mb.putSerializable("processKey",
									(Serializable) processData);
							Message mmsg = Message.obtain();
							mmsg.setData(mb);
							handler.sendMessage(mmsg);

							Thread.sleep(1000);

						}
					}
				} catch (Exception e) {
					e.getLocalizedMessage();
				}

			}// end of run

		});
		monitorThread.start();

	}

	@Override
	public void onPause() {
		super.onPause();
		monitorThread.interrupt();
	}

	/*
	 * Custom Adapter to bind custom row layout Change the ArrayAdapter<T>
	 * according to the type you want to populate the row
	 */
	public class MyListAdapter extends ArrayAdapter<Model_Process> {

		Context myContext;

		public MyListAdapter(Context context, int textViewResourceId,
				List<Model_Process> objects) {
			super(context, textViewResourceId, objects);
			myContext = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) myContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row = inflater.inflate(R.layout.process_list_item, parent,
					false);

			// For alternate row color
			if (position % 2 == 1) {
				row.setBackgroundColor(Color.TRANSPARENT);
			} else {
				row.setBackgroundColor(Color.WHITE);
			}

			if (process_list != null) {
				Model_Process processItem = process_list.get(position);
				TextView txtappname = (TextView) row
						.findViewById(R.id.txtappname);
				txtappname.setText(processItem.AppName);
				TextView txtpid = (TextView) row.findViewById(R.id.txtpid);
				txtpid.setText(Integer.toString(processItem.processID));
				TextView pname = (TextView) row.findViewById(R.id.txtpname);
				pname.setText(processItem.processName);
				TextView uid = (TextView) row.findViewById(R.id.txtuid);
				uid.setText(Integer.toString(processItem.userID));
				TextView txtpss = (TextView) row.findViewById(R.id.txtpss);
				txtpss.setText(Float.toString(processItem.memInfo[0]) + "MB");
				TextView txtpvtdirty = (TextView) row
						.findViewById(R.id.txtpvtdirty);
				txtpvtdirty.setText(Float.toString(processItem.memInfo[1])
						+ "MB");
				TextView txtshrdirty = (TextView) row
						.findViewById(R.id.txtshrdirty);
				txtshrdirty.setText(Float.toString(processItem.memInfo[2])
						+ "MB");
			}
			return row;
		}
	}

	/*
	 * private String getProcessName(String pid) { String processName; pro
	 * 
	 * return "processName"; }
	 */

	// -------------------------------------------------------------------------------
	// ------------------------Custom_Methods--------------------------------------
	// ------------------------------------------------------------------------------

	private List<Model_Process> getJavaProcessPID() {

		try {
			Utilities utilities = new Utilities();
			final PackageManager pm = getActivity().getPackageManager();
			ActivityManager activityManager = (ActivityManager) getActivity()
					.getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningAppProcessInfo> pidsTask = activityManager
					.getRunningAppProcesses();
			ArrayList<Integer> pid_arlist = new ArrayList<Integer>();
			for (int i = 0; i < pidsTask.size(); i++) {
				Model_Process processObj = new Model_Process();
				processObj.processID = pidsTask.get(i).pid;
				pid_arlist.add(processObj.processID);

				// ArrayList<Connection> con = utilities
				// .getConnections(processObj.processID.toString());

				processObj.memInfo = getProcessMemInfo(pidsTask.get(i).pid);
				processObj.processName = pidsTask.get(i).processName.split(":")[0];
				processObj.userID = pidsTask.get(i).uid;

				CharSequence app_name;
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
				}
				processObj.AppName = app_name.toString();

				if (process_list != null && isDuplicate(processObj))
					continue;
				process_list.add(processObj);
			}// End of For loop
			removeExpiredPID(pid_arlist);
		} catch (Exception ex) {
			Log.w("NEOM:Error", ex.toString(), ex);
		}

		return process_list;
	}

	/*
	 * Check if PID already exist in the List before adding
	 */
	private Boolean isDuplicate(Model_Process processObj) {
		int replacePos = -1;
		for (Model_Process pro : process_list) {
			if (pro.processID.equals(processObj.processID)) {
				replacePos = process_list.indexOf(pro);
				break;
			}
		}
		if (replacePos != -1) {
			process_list.set(replacePos, processObj);
			return true;
		}
		return false;

	}

	/*
	 * Remove the stopped process from the process list
	 */
	private void removeExpiredPID(ArrayList<Integer> pids) {

		for (Iterator<Model_Process> iterator = process_list.iterator(); iterator
				.hasNext();) {
			Model_Process pro = iterator.next();
			if (!pids.contains(pro.processID))
				iterator.remove();
		}

	}

	private float[] getProcessMemInfo(int PID) {
		ActivityManager activityManagerMEM = (ActivityManager) getActivity()
				.getSystemService(Context.ACTIVITY_SERVICE);
		float[] memInfoModel = new float[3];
		MemoryInfo mi[];// = new MemoryInfo();

		mi = activityManagerMEM.getProcessMemoryInfo(new int[] { PID });
		memInfoModel[0] = mi[0].getTotalPss() / 1000;// In KB
		memInfoModel[1] = mi[0].getTotalPrivateDirty() / 1000;
		memInfoModel[2] = mi[0].getTotalSharedDirty() / 1000;
		return memInfoModel;

	}

	
	// -------------------------------------------------------------------------------
	// ------------------------Factory_Methods--------------------------------------
	// -------------------------------------------------------------------------------

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @param sectionNumber
	 *            Parameter 1.
	 * @return A new instance of fragment MonitorFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static MonitorFragment newInstance() {
		MonitorFragment fragment = new MonitorFragment();
		/*
		 * Bundle args = new Bundle(); args.putInt(ARG_SECTION_NUMBER,
		 * sectionNumber); fragment.setArguments(args);
		 */
		return fragment;
	}

	public MonitorFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			// section_number = getArguments().getInt(ARG_SECTION_NUMBER);
			// But can't access this section_number from onCreateView
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		/*
		 * try { mListener = (OnFragmentInteractionListener) activity; } catch
		 * (ClassCastException e) { throw new
		 * ClassCastException(activity.toString() +
		 * " must implement OnFragmentInteractionListener"); }
		 */
	}

	@Override
	public void onDetach() {
		super.onDetach();
		// mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
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

}
