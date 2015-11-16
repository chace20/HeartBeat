package com.uestc.hb.ecg;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;

public class HistoryDrawThread extends Thread {

	private SurfaceHolder holder;

	private final int WIDTH;
	private final int HEIGHT;
	private final int X_OFFSET;

	private int xPoint;
	private int yCenter;
	
	private float yOld;

	private Paint pen;

	private ArrayList<Float> data = new ArrayList<Float>();
	private int count;

	private boolean run;

	public HistoryDrawThread(SurfaceHolder holder, Context context) {

		WIDTH = 480;
		HEIGHT = 270;
		X_OFFSET = 5;

		xPoint = X_OFFSET;
		yCenter = HEIGHT / 4 * 3;
		
		yOld = yCenter;

		this.holder = holder;

		holder.setFixedSize(WIDTH, HEIGHT);

		pen = new Paint();
		pen.setColor(Color.parseColor("#e91e63"));
		pen.setStrokeWidth(2);
		pen.setAntiAlias(true);
		data.add((float) 0.0);
		count = 0;

		run = false;
	}

	@Override
	public void run() {

		float yPoint;
		Canvas canvas;

		while(true){
			canvas = holder.lockCanvas();
			if(canvas != null){
				canvas.drawColor(Color.parseColor("#eeeeee"));
				break;
			}
		}

		while (true) {
			if(run){
				try {
					yPoint = data.get(count++);
					yPoint = yCenter - yPoint * 50;
					canvas.drawLine(xPoint - 1, yOld, xPoint, yPoint, pen);
					yOld = yPoint;

					xPoint++;
					if (xPoint >= WIDTH) {
						return;
					}
				} catch (IndexOutOfBoundsException e) {
					return;
				}
			}
		}
	}
	
	public void setData(ArrayList<Float> data){
		
		this.data = data;
	}

	public void setRun(boolean run){
		this.run = run;
	}
}
		


