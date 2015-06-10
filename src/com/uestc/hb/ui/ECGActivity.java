package com.uestc.hb.ui;

import com.uestc.hb.R;
import com.uestc.hb.common.BluetoothConst;
import com.uestc.hb.ecg.NormalECGSurfaceView;
import com.uestc.hb.service.BluetoothService;
import com.uestc.hb.service.BluetoothService.LocalBinder;
import com.uestc.hb.utils.ToolUtil;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ECGActivity extends BaseActivity {

	private static final String TAG = ECGActivity.class.getName();

	private View normalView;
	private View noPairView;

	private NormalECGSurfaceView normalECGSurfaceView;

	private Button nopairButton1;
	private Button nopairButton2;
	private Button normalButton;

	private boolean mIsBind = false;
	

	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case BluetoothConst.MESSAGE_CONNECTED_ERROR:
				setContentView(noPairView);
				break;
			case BluetoothConst.MESSAGE_BIND_SUCCESS:
				setContentView(normalView);
				break;
			case BluetoothConst.MESSAGE_DATA:
				normalECGSurfaceView.passData((float) msg.obj);
			}
		};
	};

	@Override
	protected void initLayout() {
		LayoutInflater inflater = getLayoutInflater();
		normalView = inflater.inflate(R.layout.layout_ecg_normal, null);
		noPairView = inflater.inflate(R.layout.layout_ecg_nopair, null);

		normalECGSurfaceView = (NormalECGSurfaceView) normalView
				.findViewById(R.id.surfaceview1);
		nopairButton1 = (Button) noPairView.findViewById(R.id.button1);
		nopairButton2 = (Button) noPairView.findViewById(R.id.button2);
		normalButton = (Button) normalView.findViewById(R.id.button1);
	}

	@Override
	protected void initListener() {

		nopairButton1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				retryConnect();
			}

		});
		nopairButton2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ToolUtil.startActivity(ECGActivity.this,
						WebActivity.creatIntent(ECGActivity.this,
								"http://baidu.com", "帮助"));
			}
		});
		normalButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				cancelConnect();
			}
		});
	}

	private ServiceConnection serConn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG, "onServiceDisconnected()...");
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(TAG, "onServiceConnected()...");
			LocalBinder mBinder = (LocalBinder) service;
			mBinder.setHandler(handler);
		}
	};

	private void doBindService() {
		Log.i(TAG, "doBindService()...");
		Intent intent = new Intent(this, BluetoothService.class);
		mIsBind = bindService(intent, serConn, BIND_AUTO_CREATE);
		Log.e(TAG, "Is bind: " + mIsBind);
	}

	private void cancelConnect() {
		Intent intent = new Intent();
		intent.setAction(BluetoothConst.ACTION_SERVICE_CANCEL_CONNECT);
		sendBroadcast(intent);
	}

	private void retryConnect() {
		Intent intent = new Intent(this, PairActivity.class);
		ToolUtil.startActivity(this, intent);
		finish();
	}

	private void handleIntent() {
		String action = getIntent().getAction();
		if (BluetoothConst.INTENT_STATE_SUCCESS.equals(action)) {
			setContentView(normalView);
			doBindService();
		} else if (BluetoothConst.INTENT_STATE_FAILED.equals(action)) {
			setContentView(noPairView);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 		handleIntent();
 		normalECGSurfaceView.setZOrderOnTop(true);
 		normalECGSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mIsBind) {
			unbindService(serConn);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.ecg, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id=item.getItemId();
		switch (id) {
		case R.id.menu_alarmlog:
			ToolUtil.startActivity(this, AlarmLogActivity.class);
			break;
		case R.id.menu_about:
			ToolUtil.startActivity(this, AboutActivity.class);
			break;
		case R.id.menu_help:
			ToolUtil.startActivity(this, WebActivity.creatIntent(this, "http://baidu.com", "帮助"));
		default:
			break;
		}
		return true;
	}
	@Override
	protected void initValue() {

	}

	@Override
	protected int setRootView() {
		return R.layout.layout_ecg_nopair;
	}

	@Override
	protected void onResume() {

		super.onResume();
		normalECGSurfaceView.setThreadState(true);

	}

	@Override
	protected void onPause() {

		super.onPause();
		normalECGSurfaceView.setThreadState(false);

	}

}
