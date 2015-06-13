package com.uestc.hb.db;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseAdapter {

	public static final String DB_ACTION = "db_action";

	public static final String DB_NAME = "heartbeat.db";
	public static final String DB_TABLE_DOC = "hb_documents";
	public static final String DB_TABLE_SUFFER = "hb_suffer";
	public static final String DB_TABLE_ILLNESS = "hb_illness";
	public static final String DB_VERSION = "1";

	public static final String KEY_ID = "docs_id";

	public static final String KEY_DOC_DATA = "hr_data";
	public static final String KEY_DOC_DATE = "date";
	public static final String KEY_DOC_RATE = "heart_rate";

	public static final String KEY_SUFFER_ID = "suffer_id";

	public static final String KEY_ILLNESS_ID = "ill_id";
	public static final String KEY_ILLNESS_NAME = "name";
	public static final String KEY_ILLNESS_DESC = "description";
	public static final String KEY_ILLNESS_ADVICE = "advice";

	public static final String QUERY_ALL_0 = "SELECT "
			+ "ill_id,name,"
			+ "description,advice "
			+ "FROM hb_illness " + "WHERE ill_id in (select ill_id from hb_suffer where date = ";
	public static final String QUERY_ALL_1 = " )";

	private Context context;
	private static SQLiteDatabase db;
	private SQLiteOpenHelper hbOpenHelper;

	public DataBaseAdapter(Context context) {
		this.context = context;
	}

	public void open() throws SQLException {
		hbOpenHelper = new HBDataBaseHelper(context, DB_NAME, null, 1);
		try {
			db = hbOpenHelper.getWritableDatabase();
		} catch (SQLException e) {
			db = hbOpenHelper.getReadableDatabase();
		}
	}

	public void close() {
		if (db != null) {
			db.close();
			db = null;
		}
	}

	public static long insert(long date, String data, List<Integer> list,
			int heartRate) {
		ContentValues docValues = new ContentValues();
		docValues.put(KEY_DOC_DATA, data);
		docValues.put(KEY_DOC_DATE, date);
		docValues.put(KEY_DOC_RATE, heartRate);

		long result = 0;

		Log.i("heart_rate", "---"+list.size());
		for (int i = 0; i < list.size(); i++) {
			ContentValues sufferValues = new ContentValues();
			sufferValues.put(KEY_DOC_DATE, date);
			sufferValues.put(KEY_ILLNESS_ID, list.get(i));
			result += db.insert(DB_TABLE_SUFFER, null, sufferValues);
		}

		return (result + db.insert(DB_TABLE_DOC, null, docValues));
	}

	public static long store(long date, String data, List<Integer> list,
			int heartRate) {
		return insert(date, data, list, heartRate);
	}

	public long deleteOneDoc(Long date) {
		return db.delete(DB_TABLE_DOC, KEY_DOC_DATE + "=" + date, null)
				+ db.delete(DB_TABLE_SUFFER, KEY_DOC_DATE + "=" + date, null);
	}

	public long deleteAllDoc() {
		return db.delete(DB_TABLE_DOC, null, null)
				+ db.delete(DB_TABLE_SUFFER, null, null);
	}

	public Cursor queryDoc() {
		return db.query(DB_TABLE_DOC, new String[] { KEY_DOC_DATE,
				KEY_DOC_DATA, KEY_DOC_RATE }, null, null, null, null, null);
	}

	public Cursor queryDetailsDoc(long date) {
		return db.query(DB_TABLE_DOC, new String[] { KEY_DOC_DATA,
				KEY_DOC_DATE, KEY_DOC_RATE }, KEY_DOC_DATE+ "=" + date, null, null, null, null);
	}

	public Cursor queryDetailsIll(long date) {
		return db.rawQuery(QUERY_ALL_0 + date + QUERY_ALL_1, null);
	}

}
