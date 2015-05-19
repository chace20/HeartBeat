package com.uestc.hb.db;

public class LogContract {
	public static final String DATABASE_NAME="heartbeat.db";
	public static final int DATABASE_VERSION=1;
	
	public interface Tables{
		String ALARM_LOG="alarm_log";
	}
	public interface AlarmColumns{
		String ID="id";
		String DATE="date";
		String STATE="state";
		String HR="hr";
	}
	
}
