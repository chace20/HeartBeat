package com.uestc.hb.ui;

import com.hb.services.BTService;
import com.hb.services.BTService.LocalBinder;
import com.uestc.hb.R;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public abstract class ECGActivity extends BaseActivity {

	private static final String TAG = ECGActivity.class.getName();

	private boolean mIsBind=false;
	
	private TextView stateText;
	private TextView dataText;
	private Button cancelButton;
	private Button retryButton;

	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(BluetoothConst.MESSAGE_BIND_SUCCESS==msg.what){
				stateText.setText("bind成功");
			}else if(BluetoothConst.MESSAGE_CONNECTED_ERROR==msg.what){
				stateText.setText("连接出现异常");
			}else if(BluetoothConst.MESSAGE_DATA==msg.what){
				//显示数据
				dataText.setText(msg.obj.toString());
			}
			
		};
	};

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
		Intent intent = new Intent(this, BTService.class);
		mIsBind = bindService(intent, serConn, BIND_AUTO_CREATE);
		Log.e(TAG, "Is bind: " + mIsBind);
	}
	
	private void initView(){
		stateText=(TextView) findViewById(R.id.stateText);
		dataText=(TextView) findViewById(R.id.dataText);
		cancelButton=(Button) findViewById(R.id.cancelButton);
		retryButton=(Button) findViewById(R.id.retryButton);
		
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cancelConnect();
			}
		});
		retryButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				retryConnect();
			}
		});
	}
	
	private void cancelConnect(){
		Intent intent=new Intent();
		intent.setAction(BluetoothConst.ACTION_SERVICE_CANCEL_CONNECT);
		sendBroadcast(intent);
		stateText.setText("未连接");
		cancelButton.setVisibility(View.GONE);
		retryButton.setVisibility(View.VISIBLE);
	}
	private void retryConnect(){
		Intent i=new Intent(this, PairActivity.class);
		startActivity(i);
		finish();
	}
	
	private void handleIntent(){
		String action=getIntent().getAction();
		trace("handleIntent"+action);
		if(BluetoothConst.INTENT_STATE_SUCCESS.equals(action)){
			stateText.setText("已连接");
			retryButton.setVisibility(View.GONE);
			doBindService();
		}else if(BluetoothConst.INTENT_STATE_FAILED.equals(action)){
			stateText.setText("未连接");
			cancelButton.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_ecg);
		initView();
		handleIntent();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mIsBind){
			unbindService(serConn);
		}
	}

	// 用来追踪状态信息
	public void trace(String msg) {
		Log.i(TAG, msg);
//		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

}
