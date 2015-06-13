package com.uestc.hb.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.database.Cursor;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.uestc.hb.R;
import com.uestc.hb.db.DataBaseAdapter;
import com.uestc.hb.ecg.NormalECGSurfaceView;

public class AlarmDetailActivity extends BaseActivity {

	private TextView textDate;
	private TextView textConclusion;
	private TextView textAdvice;
	private TextView textHeartRate;

	private NormalECGSurfaceView normalECGSurfaceView;

	private SimpleDateFormat simpleDateFormat;

	DataBaseAdapter dbAdapter;

	@Override
	protected void initLayout() {
		actionbar.setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.layout_doc_details);

		textDate = (TextView) findViewById(R.id.text_date);
		textConclusion = (TextView) findViewById(R.id.text_conclusion);
		textAdvice = (TextView) findViewById(R.id.text_advice);
		textHeartRate = (TextView) findViewById(R.id.text_heart_rate);

		normalECGSurfaceView = (NormalECGSurfaceView) findViewById(R.id.surfaceview_alarm_detail);

	}

	@Override
	protected void initListener() {
	}

	@Override
	protected void initValue() {
		dbAdapter = new DataBaseAdapter(AlarmDetailActivity.this);

		Bundle bundle = getIntent().getExtras();
		Cursor cursorDoc = dbAdapter.queryDetailsDoc(bundle.getLong("date"));
		Cursor cursorIll = dbAdapter.queryDetailsIll(bundle.getLong("date"));
		Log.i("doc", "---" + cursorDoc.getCount());
		Log.i("ill", "---" + cursorIll.getCount());
		Log.i("date", "---" + bundle.getLong("date"));
		textDate.setText(praiseDate(bundle.getLong("date")));

		if (cursorDoc.moveToFirst()) {
			textHeartRate.setText(""
					+ cursorDoc.getInt(cursorDoc.getColumnIndex("heart_rate")));
		}
		Float[] dataGet = praiseData(cursorDoc.getString(cursorDoc
				.getColumnIndex("hr_data")));
		for (int i = 0; i < dataGet.length; i++) {
			normalECGSurfaceView.passData(dataGet[i]);
		}

		textConclusion.setText("您有" + getIllnessName(cursorIll) + "这些病，"
				+ cursorIll.getString(cursorIll.getColumnIndex("description")));

		textAdvice.setText(cursorIll.getString(cursorIll
				.getColumnIndex("advice")));
	}

	@Override
	protected int setRootView() {
		return R.layout.layout_doc_details;
	}

	private Float[] praiseData(String data) {
		String[] dataGet;
		dataGet = data.split(",");
		Float[] dataPraised = new Float[dataGet.length];
		Log.i("---", dataGet[0]);
		for (int i = 0; i < dataGet.length; i++) {
			dataPraised[i] = Float.parseFloat(dataGet[i]);
		}
		return dataPraised;
	}

	private String getIllnessName(Cursor cursor) {
		String result = "";
		Log.i("列数", "---" + cursor.getCount());
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			result += cursor.getString(cursor.getColumnIndex("name")) + " ";
		}
		return result;
	}

	private String praiseDate(long date) {
		String result = "";
		Date newDate = new Date(date);
		simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		result += simpleDateFormat.format(newDate);
		return result;
	}

}
