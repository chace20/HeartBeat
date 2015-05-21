package com.uestc.hb.ecg;

import android.view.SurfaceView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;


public class NormalECGSurfaceView extends SurfaceView implements Callback {
	
	private Context context;
	private SurfaceHolder holder;
	private NormalDrawThread normalDrawThread;
	
	public NormalECGSurfaceView(Context context,AttributeSet attrs) {
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
		normalDrawThread = new NormalDrawThread(holder, context);
		normalDrawThread.setRun(true);
		normalDrawThread.start();
	}

	/* (non-Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.SurfaceHolder)
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		normalDrawThread.setRun(false);
		normalDrawThread = null;
	}

}
