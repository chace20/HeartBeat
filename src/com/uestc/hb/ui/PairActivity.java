package com.uestc.hb.ui;

import java.util.Set;

import me.drakeet.materialdialog.MaterialDialog;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.internal.widget.AdapterViewCompat.OnItemClickListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.uestc.hb.R;
import com.uestc.hb.common.BluetoothConst;

public class PairActivity extends BaseActivity {
	private static final String TAG = PairActivity.class.getName();
	
	private static final int REQUEST_ENABLE_BT = 123;
	
	private ListView devicesListview = null; 
	
	public static String EXTRA_DEVICE = "device";
	
	private Button connectButton;
	private Button serchButton;
	private TextView pairText3;
	
	private BluetoothAdapter mBluetoothAdapter;
	
	private ArrayAdapter<String> mDevicesArrayAdapter;
	
    private DeviceReceiver pairReceiver=new DeviceReceiver();  
    
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		registReceiver();
	}

	@Override
	protected void initLayout() {
		connectButton = (Button) findViewById(R.id.connectButton);
		pairText3 = (TextView) findViewById(R.id.pairText3);
		pairText3.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		ListView devicesListView = (ListView) findViewById(R.id.deviceslist);
		
		mDevicesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		devicesListView.setAdapter(mDevicesArrayAdapter);
		serchButton = (Button) findViewById(R.id.serchButton);
	}
	

	@Override
	protected void initListener() {
		connectButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openBT();	
			}
		});
		
		pairText3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(PairActivity.this,ECGActivity.class);
				startActivity(i);
				
			}
		});
		
		serchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDevicesList();			}
		});
		
	}
	
	
	private void openBT(){
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
				
				doDiscovery();
			}
		}
		
	}
	

	private void startECGActivity(String action){
		Intent intent=new Intent(this, ECGActivity.class);
		intent.setAction(action);
		startActivity(intent);
		finish();
	}
	
	private void doDiscovery() {

	        if (mBluetoothAdapter.isDiscovering()) {
	            mBluetoothAdapter.cancelDiscovery();
	        }

	        mBluetoothAdapter.startDiscovery();
	    }

	
	private void showDevicesList(){
		devicesListview.setOnItemClickListener(
				(android.widget.AdapterView.OnItemClickListener) mDeviceClickListener);
		
		final MaterialDialog alert = new MaterialDialog(this)
         .setTitle("选择设备")
         .setContentView(devicesListview);
		
		alert.show();

	
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(pairReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(pairReceiver, filter);
        
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            mDevicesArrayAdapter.add("未发现有效设备");
        }

	}
	
	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		

		@Override
		public void onItemClick(AdapterViewCompat<?> arg0, View v, int arg2,
				long arg3) {
						mBluetoothAdapter.cancelDiscovery();

						String info = ((TextView) v).getText().toString();
						String address = info.substring(info.length() - 17);

						Intent intent = new Intent();
						
						BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
						intent.putExtra(EXTRA_DEVICE, device);
						
						startService(intent);

						setResult(Activity.RESULT_OK, intent);
		}

	
	};


	@Override
	protected int setRootView() {
		return R.layout.layout_pairing;
	}

	
	public void trace(String msg){
		Log.i(TAG, msg);
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
	    if (mBluetoothAdapter != null) {
	        mBluetoothAdapter.cancelDiscovery();
	    }

	    this.unregisterReceiver(pairReceiver);
	}

	@Override
	protected void initValue() {
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				trace("蓝牙打开成功");
			} else {
				trace("蓝牙打开失败");
				startECGActivity(BluetoothConst.INTENT_STATE_FAILED);
			}
			break;
		}
	}

	private void registReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothConst.ACTION_PAIR_CONNECTED);
		filter.addAction(BluetoothConst.ACTION_PAIR_NOT_FOUND);
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(pairReceiver, filter);
	}
	
    private class DeviceReceiver extends BroadcastReceiver{  
    	  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            String action =intent.getAction();  
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
		        trace("蓝牙已连接");
		        startECGActivity(BluetoothConst.INTENT_STATE_SUCCESS);
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else if(BluetoothConst.ACTION_PAIR_NOT_FOUND.equals(action)){
            	startECGActivity(BluetoothConst.INTENT_STATE_FAILED);
            	trace("蓝牙未找到设备");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            	setTitle("搜索结束");
            	if (mDevicesArrayAdapter.getCount() == 0) {
            		mDevicesArrayAdapter.add("没有找到设备");
            	}
            } else{
				trace("蓝牙状态异常");
			}			
            }
    }
    
}


