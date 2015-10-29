package com.uestc.hb.analysis;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.uestc.hb.R;
import com.uestc.hb.common.BluetoothConst;
import com.uestc.hb.common.IllnessType;
import com.uestc.hb.db.DataBaseAdapter;
import com.uestc.hb.ui.AlarmLogActivity;
import com.uestc.hb.utils.NotifyUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AnalysisThread extends Thread {
	private static final String TAG = "AnalysisThread";
	public Handler handler;
	
	private Handler heartRateHandler;
	private Context context;

	private Queue<List<Float>> queue = new LinkedList<>();
	private List<Float> dataArray = new ArrayList<>();
	private int dataSize = 0;
	private int lastRIndex = 0;
	private Sorter sorter = new Sorter();

	public AnalysisThread(Context context, Handler heartRateHandler) {
		this.context = context;
		this.heartRateHandler = heartRateHandler;
	}

	// 为了防止构造函数传进来的handler为空，所以加上此方法
	public void setHeartRateHandler(Handler heartRateHandler) {
		this.heartRateHandler = heartRateHandler;
	}

	@Override
	public void run() {
		Looper.prepare();
		handler = new Handler() {
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
					AnalysisThread.this.readData((float) msg.obj);
					break;
				default:
					break;
				}
			}
		};
		Looper.loop();
	}

	private void readData(float data) {
		if (dataSize < QRSHelper.LENGTH) {
			dataArray.add(data);
		} else {
			dataSize = 0;
			queue.offer(dataArray);
			int RIndex = QRSHelper.getRIndex(dataArray);
			if (lastRIndex != 0) {
				QRSBean qrs = QRSHelper.getQRS(lastRIndex, RIndex,
						QRSHelper.getQBeginIndex(dataArray, RIndex),
						QRSHelper.getSEndIndex(dataArray, RIndex));
				sendHeartRate(QRSHelper.getHeartRate(qrs.RRInterphase));
				List<Integer> illnessList = sorter.analysis(qrs);
				if (!illnessList.isEmpty()) {
					handleHeartException(queue, illnessList,QRSHelper.getHeartRate(qrs.RRInterphase));
				}
			}
			if (queue.size() > 5) {
				queue.remove();
			}
			lastRIndex = RIndex;
			dataArray.clear();
		}
		dataSize++;
	}

	private void handleHeartException(Queue<List<Float>> queue,
			List<Integer> illnessList,int rate) {
		long currentTime = System.currentTimeMillis();
		StringBuilder builder = new StringBuilder();
		for (List<Float> list : queue) {
			for (float data : list) {
				builder.append(data + ",");
			}
		}
		String dataString = builder.toString().substring(0,
				builder.length() - 1);
		// TODO 发送异常记录给存储模块
		DataBaseAdapter.store(currentTime, dataString, illnessList, rate);
		Log.i("heart_rate", "---"+illnessList.size());
		
		StringBuilder illnessBuilder = new StringBuilder();
		for (int type : illnessList) {
			illnessBuilder.append(IllnessType.getIllnessTypeString(type) + "、");
		}
		String illnessString = "检测到"
				+ illnessBuilder.toString().substring(0,
						illnessBuilder.length() - 1);
		toNotifyAlarm(illnessString);// 通知栏显示
		sendAlarmMessage(illnessString);//发送给绘图页面
	}

	private void sendHeartRate(int heartRate) {
		if (heartRateHandler != null) {
			heartRateHandler.obtainMessage(BluetoothConst.MESSAGE_HEART_RATE,
					heartRate, 0).sendToTarget();
		}
	}
	
	private void sendAlarmMessage(String illnessString){
		if (heartRateHandler != null) {
			heartRateHandler.obtainMessage(BluetoothConst.MESSAGE_ALARM,illnessString).sendToTarget();
		}
	}
	
	private void toNotifyAlarm(String illnessString) {
		Intent i = new Intent(context, AlarmLogActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		NotifyUtil.toNotify(context, R.drawable.ic_girl, "HeartBeat发现心脏异常", 1,
				i, illnessString);
	}

}
