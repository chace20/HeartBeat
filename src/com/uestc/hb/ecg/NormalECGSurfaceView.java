package com.uestc.hb.ecg;

import android.R;
import android.view.SurfaceView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

public class NormalECGSurfaceView extends SurfaceView implements Callback {

	private Context context;
	private SurfaceHolder holder;
	private NormalDrawThread normalDrawThread;

	private int width;
	private int height;
	
	private DisplayMetrics dm = getResources().getDisplayMetrics(); 
	
	

	public NormalECGSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		holder = this.getHolder();
		holder.addCallback(this);
		width = (int) (296*dm.ydpi/160);
		height = (int) (256*dm.xdpi/160);
		Log.i("ecg", "hw" + height + width);
		normalDrawThread = new NormalDrawThread(holder, context, width, height);
		normalDrawThread.start();
//		LayoutInflater inflater = (LayoutInflater) context
//				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		LayoutInflater.from(context).inflate(, this, true);
		
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder
	 * , int, int, int)
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder
	 * )
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		Canvas canvas = holder.lockCanvas();
		canvas.drawColor(Color.parseColor("#eeeeee"));
		holder.unlockCanvasAndPost(canvas);
		normalDrawThread.setRun(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.
	 * SurfaceHolder)
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	public void setThreadState(boolean run) {

		normalDrawThread.setRun(run);
	}

	public void passData(float msg) {

		normalDrawThread.addData(msg);
	}

}
