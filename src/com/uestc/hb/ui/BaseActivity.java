package com.uestc.hb.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

public abstract class BaseActivity extends ActionBarActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(setRootView());
		initLayout();
		initListener();
		initValue();
	}
	/**
	 * 初始化布局，主要是findView和inflate
	 */
	abstract protected void initLayout();
	/**
	 * 初始化监听器，比如setOnClickListener
	 */
	abstract protected void initListener();
	/**
	 * 初始化值，比如setAdapter，setText
	 */
	abstract protected void initValue();

	
	/**
	 * 设置根视图layout
	 * @return layoutResID
	 */
	abstract protected int setRootView();
	
}
