package com.nikhil.neom;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.R.string;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ListFragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Fragment;
import android.content.Context;
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

	private List<Model_Process> process_list;
	private static final String ARG_SECTION_NUMBER = "section_number";
	private String section_number;
	private Thread monitorThread, hashTableThread;
	private Handler handler;
	private int counter = 0; // For test TBR

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
		final TextView txtview = (TextView) rootView
				.findViewById(R.id.text_view);
		// pidinodepname();
		txtview.setText("In MonitorFragment");

		// pid_inode_pname();
		/*
		 * // region monitoring : NG handler = new Handler() {
		 * 
		 * @Override public void handleMessage(Message msg) {
		 * super.handleMessage(msg); Bundle mbb = msg.getData();
		 * txtview.setText("Monitor Fragment : " +
		 * Integer.toString(mbb.getInt("Num"))); } }; monitorThread = new
		 * Thread(new Runnable() {
		 * 
		 * @Override public void run() {
		 * 
		 * while (counter < 10) { counter++; Bundle mb = new Bundle();
		 * mb.putInt("Num", counter); Message mmsg = Message.obtain();
		 * mmsg.setData(mb); handler.sendMessage(mmsg); try {
		 * Thread.sleep(3000); // Do some stuff } catch (Exception e) {
		 * e.getLocalizedMessage(); }
		 * 
		 * } } }); monitorThread.start(); // endregion monitoring : NG
		 */
		return rootView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		String[] values = new String[] { "Android List View",
				"Adapter implementation", "Simple List View In Android",
				"Create List View Android", "Android Example",
				"List View Source Code", "List View Array Adapter",
				"Android Example List View" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, values);
		// name();
		getUserPID();
		//
		// ArrayAdapter(
		// getActivity(), values,
		// android.R.layout.simple_list_item_1);

		setListAdapter(adapter);
		getListView().setOnItemClickListener(this);

	}

	/*
	 * private String getProcessName(String pid) { String processName; pro
	 * 
	 * return "processName"; }
	 */

	/*
	 * Custom-------------------------------------------------------------
	 * Methods-------------------------------------------------------------
	 */

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

	private void getUserPID() {
		ActivityManager activityManager = (ActivityManager) getActivity()
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> pidsTask = activityManager
				.getRunningAppProcesses();
		for (int i = 0; i < pidsTask.size(); i++) {
			Model_Process processObj = new Model_Process();
			processObj.processID = pidsTask.get(i).pid;
			processObj.processName = pidsTask.get(i).processName;
			processObj.userID = pidsTask.get(i).uid;
			processObj.packageList = pidsTask.get(i).pkgList;
			process_list.add(processObj);
		}
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
