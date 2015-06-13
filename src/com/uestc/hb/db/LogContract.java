package com.uestc.hb.db;

public class LogContract {
	public static final String DB_ACTION = "db_action";
	
	public static final String DATABASE_NAME="heartbeat.db";
	public static final int DATABASE_VERSION=1;
	
	public interface Tables{
		String DB_TABLE_DOC = "hb_documents";
		String DB_TABLE_SUFFER = "hb_suffer";
		String DB_TABLE_ILLNESS = "hb_illness";
	}
	public interface Columns{
		String KEY_ID = "docs_id";
		String KEY_DOC_DATA = "hr_data";
		String KEY_DOC_DATE = "date";
		String KEY_DOC_RATE = "heart_rate";
		
		String KEY_SUFFER_ID = "suffer_id";
		
		String KEY_ILLNESS_ID = "ill_id";
		String KEY_ILLNESS_NAME = "name";
		String KEY_ILLNESS_DESC = "description";
		String KEY_ILLNESS_ADVICE = "advice";
	}
	
}
