package com.uestc.hb.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.uestc.hb.R;
import com.uestc.hb.common.BluetoothConst;
import com.uestc.hb.service.BluetoothService;

import android.util.Log;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PairActivity extends Activity {
	private static final String TAG = PairActivity.class.getName();

	private static final int REQUEST_ENABLE_BT = 123;

	public BluetoothAdapter mBluetoothAdapter;

	private Button connectButton;
	
	private BroadcastReceiver pairReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothConst.ACTION_PAIR_CONNECTED.equals(action)) {
				trace("蓝牙已连接");
				startECGActivity(BluetoothConst.INTENT_STATE_SUCCESS);
			}else if(BluetoothConst.ACTION_PAIR_NOT_FOUND.equals(action)){
				startECGActivity(BluetoothConst.INTENT_STATE_FAILED);
				trace("蓝牙未找到设备");
			}else{
				//更多异常状况
				trace("蓝牙状态异常");
			}
		}
	};
	
	private void initView() {
		connectButton = (Button) findViewById(R.id.connectButton);
		connectButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openBT();
			}
		});
	}

	private void openBT() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			trace("手机没有蓝牙适配器");
			finish();
		} else {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}else{
				trace("蓝牙已打开");
				startBTService();
			}
		}
	}


	private void registReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothConst.ACTION_PAIR_CONNECTED);
		filter.addAction(BluetoothConst.ACTION_PAIR_NOT_FOUND);
		registerReceiver(pairReceiver, filter);
	}
	
	private void startBTService(){
		trace("启动BTService");
		Intent intent=new Intent(this, BluetoothService.class);
		startService(intent);
	}
	
	private void startECGActivity(String action){
		Intent intent=new Intent(this, ECGActivity.class);
		intent.setAction(action);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_pair);
		initView();
		registReceiver();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(pairReceiver);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				trace("蓝牙打开成功");
				startBTService();
			} else {
				trace("蓝牙打开失败");
				startECGActivity(BluetoothConst.INTENT_STATE_FAILED);
			}
			break;
		}
	}
	
	//用来追踪状态信息
	public void trace(String msg){
		Log.i(TAG, msg);
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
}
