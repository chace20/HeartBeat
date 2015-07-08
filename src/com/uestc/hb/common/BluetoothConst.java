package com.uestc.hb.common;

import java.util.UUID;

public class BluetoothConst {

	//测试用，写死了的mac地址
	public static final String MAC_ADDRESS="80:CF:41:D7:19:84";
	
	public static final String MY_SDP = "heartbeat_bluetooth";
	public static final UUID MY_UUID = UUID
			.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
	
	/*
	 * handler what value
	 */
	public static final int MESSAGE_DATA=1;
	public static final int MESSAGE_CONNECTED_ERROR=2;
	public static final int MESSAGE_BIND_SUCCESS=3;
	public static final int MESSAGE_HEART_RATE=4;
	public static final int MESSAGE_ALARM=5;
	
	/*
	 * broadcast action value
	 */	
	//配对状态信息
	public static final String ACTION_PAIR_NOT_FOUND="com.uestc.hb.ACTION_PAIR_NOT_FOUND";
	public static final String ACTION_PAIR_CONNECTED="com.uestc.hb.ACTION_PAIR_CONNECTED";
	//断开连接
	public static final String ACTION_SERVICE_CANCEL_CONNECT="com.uestc.hb.ACTION_SERVICE_CANCEL_CONNECT";
	
	/*
	 * intent action value
	 */
	//配对成功或者失败
	public static final String INTENT_STATE_SUCCESS="com.uestc.hb.ACTION_STATE_SUCCESS";
	public static final String INTENT_STATE_FAILED="com.uestc.hb.ACTION_STATE_FAILED";
	//重新连接
	public static final String INTENT_RETRY_PAIR="com.uestc.hb.ACTION_RETRY_PAIR";

}
