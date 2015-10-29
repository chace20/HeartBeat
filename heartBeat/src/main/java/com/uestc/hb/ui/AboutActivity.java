package com.uestc.hb.ui;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.uestc.hb.R;
import com.uestc.hb.utils.ToolUtil;


public class AboutActivity extends BaseActivity{
	private TextView contactText;
	public static final String contactUrl="http://evernever.github.io/heartbeat/contact/contact.html";
	
	@Override
	protected void initLayout() {
		actionbar.setDisplayHomeAsUpEnabled(true);
		contactText=(TextView) findViewById(R.id.contactText);
	}

	@Override
	protected void initListener() {
		contactText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ToolUtil.startActivity(AboutActivity.this, WebActivity.creatIntent(AboutActivity.this, contactUrl,"联系我们"));
			}
		});
	}

	@Override
	protected void initValue() {
		// TODO 自动生成的方法存根
		
	}

	@Override
	protected int setRootView() {
		return R.layout.layout_about;
	}

}
