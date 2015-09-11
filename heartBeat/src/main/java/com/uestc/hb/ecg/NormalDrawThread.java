package com.uestc.hb.ecg;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
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
		pen.setColor(Color.BLUE);
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
						yPoint = yCenter - yPoint * 100 + 100;
					} catch (Exception e) {
					} finally {

						try {
							Canvas canvas = holder.lockCanvas(new Rect(xPoint,
									0, xPoint + 7, HEIGHT));
							canvas.drawLine(xPoint, yOld, xPoint + 1, yPoint,
									pen);
							holder.unlockCanvasAndPost(canvas);
						} catch (Exception e) {
						}
						yOld = yPoint;
						xPoint += 1;
						if (xPoint >= WIDTH) {
							xPoint = X_OFFSET;
							drawBack(holder);
							drawBack(holder);
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
			// canvas.drawColor(Color.parseColor("#eeeeee"));
			// holder.unlockCanvasAndPost(canvas);

			Paint paint = new Paint();
			paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
			canvas.drawPaint(paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
			holder.unlockCanvasAndPost(canvas);
			Canvas canvas2 = holder.lockCanvas(new Rect(0, 0, 10, HEIGHT));
			canvas2.drawColor(Color.parseColor("#eeeeee"));
			holder.unlockCanvasAndPost(canvas2);
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
}
