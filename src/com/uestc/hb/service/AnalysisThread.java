package com.uestc.hb.service;

import com.uestc.hb.common.BluetoothConst;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class AnalysisThread extends Thread {
	public static final String TAG=AnalysisThread.class.getName();
	public Handler handler;
	
	public AnalysisThread() {
		
	}
	@Override
	public void run() {
		Looper.prepare();
		handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case BluetoothConst.MESSAGE_BIND_SUCCESS:
					Log.i(TAG, "BIND_SUCCESS");
					break;
				case BluetoothConst.MESSAGE_CONNECTED_ERROR:
					Log.i(TAG, "CONNECTED_ERROR");
					break;
				case BluetoothConst.MESSAGE_DATA:
					//TODO 处理一下接收到的数据
					break;
				default:
					break;
				}
			}
		};
		Looper.loop();
	}
}
