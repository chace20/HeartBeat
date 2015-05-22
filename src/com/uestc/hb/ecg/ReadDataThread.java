package com.uestc.hb.ecg;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Looper;

public class ReadDataThread extends Thread {

	public Handler mHandler;
	private ArrayList<Number> data;

	private final int SEND_DATA = 1;

	@Override
	public void run() {

		Looper.prepare();

		mHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {

				int what = msg.what;

				switch (what) {

				case SEND_DATA:
					data.add((Number) msg.obj);
					break;
				default:
					super.handleMessage(msg);
				}
			}
		};
		
		Looper.loop();
	}
	
	public float getData(){
		
		float temp = -1;
		try{
			temp = (Float) data.get(0);
			data.remove(0);
			return temp;
		}catch(Exception e){
			return temp;
		}
	}
}
