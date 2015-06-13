package com.uestc.hb.ecg;

import java.util.ArrayList;

import android.view.SurfaceView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

public class HistoryECGSurView extends SurfaceView implements Callback {
	
	private Context context;
	private SurfaceHolder holder;
	private HistoryDrawThread historyDrawThread;
	
	public HistoryECGSurView(Context context,AttributeSet attrs) {
		super(context,attrs);
		this.context = context;
		holder = this.getHolder();
		holder.addCallback(this);

	}

	/* (non-Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder, int, int, int)
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	/* (non-Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder)
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		historyDrawThread = new HistoryDrawThread(holder, context);
		historyDrawThread.start();
	}

	/* (non-Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.SurfaceHolder)
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		historyDrawThread = null;
	}
	
	public void passData(ArrayList<Float> data){
		
		historyDrawThread.setData(data);
	}

}
