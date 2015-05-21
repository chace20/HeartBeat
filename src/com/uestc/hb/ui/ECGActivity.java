package com.uestc.hb.ui;

import com.uestc.hb.R;

import android.view.LayoutInflater;
import android.view.View;

public class ECGActivity extends BaseActivity {
	private View normalView;
	private View noPairView;

	@Override
	protected void initLayout() {
		LayoutInflater inflater=getLayoutInflater();
		normalView=inflater.inflate(R.layout.layout_ecg_normal, null);
		noPairView=inflater.inflate(R.layout.layout_ecg_nopair, null);
		
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initValue() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int setRootView() {
		return R.layout.layout_ecg_nopair;
	}

}
