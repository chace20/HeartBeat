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
	
	private int width;
	private int height;
	
	public NormalECGSurfaceView(Context context,AttributeSet attrs) {
		super(context,attrs);
		this.context = context;
		holder = this.getHolder();
		holder.addCallback(this);
		width = this.getWidth();
		height = this.getHeight();
		normalDrawThread = new NormalDrawThread(holder, context, width, height);
		normalDrawThread.start();
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
		
		normalDrawThread.setRun(true);
	}

	/* (non-Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.SurfaceHolder)
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}
	
	public void setThreadState(boolean run){
		
		normalDrawThread.setRun(run);
	}
	
	public void passData(float msg){
		
		normalDrawThread.addData(msg);
	}

}
