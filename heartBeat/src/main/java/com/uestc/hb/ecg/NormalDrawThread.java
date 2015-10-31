package com.uestc.hb.ecg;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;

public class NormalDrawThread extends Thread {

	private SurfaceHolder holder;
	private boolean run;

	private final int WIDTH;
	private final int HEIGHT;
	private final int X_OFFSET;

	private int xPoint;
	private int yCenter;
	private float yPoint;

	private float yOld;

	private Timer timer;
	private TimerTask task;

	private Paint pen;

	private ArrayList<Float> data = new ArrayList<Float>();

	public NormalDrawThread(SurfaceHolder holder, Context context) {

		WIDTH = 480;
		HEIGHT = 270;
		X_OFFSET = 1;

		xPoint = X_OFFSET;
		yCenter = HEIGHT / 4 * 3;

		yOld = yCenter;
		yPoint = yCenter;

		timer = new Timer();
		task = null;

		this.holder = holder;
		run = false;

		holder.setFixedSize(WIDTH, HEIGHT);

		pen = new Paint();
		// pen.setColor(Color.parseColor("#e91e63"));
		pen.setColor(Color.parseColor("#eeeeee"));
		pen.setStrokeWidth(2);
		pen.setAntiAlias(true);
	}

	@Override
	public void run() {
		// drawBack(holder);
		task = new TimerTask() {

			@Override
			public void run() {
				if (run) {
					try {
						yPoint = useData();
//						yPoint = (float)(Math.random() + 2);
						yPoint = yCenter - yPoint * 100 + 100;
					} catch (Exception e) {
					} finally {

						try {
							if(xPoint == X_OFFSET){
								drawBack(holder);
								drawBack(holder);
								drawAxises(holder);
							}
							Canvas canvas = holder.lockCanvas(new Rect(xPoint,
									0, xPoint + 7, HEIGHT));
							canvas.drawLine(xPoint, yOld, xPoint + 1, yPoint,
									pen);
							holder.unlockCanvasAndPost(canvas);
							yOld = yPoint;
							xPoint += 1;
							if (xPoint >= WIDTH) {
								xPoint = X_OFFSET;
								drawBack(holder);
								pen.setColor(Color.BLUE);
							}
						} catch (Exception e) {
						}

					}
				}
			}
		};

		timer.schedule(task, 0, 2);

	}

	private void drawBack(SurfaceHolder holder) {
		try {
			Canvas canvas = holder.lockCanvas();
			 canvas.drawColor(Color.parseColor("#eeeeee"));
			 holder.unlockCanvasAndPost(canvas);

//			Paint paint = new Paint();
//			paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
//			paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
//			canvas.drawPaint(paint);
			holder.unlockCanvasAndPost(canvas);
			Canvas fixCanvas = holder.lockCanvas(new Rect(0, 0, 10, HEIGHT));
			fixCanvas.drawColor(Color.parseColor("#eeeeee"));
			holder.unlockCanvasAndPost(fixCanvas);
		} catch (Exception e) {

		} finally {

		}

		// holder.lockCanvas(new Rect(0, 0, 0, 0));
		// holder.unlockCanvasAndPost(canvas);
	}

	public boolean isRun() {
		return run;
	}

	public void setRun(boolean run) {
		this.run = run;
	}

	public void addData(float msg) {

		// lock.lock();
		try {

			data.add(msg);
		} catch (Exception e) {

		} finally {

			// lock.unlock();
		}

	}

	public float useData() {

		float temp = -1;
		// lock.lock();
		try {

			temp = (Float) data.get(0);
			data.remove(0);
		} catch (Exception e) {
		}

		// lock.unlock();
		return temp;

	}

	public void drawAxises(SurfaceHolder holder){
		Bitmap mBitmap = Bitmap.createBitmap(480, 270, Bitmap.Config.ARGB_8888);
		float x, y;
		Paint axisPen = new Paint();
		axisPen.setColor(Color.parseColor("#8b8989"));
		axisPen.setStrokeWidth(1);
		Canvas tmp = new Canvas(mBitmap);
		for(x = X_OFFSET;x <= WIDTH;x += 16){
			tmp.drawLine(x, 0, x, HEIGHT, axisPen);
		}
		for(y = 0;y <= HEIGHT; y += 10){
			tmp.drawLine(X_OFFSET, y, WIDTH, y, axisPen);
		}
		Canvas canvas = holder.lockCanvas();
		canvas.drawBitmap(mBitmap, 0, 0, axisPen);
		Log.e("tag", "drawAxises ");
		holder.unlockCanvasAndPost(canvas);
	}
}
