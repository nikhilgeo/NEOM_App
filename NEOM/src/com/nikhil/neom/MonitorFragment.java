package com.nikhil.neom;

import java.io.File;
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
import com.stericson.RootTools.RootTools;

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
	private Thread monitorThread, hashTableThread;
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

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				// Bundle mbb = msg.getData();
				// txtview.setText("Monitor Fragment : " +
				// Integer.toString(mbb.getInt("Num")));
				// if (mbb.getBoolean("DataChanged")) {
				// myListAdapter.clear();
				getJavaProcessPID();
				myListAdapter.notifyDataSetChanged();
				// }
			}
		};

		monitorThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {

						Thread.sleep(500);

						// Bundle mb = new Bundle();
						// mb.putBoolean("DataChanged", true);
						// Message mmsg = Message.obtain();
						// mmsg.setData(mb);
						//
						// handler.sendMessage(mmsg);
						handler.sendEmptyMessage(0);

					}
				} catch (Exception e) {
					e.getLocalizedMessage();
				}

			}// end of run
		});
		monitorThread.start();

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

			Model_Process processItem = process_list.get(position);
			TextView packgname = (TextView) row.findViewById(R.id.txtpackgname);
			packgname.setText(processItem.packageList);
			TextView txtpid = (TextView) row.findViewById(R.id.txtpid);
			txtpid.setText(Integer.toString(processItem.processID));
			TextView pname = (TextView) row.findViewById(R.id.txtpname);
			pname.setText(processItem.processName);
			TextView uid = (TextView) row.findViewById(R.id.txtuid);
			uid.setText(Integer.toString(processItem.userID));
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
	// -------------------------------------------------------------------------------

	private void getAllPID() {
		try {
			String regex = "\\d+";
			Pattern p = Pattern.compile(regex);
			File proc_dir = new File("/proc");
			String[] content_list = proc_dir.list();
			for (String name : content_list) {
				if (new File("/proc/" + name).isDirectory()) {
					Matcher m = p.matcher(name);
					if (m.matches()) {
						Log.i("NEOM in getPID", name);
						if (RootTools.isAccessGiven())
							Log.i("NEOM in getPID", new File("/proc/" + name
									+ "/exe").getCanonicalPath());
						// getProcessName(name)
					}
				}
			}
		} catch (Exception ex) {
			Log.i("NEOM in getPID", ex.toString());
		}

	}

	private void getJavaProcessPID() {
		ActivityManager activityManager = (ActivityManager) getActivity()
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> pidsTask = activityManager
				.getRunningAppProcesses();
		ArrayList<Integer> pid_arlist = new ArrayList<Integer>();
		for (int i = 0; i < pidsTask.size(); i++) {
			Model_Process processObj = new Model_Process();
			processObj.processID = pidsTask.get(i).pid;
			pid_arlist.add(processObj.processID);

			// getProcessMemInfo(pidsTask.get(i).pid);
			processObj.processName = pidsTask.get(i).processName;
			processObj.userID = pidsTask.get(i).uid;
			for (String pkg : pidsTask.get(i).pkgList)
				processObj.packageList = processObj.packageList + pkg + "\n";
			processObj.packageList = processObj.packageList.substring(0,
					processObj.packageList.length() - 1);
			if (!process_list.isEmpty())
				if (isDuplicate(processObj.processID))
					continue;
			process_list.add(processObj);
		}// End of For loop
		removeExpiredPID(pid_arlist);
	}

	/*
	 * Check if PID already exist in the List before adding
	 */
	private Boolean isDuplicate(int PID) {
		for (Model_Process pro : process_list) {
			if (pro.processID == PID)
				return true;
		}
		return false;
	}

	/*
	 * Remove the stopped process from the process list
	 */
	private void removeExpiredPID(ArrayList<Integer> pids) {
		// for (Model_Process pro : process_list) {
		// if (!pids.contains(pro.processID))
		// process_list.remove(pro);
		// }
		for (Iterator<Model_Process> iterator = process_list.iterator(); iterator
				.hasNext();) {
			Model_Process pro = iterator.next();
			if (!pids.contains(pro.processID))
				iterator.remove();
		}

	}

	private void getProcessMemInfo(int PID) {
		ActivityManager activityManagerMEM = (ActivityManager) getActivity()
				.getSystemService(Context.ACTIVITY_SERVICE);
		int ar[];

		MemoryInfo mi[];// = new MemoryInfo();

		mi = activityManagerMEM.getProcessMemoryInfo(new int[] { PID });

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
