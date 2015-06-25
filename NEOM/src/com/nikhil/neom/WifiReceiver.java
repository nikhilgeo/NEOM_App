package com.nikhil.neom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class WifiReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		NetworkInfo info = intent
				.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
		if (info != null) {
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			String ssid = wifiInfo.getSSID();
			if (info.isConnected()) {

				Toast.makeText(context, "Connected to WiFi " + ssid,
						Toast.LENGTH_SHORT).show();
				// fetch the rules for SSID from DB
				// APPEND the rules to iptable

			}
			if (!info.isConnected()) {
				// When ever disconnected remove the applied WiFi
				// context----Save some flag in DB to knw if rules are
				// on--remove rules only if they are applied.

				Toast.makeText(context, "Disconnected from WiFi " + ssid,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

}
