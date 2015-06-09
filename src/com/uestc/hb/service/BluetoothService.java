package com.uestc.hb.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import com.uestc.hb.R;
import com.uestc.hb.common.BluetoothConst;
import com.uestc.hb.ui.PairActivity;
import com.uestc.hb.utils.NotifyUtil;
import com.uestc.hb.utils.ToolUtil;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BluetoothService extends Service {
	private static final String TAG = BluetoothService.class.getName();
	private static final String SERVICE = "BTService";

	private BluetoothAdapter mBluetoothAdapter;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	private AnalysisThread mAnalysisThread;
	private static Handler mHandler = null;

	private final IBinder mBinder = new LocalBinder();

	private BroadcastReceiver serviceReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			switch (action) {
			case BluetoothConst.ACTION_SERVICE_CANCEL_CONNECT:
				trace("断开连接");
				stop();
				break;
			case BluetoothAdapter.ACTION_STATE_CHANGED:
				int state = intent
						.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
				if (BluetoothAdapter.STATE_TURNING_OFF == state) {
					Intent i = new Intent(BluetoothService.this,
							PairActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					NotifyUtil.toNotify(BluetoothService.this,
							R.drawable.ic_girl, "蓝牙被改变", 1, i,
							"蓝牙被关闭，HeartBeat无法正常工作");
					stop();
				}
				break;
			default:
				trace("异常");
				break;
			}
		}
	};

	public synchronized void connectToDevice(BluetoothDevice device) {
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		mConnectThread = new ConnectThread(device);
		mConnectThread.start();
	}

	public synchronized void stop() {
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		if (mBluetoothAdapter != null) {
			mBluetoothAdapter.cancelDiscovery();
		}
		stopSelf();
	}

	private synchronized void manageConnectedSocket(BluetoothSocket mmSocket) {
		mConnectedThread = new ConnectedThread(mmSocket);
		mConnectedThread.start();
		mAnalysisThread = new AnalysisThread();
		mAnalysisThread.start();
		sendBroadcast(BluetoothConst.ACTION_PAIR_CONNECTED);
	}

	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;

		public ConnectThread(BluetoothDevice device) {
			BluetoothSocket tmp = null;
			try {
				tmp = device
						.createRfcommSocketToServiceRecord(BluetoothConst.MY_UUID);
			} catch (IOException e) {
				e.printStackTrace();
			}
			mmSocket = tmp;
		}

		@Override
		public void run() {
			Log.i(TAG, "ConnectThread run");
			mBluetoothAdapter.cancelDiscovery();
			trace("mmSocket--" + mmSocket);
			try {
				mmSocket.connect();
			} catch (IOException e) {
				trace("异常--" + e.toString());
				sendBroadcast(BluetoothConst.ACTION_PAIR_NOT_FOUND);
				try {
					mmSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return;

			}
			manageConnectedSocket(mmSocket);
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;

		public ConnectedThread(BluetoothSocket socket) {
			mmSocket = socket;
			InputStream tmpIn = null;
			// Get the input and output streams, using temp objects because
			// member streams are final
			try {
				tmpIn = socket.getInputStream();
			} catch (IOException e) {
			}

			mmInStream = tmpIn;
		}

		public void run() {
			byte[] buffer = new byte[1024];
			try {
				while (mmInStream.read(buffer) != -1) {
					float data = ToolUtil.getFloat(buffer);
					sendDataMessage(data);
				}
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(BluetoothService.this, "输入流读完了",
								Toast.LENGTH_SHORT).show();
					}
				});
				Log.i(TAG, "输入流读完了");
				sendStateMessage(BluetoothConst.MESSAGE_CONNECTED_ERROR);
			} catch (IOException e) {
				Log.i(TAG, e.toString());
				sendStateMessage(BluetoothConst.MESSAGE_CONNECTED_ERROR);
			}
		}

		/* Call this from the main activity to shutdown the connection */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

	private void registReceiver() {
		IntentFilter filter = new IntentFilter();
		// 取消连接
		filter.addAction(BluetoothConst.ACTION_SERVICE_CANCEL_CONNECT);
		// 系统状态
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		trace("注册广播");
		registerReceiver(serviceReceiver, filter);
	}

	private void sendBroadcast(String action) {
		Intent intent = new Intent();
		intent.setAction(action);
		sendBroadcast(intent);
	}

	private void sendDataMessage(float data) {
		if (mHandler != null) {
			mHandler.obtainMessage(BluetoothConst.MESSAGE_DATA, data)
					.sendToTarget();
		}
		mAnalysisThread.handler
				.obtainMessage(BluetoothConst.MESSAGE_DATA, data)
				.sendToTarget();
	}

	private void sendStateMessage(int what) {
		if (mHandler != null) {
			mHandler.obtainMessage(what).sendToTarget();
		}
		mAnalysisThread.handler.obtainMessage(what).sendToTarget();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		registReceiver();
	}

	@Override
	public IBinder onBind(Intent intent) {
		sendStateMessage(BluetoothConst.MESSAGE_BIND_SUCCESS);
		return mBinder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		BluetoothDevice device=intent.getParcelableExtra(PairActivity.SELECTED_DEVICE);
		connectToDevice(device);
		trace("service开始连接设备-- "+device.getName());
		return START_STICKY;
	}

	@Override
	public boolean stopService(Intent name) {
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		mBluetoothAdapter.cancelDiscovery();
		return super.stopService(name);
	}

	@Override
	public void onDestroy() {
		Log.d(SERVICE, "Destroyed");
		super.onDestroy();
		unregisterReceiver(serviceReceiver);
	}

	public class LocalBinder extends Binder {
		public BluetoothService getService() {
			return BluetoothService.this;
		}

		public void setHandler(Handler handler) {
			trace("setHandler");
			BluetoothService.mHandler = handler;
		}
	}

	// 用来追踪状态信息
	public void trace(String msg) {
		Log.e(TAG, msg);
	}
}
