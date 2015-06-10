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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.uestc.hb.R;
import com.uestc.hb.common.BluetoothConst;
import com.uestc.hb.service.BluetoothService;
import com.uestc.hb.utils.ToolUtil;

public class PairActivity extends BaseActivity {
	private static final String TAG = PairActivity.class.getName();

	private static final int REQUEST_ENABLE_BT = 123;

	private ListView devicesListView = null;

	public static final String SELECTED_DEVICE = "selected_device";

	private TextView cancelPairText;

	private BluetoothAdapter mBluetoothAdapter;

	private ArrayAdapter<String> mDevicesArrayAdapter;

	private DeviceReceiver pairReceiver = new DeviceReceiver();

	@Override
	protected void initLayout() {
		cancelPairText = (TextView) findViewById(R.id.cancelPairText);
		devicesListView = new ListView(this);
	}

	@Override
	protected void initListener() {
		cancelPairText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startECGActivity(BluetoothConst.INTENT_STATE_FAILED);
			}
		});
		devicesListView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View view,
							int position, long arg3) {						
						mBluetoothAdapter.cancelDiscovery();
						String info=mDevicesArrayAdapter.getItem(position);
						String address = info.substring(info.length() - 17);
						Intent intent = new Intent(PairActivity.this,BluetoothService.class);
						BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
							intent.putExtra(SELECTED_DEVICE, device);
						startService(intent);
					}
				});
	}

	@Override
	protected void initValue() {
		cancelPairText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		mDevicesArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.item_pair_dialog);
		devicesListView.setAdapter(mDevicesArrayAdapter);

		registReceiver();

		openBT();
	}

	private void openBT() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			trace("手机没有蓝牙适配器");
			startECGActivity(BluetoothConst.INTENT_STATE_FAILED);
		} else {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			} else {
				trace("蓝牙已打开");
				doDiscovery();
			}
		}

	}

	private void startECGActivity(String action) {
		Intent intent = new Intent(this, ECGActivity.class);
		intent.setAction(action);
		ToolUtil.startActivity(this, intent);
		finish();
	}

	private void doDiscovery() {

		if (mBluetoothAdapter.isDiscovering()) {
			mBluetoothAdapter.cancelDiscovery();
		}

		mBluetoothAdapter.startDiscovery();
		showDevicesList();
	}

	private void showDevicesList() {
		final MaterialDialog alert = new MaterialDialog(this).setTitle("选择设备")
				.setContentView(devicesListView);
		alert.setCanceledOnTouchOutside(true);
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
				.getBondedDevices();

		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				mDevicesArrayAdapter.add(device.getName() + "\n"
						+ device.getAddress());
			}
		}
		alert.show();
	}

	@Override
	protected int setRootView() {
		return R.layout.layout_pairing;
	}

	public void trace(String msg) {
		Log.e(TAG, msg);
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				trace("蓝牙打开成功");
				doDiscovery();
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

	private class DeviceReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				trace("查找设备");
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				mDevicesArrayAdapter.add(device.getName() + "\n"
							+ device.getAddress());
			} else if (BluetoothConst.ACTION_PAIR_NOT_FOUND.equals(action)) {
				trace("连接失败");
				startECGActivity(BluetoothConst.INTENT_STATE_FAILED);
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				if (mDevicesArrayAdapter.getCount() == 0) {
					trace("未找到设备");
					startECGActivity(BluetoothConst.INTENT_STATE_FAILED);
				}
			} else if (BluetoothConst.ACTION_PAIR_CONNECTED.equals(action)) {
				trace("连接成功");
				startECGActivity(BluetoothConst.INTENT_STATE_SUCCESS);
			} else {
				trace("蓝牙状态异常");
			}
		}
	}
	// discover的设备通过对话框显示出来，点击后把device发送给service，service连接失败发送失败的广播给pairactivity，
	// pairActivity再启动ecgactivity，在intent里面传递失败。service连接成功就发送成功的广播给pairactivity，
	// pairactivity传递成功的intent并启动ecgActivity
}
