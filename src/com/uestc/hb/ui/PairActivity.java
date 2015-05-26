package com.uestc.hb.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.uestc.hb.R;
import android.util.Log;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public abstract class PairActivity extends BaseActivity implements OnItemClickListener {
	
	private BluetoothAdapter bAdapter;
	
	private final static int REQUEST_ENABLE_BT = 0;//0 here may need change into 1
	
	private ListView pairList;
	private List<String> bluetoothDevices = new ArrayList<String>();
	private ArrayAdapter<String> arrayAdapter;
	private AcceptThread acceptThread;
	private final UUID MY_UUID = UUID
			.fromString("12345678-aaaa-bbbb-cccc-abcdefghijkl");
	private final String NAME = "Bluetooth_socket";


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//	--------click	
		TextView pairText3 = (TextView) findViewById(R.id.pairText3);
		pairText3.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

		pairText3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent("com.uestc.hb.ui.intent.action.NoPairActivity");
				startActivity(i);
			}
		});
//	----------1
		bAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bAdapter==null){
			Toast.makeText(this, "请检查蓝牙设备是否可用", Toast.LENGTH_LONG).show();
			
			finish();//     here will be a test
			
			Intent i = new Intent("com.uestc.hb.ui.intent.action.NoPairActivity");
			startActivity(i);
			
			return;//	   another test
		}
        if(!bAdapter.isEnabled()){
        	Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        
        
        
        Set<BluetoothDevice>pairedDevices = bAdapter.getBondedDevices();
        if (pairedDevices.size() > 0){
        	for (BluetoothDevice device : pairedDevices){
        		bluetoothDevices.add(device.getName() + ":"
						+ device.getAddress() + "\n");
        	}
        }
        
        arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				bluetoothDevices);

        ((AdapterView<ListAdapter>) pairedDevices).setAdapter(arrayAdapter);
        ((AdapterView<ListAdapter>) pairedDevices).setOnItemClickListener(this);

        acceptThread = new AcceptThread(acceptThread);
        acceptThread.start();

	}
	public void ensureDiscoverable(){
		if (bAdapter.getScanMode()!= 
				BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
			Intent discoverIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverIntent);
		}
	}

	public void onClick_Search(View view) {
		setProgressBarIndeterminateVisibility(true);
		setTitle("Discovering...");

		if (bAdapter.isDiscovering()) {
			bAdapter.cancelDiscovery();
		}
		bAdapter.startDiscovery();
	}
	
	
	private final BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					bluetoothDevices.add(device.getName() + ":"
							+ device.getAddress() + "\n");
					arrayAdapter.notifyDataSetChanged();
				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
						.equals(action)) {
					setProgressBarVisibility(false);
					setTitle("设备连接成功");
//	                此处还需修改				
//					此处应有跳转
				}
			}

		}
	};


	private class AcceptThread extends Thread {
		private BluetoothServerSocket serverSocket;
		private BluetoothSocket socket;
		private InputStream is;
		private OutputStream os;

		public AcceptThread(AcceptThread acceptThread) {
			BluetoothServerSocket bServerSocket = null;
			try {
				bServerSocket = bAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			serverSocket = bServerSocket;

		}

		public void run() {
			BluetoothSocket bSocket = null;
			while (true){
				try {
					bSocket = serverSocket.accept();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		public void cancel(){
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	public class ConnectThread extends Thread{
		private BluetoothSocket clientSocket;
		private BluetoothDevice device;
		public ConnectThread(BluetoothDevice device,boolean secure){
			this.device = device;
			BluetoothSocket bSocket = null;
			try {
				bSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		public void run(){
			bAdapter.cancelDiscovery();
			try {
				clientSocket.connect();
			} catch (IOException e) {
				try {
					clientSocket.close();
				} catch (IOException e2) {
					Log.e("app", "unable to close() "+"clienSocket during connection failure",e2);
				}
				return;
			}
			
			//此处可有一个数据交换线程把此socket传入
		}
		
		public void cancel(){
			try{
				clientSocket.close();
			}catch (IOException e){
				Log.e("app", "close() of connect clientSocket failed", e);
			}
		}
	}
	
	private class ConnectedThread extends Thread{
		private BluetoothSocket socket;
		private OutputStream os;
		private InputStream is;
		
		public ConnectedThread(BluetoothSocket socket){
			this.socket =socket;
			try{
				is = socket.getInputStream();
				os = socket.getOutputStream();
			}catch(IOException e){
				Log.e("app", "temp sockets not create", e);
			}
		}
		
		public void run(){
			byte[] buff = new byte[1024];
			int len = 0;
			while(true){
				try{
					len = is.read(buff);
//					接下来可以把读到的数据发送出去
				}catch(IOException e){
					Log.e("app", "disconnected", e);
//             失去连接
					start();
					break;
				}
			}
		}
	}


	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pair, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void initLayout() {
		// TODO �Զ����ɵķ������
		
	}

	@Override
	protected void initListener() {
		// TODO �Զ����ɵķ������
		
	}

	@Override
	protected void initValue() {
		// TODO �Զ����ɵķ������
		
	}

	@Override
	protected int setRootView() {
		return R.layout.layout_pair;
	}
}
