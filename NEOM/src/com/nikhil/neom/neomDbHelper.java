package com.nikhil.neom;

import com.nikhil.neom.iptablesDBContract.iptblrule;
import com.nikhil.neom.iptablesDBContract.iptblruleSSID;
import com.nikhil.neom.iptablesDBContract.ssidInfo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class neomDbHelper extends SQLiteOpenHelper {
	// If you change the database schema, you must increment the database
	// version.
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "neom.db";
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_ENTRIES_iptblrule = "CREATE TABLE "
			+ iptblrule.TABLE_NAME + " (" + iptblrule._ID
			+ " INTEGER PRIMARY KEY" + COMMA_SEP + iptblrule.COLUMN_NAME_UID
			+ TEXT_TYPE + COMMA_SEP + iptblrule.COLUMN_NAME_RULE + TEXT_TYPE
			+ " )";

	private static final String SQL_CREATE_ENTRIES_iptblruleSSID = "CREATE TABLE "
			+ iptblruleSSID.TABLE_NAME
			+ " ("
			+ iptblruleSSID._ID
			+ " INTEGER PRIMARY KEY"
			+ COMMA_SEP
			+ iptblruleSSID.COLUMN_NAME_UID
			+ TEXT_TYPE
			+ COMMA_SEP
			+ iptblruleSSID.COLUMN_NAME_RULE
			+ TEXT_TYPE
			+ COMMA_SEP
			+ iptblruleSSID.COLUMN_NAME_SSID + TEXT_TYPE + " )";

	private static final String SQL_CREATE_ENTRIES_ssidInfo = "CREATE TABLE "
			+ ssidInfo.TABLE_NAME + " (" + ssidInfo._ID
			+ " INTEGER PRIMARY KEY" + COMMA_SEP + ssidInfo.COLUMN_NAME_SSID
			+ TEXT_TYPE + COMMA_SEP + ssidInfo.COLUMN_NAME_ACTIVE + TEXT_TYPE
			+ " )";

	private static final String SQL_DELETE_ENTRIES_iptblrule = "DROP TABLE IF EXISTS "
			+ iptblrule.TABLE_NAME;
	private static final String SQL_DELETE_ENTRIES_iptblruleSSID = "DROP TABLE IF EXISTS "
			+ iptblruleSSID.TABLE_NAME;
	private static final String SQL_DELETE_ENTRIES_ssidInfo = "DROP TABLE IF EXISTS "
			+ ssidInfo.TABLE_NAME;

	public neomDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		try {
			Log.w("NEOM:", SQL_CREATE_ENTRIES_iptblrule);

			db.execSQL(SQL_CREATE_ENTRIES_iptblrule);
			Log.w("NEOM:", SQL_CREATE_ENTRIES_iptblrule);
			db.execSQL(SQL_CREATE_ENTRIES_iptblruleSSID);
			Log.w("NEOM:", SQL_CREATE_ENTRIES_iptblruleSSID);
			db.execSQL(SQL_CREATE_ENTRIES_ssidInfo);
		} catch (Exception ex) {
			Log.w("NEOM:", ex.toString(), ex);
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// This database is only a cache for online data, so its upgrade policy
		// is
		// to simply to discard the data and start over
		db.execSQL(SQL_DELETE_ENTRIES_iptblrule);
		db.execSQL(SQL_DELETE_ENTRIES_iptblruleSSID);
		db.execSQL(SQL_DELETE_ENTRIES_ssidInfo);
		onCreate(db);
	}

	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}
}
