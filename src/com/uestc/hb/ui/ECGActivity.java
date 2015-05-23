package com.uestc.hb.ui;

import com.uestc.hb.R;
import com.uestc.hb.ecg.HistoryECGSurView;
import com.uestc.hb.ecg.NormalECGSurfaceView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ECGActivity extends BaseActivity implements OnClickListener{
	private View normalView;
	private View noPairView;

	private NormalECGSurfaceView normalECGSurfaceView;

	private Button nopairButton1;
	private Button nopairButton2;
	private Button normalButton;

	@Override
	protected void initLayout() {
		LayoutInflater inflater = getLayoutInflater();
		normalView = inflater.inflate(R.layout.layout_ecg_normal, null);
		noPairView = inflater.inflate(R.layout.layout_ecg_nopair, null);

		normalECGSurfaceView = (NormalECGSurfaceView) normalView
				.findViewById(R.id.surfaceview1);
		nopairButton1 = (Button) noPairView.findViewById(R.id.button1);
		nopairButton2 = (Button) noPairView.findViewById(R.id.button2);
		normalButton = (Button) normalButton.findViewById(R.id.button1);
	}

	@Override
	protected void initListener() {

		nopairButton1.setOnClickListener(this);
		nopairButton2.setOnClickListener(this);
		normalButton.setOnClickListener(this);
	}

	@Override
	protected void initValue() {
		// TODO Auto-generated method stub

	}

	@Override
	protected int setRootView() {
		return R.layout.layout_ecg_nopair;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
