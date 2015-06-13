package com.uestc.hb.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.SyncStateContract.Columns;
import android.util.Log;

import com.uestc.hb.common.IllnessType;
import com.uestc.hb.db.LogContract.Tables;

public class HBDataBaseHelper extends SQLiteOpenHelper implements Tables,
		Columns {

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

	public static final String DB_CREATE_DOC = "CREATE TABLE " + DB_TABLE_DOC
			+ " (" + KEY_ID + " integer primary key autoincrement, "
			+ KEY_DOC_DATE + " long not null, " + KEY_DOC_RATE
			+ " integer not null," + KEY_DOC_DATA + " text not null)";

	public static final String DB_CREATE_ILLNESS = "CREATE TABLE "
			+ DB_TABLE_ILLNESS + "(" + KEY_ILLNESS_ID
			+ " integer primary key, " + KEY_ILLNESS_NAME + " text not null, "
			+ KEY_ILLNESS_DESC + " text not null," + KEY_ILLNESS_ADVICE
			+ " text not null)";

	public static final String DB_CREATE_SUFFER = "CREATE TABLE "
			+ DB_TABLE_SUFFER + " (" + KEY_SUFFER_ID
			+ " integer primary key autoincrement, " + KEY_ILLNESS_ID
			+ " integer not null," + "date long not null,"
			+ " constraint ill_id_fk foreign key (ill_id) references "
			+ DB_TABLE_ILLNESS + "(" + KEY_ILLNESS_ID + ") ,"
			+ " constraint date_fk foreign key (date) references "
			+ DB_TABLE_DOC + "(" + KEY_DOC_DATE + "))";

	public HBDataBaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DB_CREATE_DOC);
		db.execSQL(DB_CREATE_ILLNESS);
		db.execSQL(DB_CREATE_SUFFER);

		IllnessType illnessType = new IllnessType();
		List<Integer> list = new ArrayList<Integer>();
		list = getIllnessType();

		for (int i = 0; i < list.size(); i++) {
			ContentValues illValues = new ContentValues();
			illValues.put(KEY_ILLNESS_ID, list.get(i));
			illValues.put(KEY_ILLNESS_NAME,
					IllnessType.getIllnessTypeString(i + 1));
			illValues.put(KEY_ILLNESS_DESC, "病情需要进一步检查");
			illValues.put(KEY_ILLNESS_ADVICE, "建议您去医院进行进一步的检查");
			db.insert(DB_TABLE_ILLNESS, null, illValues);
		}
		Log.i(DB_ACTION, "Create");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF  EXISTS" + DB_TABLE_DOC);
		db.execSQL("DROP TABLE IF  EXISTS" + DB_TABLE_ILLNESS);
		db.execSQL("DROP TABLE IF  EXISTS" + DB_TABLE_SUFFER);
		Log.i(DB_ACTION, "upgrade");
	}

	private List<Integer> getIllnessType() {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0x1; i < 0xd; i++) {
			list.add(i);
		}
		return list;
	}
}
