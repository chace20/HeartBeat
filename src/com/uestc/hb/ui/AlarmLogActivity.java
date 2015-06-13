package com.uestc.hb.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.drakeet.materialdialog.MaterialDialog;

import com.uestc.hb.R;
import com.uestc.hb.db.DataBaseAdapter;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class AlarmLogActivity extends BaseActivity {

	ListView listDoc;

	BaseAdapter listAdapter;

	protected DataBaseAdapter dbAdapter;

	SwipeRefreshLayout swipeRefreshLayout;

	protected List<Map<String, Object>> docsData;
	protected List<Long> date;

	@Override
	protected void initLayout() {
		actionbar.setDisplayHomeAsUpEnabled(true);

		dbAdapter = new DataBaseAdapter(this);
		dbAdapter.open();

		if (!isNull(dbAdapter)) {
			setContentView(R.layout.layout_list_normal);

			listDoc = (ListView) findViewById(R.id.list_doc);
			swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_list);
			docsData = new ArrayList<Map<String, Object>>();
			date = new ArrayList<Long>();

			getDoc(docsData, date);
			listDoc.setAdapter(listAdapter = getDocAdapter());
			turnToContent(listDoc, date);
			deleteOneDoc(listDoc, date, dbAdapter);
			refreshList(refreshHandler);
		} else {
			toDoNoDoc();
		}

	}

	private void toDoNoDoc() {
		setContentView(R.layout.layout_list_nodoc);
		setNoDocView();
	}

	@Override
	protected void initListener() {
	}

	@Override
	protected void initValue() {
	}

	@Override
	protected int setRootView() {
		return R.layout.layout_list_normal;
	}

	private Boolean isNull(DataBaseAdapter hbAdapter) {
		Cursor cursor = hbAdapter.queryDoc();
		if (cursor.getCount() == 0) {
			return true;
		}
		return false;
	}

	private void setNoDocView() {
		TextView textNodoc = (TextView) findViewById(R.id.text_nodoc);
		textNodoc.setText("您很健康，并没有异常记录");
	}

	private Handler refreshHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x110:
				getDoc(docsData, date);
				listAdapter.notifyDataSetChanged();
				swipeRefreshLayout.setRefreshing(false);
				break;
			}
		}
	};

	private void refreshList(final Handler refreshHandler) {
		swipeRefreshLayout
				.setColorSchemeResources(android.R.color.holo_blue_light,
						android.R.color.holo_red_light);
		swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				refreshHandler.sendEmptyMessageDelayed(0x110, 1000);
			}
		});
	}

	private void getDoc(List<Map<String, Object>> docsData, List<Long> date) {
		DataBaseAdapter dbAdapter = new DataBaseAdapter(this);
		dbAdapter.open();
		docsData.clear();
		Cursor cursor = dbAdapter.queryDoc();
		for (int i = cursor.getCount() - 1; i >= 0; i--) {
			cursor.moveToPosition(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("date", lagConverToString(getLag(cursor.getLong(cursor
					.getColumnIndex("date")))));
			map.put("heart_rate",
					cursor.getInt(cursor.getColumnIndex("heart_rate")));
			docsData.add(map);
			date.add(cursor.getLong(cursor.getColumnIndex("date")));
			Log.i("date", ""+cursor.getLong(cursor.getColumnIndex("date")));
		}
	}

	private BaseAdapter getDocAdapter() {
		return new SimpleAdapter(AlarmLogActivity.this, docsData,
				R.layout.layout_list_item,
				new String[] { "date", "heart_rate" }, new int[] {
						R.id.text_date, R.id.text_hr });
	}

	private void turnToContent(ListView list, final List<Long> date) {
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(AlarmLogActivity.this,
						AlarmDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putLong("date", (long) date.get(position));
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	private void deleteOneDoc(final ListView listDoc, final List<Long> date,
			final DataBaseAdapter dbAdapter) {
		listDoc.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {

				final MaterialDialog mMaterialDialog = new MaterialDialog(AlarmLogActivity.this);
				
			    mMaterialDialog.setMessage("是否删除？")
			    .setPositiveButton("确定", new View.OnClickListener() {
			        @Override
			        public void onClick(View v) {
			        	dbAdapter.deleteOneDoc(date.get(position));
			        	date.remove(position);
			        	refreshHandler.sendEmptyMessageDelayed(0x110, 0);
			        	mMaterialDialog.dismiss();
			        	if (isNull(dbAdapter)) {
							toDoNoDoc();
						}
			        }
			    })
			    .setNegativeButton("取消", new View.OnClickListener() {
			        @Override
			        public void onClick(View v) {
			        	mMaterialDialog.dismiss();
			        }
			    }).show();

				return false;
			}
		});
	};

	private String lagConverToString(long lag) {
		String lag_string = "";
		if (lag < 60) {
			lag_string += "刚刚";
			return lag_string;
		}
		if (lag < 60 * 60) {
			lag_string += "" + (int) (lag / 60) + "分钟前";
			return lag_string;
		}
		if (lag < 60 * 60 * 24) {
			lag_string += "" + (int) (lag / (60 * 60)) + "小时前";
			return lag_string;
		}
		if (lag < 60 * 60 * 24 * 30) {
			lag_string += "" + (int) (lag / (60 * 60 * 24)) + "天前";
			return lag_string;
		}
		if (lag < 60 * 60 * 24 * 30 * 12) {
			lag_string += "" + (int) (lag / (60 * 60 * 24 * 30)) + "个月前";
			return lag_string;
		}
		lag_string += "" + (int) (lag / (60 * 60 * 24 * 30 * 12)) + "年前";
		return lag_string;
	}

	private long getLag(long date) {
		long nowDate = System.currentTimeMillis();
		long between = 0;
		between = (nowDate - date) / 1000;

		return between;
	}

}
