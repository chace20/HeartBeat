package com.uestc.hb.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import me.drakeet.materialdialog.MaterialDialog;

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
//-------------------------------------------------------
	private ArrayAdapter<String> mDevicesArrayAdapter;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
//---------------------------------------------------------
	private Button connectButton;
	
//	private BroadcastReceiver pairReceiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//			if (BluetoothConst.ACTION_PAIR_CONNECTED.equals(action)) {
//				trace("蓝牙已连接");
//				startECGActivity(BluetoothConst.INTENT_STATE_SUCCESS);
//			}else if(BluetoothConst.ACTION_PAIR_NOT_FOUND.equals(action)){
//				startECGActivity(BluetoothConst.INTENT_STATE_FAILED);
//				trace("蓝牙未找到设备");
//			}else{
//				//更多异常状况
//				trace("蓝牙状态异常");
//			}
//		}
//	};
	
	private void initView() {
		connectButton = (Button) findViewById(R.id.connectButton);
		connectButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openBT();
			}
		});
		
		TextView pairText3 = (TextView) findViewById(R.id.pairText3);
		pairText3.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		pairText3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(PairActivity.this,ECGActivity.class);
				startActivity(i);
				
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
		doDiscovery();
		registReceiver();
//		openBT();
//-------------------------------------------------------		
	//	// Find and set up the ListView for paired devices
				ListView devicesListView = new ListView(this);
				devicesListView.setAdapter(mDevicesArrayAdapter);
				devicesListView.setOnItemClickListener(mDeviceClickListener);
				
				final MaterialDialog alert = new MaterialDialog(this)
	             .setTitle("MaterialDialog")
	             .setContentView(devicesListView);

			//	// Find and set up the ListView for newly discovered devices
//				ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
//				newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
//				newDevicesListView.setOnItemClickListener(mDeviceClickListener);
				
			//	 // Register for broadcasts when a device is discovered
		        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		        this.registerReceiver(pairReceiver, filter);

		   //   // Register for broadcasts when discovery has finished
		        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		        this.registerReceiver(pairReceiver, filter);
		        
		   //   // Get a set of currently paired devices
		        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

		   //   // If there are paired devices, add each one to the ArrayAdapter
		        if (pairedDevices.size() > 0) {
		   //         findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
		            for (BluetoothDevice device : pairedDevices) {
		                mDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
		            }
		        } else {
		            mDevicesArrayAdapter.add("未发现有效设备");
		        }
//--------------------------------------------------------------------------------------------
	}
	
	
	
	private void doDiscovery() {

       // // Indicate scanning in the title
        //setProgressBarIndeterminateVisibility(true);
        setTitle("正在搜索");

      // // Turn on sub-title for new devices
        //findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

      // // If we're already discovering, stop it
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

      // // Request discover from BluetoothAdapter
        mBluetoothAdapter.startDiscovery();
    }
//-----------------------------------------------------------------------------------------------
	// //The on-click listener for all devices in the ListViews
		private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
			public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
			// // Cancel discovery because it's costly and we're about to connect
				mBluetoothAdapter.cancelDiscovery();

			// // Get the device MAC address, which is the last 17 chars in the View
				String info = ((TextView) v).getText().toString();
				String address = info.substring(info.length() - 17);

			// // Create the result Intent and include the MAC address
				Intent intent = new Intent();
				intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

			// // Set result and finish this Activity
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		};
//------------------------------------------------------------------------------------------------
		private BroadcastReceiver pairReceiver = new BroadcastReceiver() {
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				String action = intent.getAction();
//				if (BluetoothConst.ACTION_PAIR_CONNECTED.equals(action)) {
//					trace("蓝牙已连接");
//					startECGActivity(BluetoothConst.INTENT_STATE_SUCCESS);
//				}else if(BluetoothConst.ACTION_PAIR_NOT_FOUND.equals(action)){
//					startECGActivity(BluetoothConst.INTENT_STATE_FAILED);
//					trace("蓝牙未找到设备");
//				}else{
//					//更多异常状况
//					trace("蓝牙状态异常");
//				}
//			}
			
		    @Override
	        public void onReceive(Context context, Intent intent) {
	            String action = intent.getAction();

	            // When discovery finds a device
	            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			        trace("蓝牙已连接");
			        startECGActivity(BluetoothConst.INTENT_STATE_SUCCESS);
	                // Get the BluetoothDevice object from the Intent
	                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	                // If it's already paired, skip it, because it's been listed already
	                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
	                    mDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
	                }
	            // When discovery is finished, change the Activity title
	            } else if(BluetoothConst.ACTION_PAIR_NOT_FOUND.equals(action)){
	            	startECGActivity(BluetoothConst.INTENT_STATE_FAILED);
	            	trace("蓝牙未找到设备");
	            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//	            	setProgressBarIndeterminateVisibility(false);
	            	setTitle("搜索结束");
	            	if (mDevicesArrayAdapter.getCount() == 0) {
//	            		String noDevices = getResources().getText(R.string.none_found).toString();
	            		mDevicesArrayAdapter.add("没有找到设备");
	            	}
	            } else{
					//更多异常状况
					trace("蓝牙状态异常");
				}
	        }
			
		};
//-----------------------------------------------------------------------------------------------

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		   // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(pairReceiver);
	}
	
//-----------------------------------------------------------------------------------------------
	
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
