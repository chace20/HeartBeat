package com.uestc.hb.ecg;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;

public class NormalDrawThread extends Thread {

	private SurfaceHolder holder;
	private boolean run;

	private final int WIDTH;
	private final int HEIGHT;
	private final int X_OFFSET;

	private int xPoint;
	private int yCenter;
	
	private float yOld;

	private Timer timer;
	private TimerTask task;

	private Paint pen;

	private FileRead fileRead;
	private ArrayList<Number> data;
	private int count;

	public NormalDrawThread(SurfaceHolder holder, Context context) {

		//TODO  修改分辨率
		WIDTH = 480;
		HEIGHT = 270;
		X_OFFSET = 5;

		xPoint = X_OFFSET;
		yCenter = HEIGHT / 3 * 2;
		
		yOld = yCenter;

		timer = new Timer();
		task = null;

		this.holder = holder;
		run = true;

		holder.setFixedSize(WIDTH + 48, HEIGHT + 27);

		pen = new Paint();
		pen.setColor(Color.GREEN);
		pen.setStrokeWidth(2);
		fileRead = new FileRead(context);
		data = fileRead.readData();
		count = 0;
	}

	@Override
	public void run() {

		//TODO 改成画线
		task = new TimerTask() {

			@Override
			public void run() {
				if (run) {
					float yPoint = (Float) data.get(count++);
					yPoint = yCenter - yPoint * 50;

					Canvas canvas = holder.lockCanvas(new Rect(xPoint,
							(int) yPoint - 2, xPoint + 2, (int) yPoint + 2));
					canvas.drawLine(xPoint - 1, yOld, xPoint, yPoint, pen);
					holder.unlockCanvasAndPost(canvas);

					xPoint++;
					if (xPoint >= WIDTH) {
						xPoint = X_OFFSET;
						drawBack(holder);
					}
				}
			}
		};

		timer.schedule(task, 0, 2);

	}

	private void drawBack(SurfaceHolder holder) {

		Canvas canvas = holder.lockCanvas();
		canvas.drawColor(Color.BLACK);
		holder.unlockCanvasAndPost(canvas);

		// holder.lockCanvas(new Rect(0, 0, 0, 0));
		// holder.unlockCanvasAndPost(canvas);
	}

	public boolean isRun() {
		return run;
	}

	public void setRun(boolean run) {
		this.run = run;
	}
}

