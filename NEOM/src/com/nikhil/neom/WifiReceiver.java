package com.nikhil.neom;

import java.util.ArrayList;

import org.apache.http.impl.client.TunnelRefusedException;

import com.nikhil.neom.iptablesDBContract.iptblrule;
import com.nikhil.neom.iptablesDBContract.iptblruleSSID;
import com.nikhil.neom.iptablesDBContract.ssidInfo;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class WifiReceiver extends BroadcastReceiver {
	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		NetworkInfo info = intent
				.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
		neomDbHelper mDbHelper = new neomDbHelper(context);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		if (info != null) {
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			String ssid = wifiInfo.getSSID();
			if (info.isConnected()) {

				Toast.makeText(context, "Connected to WiFi " + ssid,
						Toast.LENGTH_SHORT).show();
				check_applyRules(ssid);

			}
			// When disconnected
			if (!info.isConnected()) {

				// Check if any rules where applied
				if (checkSSID_Exist(ssid)) {
					// Remove the rules
					ArrayList<String> block_rules_arlist = new ArrayList<String>();
					block_rules_arlist = getRulesForSSID(ssid);
					ArrayList<String> block_rules_arlisttoDel = new ArrayList<String>();

					// Create Delete rules
					for (String rule : block_rules_arlist) {
						block_rules_arlisttoDel.add(rule.replace("-A", "-D"));
					}

					String block_rules_ar[] = block_rules_arlisttoDel
							.toArray(new String[block_rules_arlisttoDel.size()]);
					ExecuteCMD executeCMD = new ExecuteCMD();
					executeCMD.RunAsRoot(block_rules_ar);

					// Update rule appled into ssidInfo table.
					ContentValues cv = new ContentValues();
					cv.put(ssidInfo.COLUMN_NAME_ACTIVE, "N");
					String whereClause = ssidInfo.COLUMN_NAME_SSID + "=" + "?";
					db.update(ssidInfo.TABLE_NAME, cv, whereClause,
							new String[] { ssid });

				}

				Toast.makeText(context, "Rules for  " + ssid + "Removed..",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/*
	 * Check any rules for the WiFi network from which device is dis-connected
	 * have any rules applied for it.
	 */
	private boolean checkSSID_Exist(String wifiSSID) {

		neomDbHelper mDbHelper = new neomDbHelper(context);
		SQLiteDatabase db = mDbHelper.getReadableDatabase();

		String[] projection = { ssidInfo.COLUMN_NAME_SSID,
				ssidInfo.COLUMN_NAME_ACTIVE };
		String whereClause = ssidInfo.COLUMN_NAME_SSID + "=" + "?";
		Cursor cursor = db.query(ssidInfo.TABLE_NAME, // The table to
				// // query
				projection, // The columns to return
				whereClause, // The columns for the WHERE clause
				new String[] { wifiSSID },// The sort order
				null, null, null);
		if (cursor.moveToFirst()) {

			String isActive = cursor.getString(cursor
					.getColumnIndexOrThrow(ssidInfo.COLUMN_NAME_ACTIVE));
			if (isActive.equals("Y"))
				return true;
			else
				return false;
		} else
			return true;

	}

	/*
	 * Check if rules for connected WiFi N/W exist. If yes apply them
	 */
	private void check_applyRules(String wifiSSID) {
		neomDbHelper mDbHelper = new neomDbHelper(context);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		String applyRules[];
		ArrayList<String> block_rules_arlist = new ArrayList<String>();

		block_rules_arlist = getRulesForSSID(wifiSSID);

		if (block_rules_arlist != null && !block_rules_arlist.isEmpty()) {
			String block_rules_ar[] = block_rules_arlist
					.toArray(new String[block_rules_arlist.size()]);

			// Apply rules to iptables
			ExecuteCMD executeCMD = new ExecuteCMD();
			executeCMD.RunAsRoot(block_rules_ar);

			// Update rule appled into ssidInfo table.
			ContentValues cv = new ContentValues();
			cv.put(ssidInfo.COLUMN_NAME_ACTIVE, "Y");
			String whereClause = ssidInfo.COLUMN_NAME_SSID + "=" + "?";
			db.update(ssidInfo.TABLE_NAME, cv, whereClause,
					new String[] { wifiSSID });
			Toast.makeText(
					context,
					Integer.toString(block_rules_ar.length) + "Rules for  "
							+ wifiSSID + " applied..", Toast.LENGTH_SHORT)
					.show();
		}

	}

	private ArrayList<String> getRulesForSSID(String wifiSSID) {
		neomDbHelper mDbHelper = new neomDbHelper(context);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ArrayList<String> block_rules_arlist = new ArrayList<String>();

		try {
			String[] projection = { iptblruleSSID.COLUMN_NAME_RULE };
			String whereClause = iptblruleSSID.COLUMN_NAME_SSID + "=" + "?";
			Cursor cursor = db.query(iptblruleSSID.TABLE_NAME, // The table to
					// // query
					projection, // The columns to return
					whereClause, // The columns for the WHERE clause
					new String[] { wifiSSID },// The sort order
					null, null, null);
			if (cursor.moveToFirst()) {
				do {
					String rule = cursor
							.getString(cursor
									.getColumnIndexOrThrow(iptblruleSSID.COLUMN_NAME_RULE));
					block_rules_arlist.add(rule);
				} while (cursor.moveToNext());
			}

			return block_rules_arlist;
		} catch (Exception e) {
			return null;
		}

	}

}
