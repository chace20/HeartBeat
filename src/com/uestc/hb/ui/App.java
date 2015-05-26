package com.uestc.hb.ui;

import android.app.Application;
import android.os.Handler;

public class App extends Application{
	Handler.Callback realCallback = null;
	Handler handler = new Handler() {
	    public void handleMessage(android.os.Message msg) {
	        if (realCallback != null) {
	            realCallback.handleMessage(msg);
	        }
	    };
	};
	public Handler getHandler() {
	    return handler;
	}
	public void setCallBack(Handler.Callback callback) {
	    this.realCallback = callback;
	}
}