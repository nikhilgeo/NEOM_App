package com.nikhil.neom;

import android.provider.BaseColumns;

public class iptablesDBContract {
	// To prevent someone from accidentally instantiating the contract class,
	// give it an empty constructor.
	public iptablesDBContract() {
	}

	/* Inner class that defines the table contents */
	public static abstract class iptblrule implements BaseColumns {
		public static final String TABLE_NAME = "iptblrules";
		public static final String COLUMN_NAME_UID = "uid";
		public static final String COLUMN_NAME_RULE = "rule";
	}

	/* Inner class that defines the table contents */
	public static abstract class iptblruleSSID implements BaseColumns {
		public static final String TABLE_NAME = "iptblrulesSSID";
		public static final String COLUMN_NAME_UID = "uid";
		public static final String COLUMN_NAME_RULE = "rule";
		public static final String COLUMN_NAME_SSID = "ssid";
	}
	/* Inner class that defines the table contents */
	public static abstract class ssidInfo implements BaseColumns {
		public static final String TABLE_NAME = "ssidInfo";
		public static final String COLUMN_NAME_SSID = "ssid";
		public static final String COLUMN_NAME_ACTIVE = "active";
	}
}
