package com.uestc.hb.ecg;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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

	private FileRead fileRead;
	private ArrayList<Number> data;
	private int count;

	public HistoryDrawThread(SurfaceHolder holder, Context context, int width, int height) {

		WIDTH = width;
		HEIGHT = height;
		X_OFFSET = 5;

		xPoint = X_OFFSET;
		yCenter = HEIGHT / 3 * 2;
		
		yOld = yCenter;

		this.holder = holder;

		holder.setFixedSize((int)(WIDTH * 1.1), (int)(HEIGHT * 1.1));

		pen = new Paint();
		pen.setColor(Color.parseColor("#e91e63"));
		pen.setStrokeWidth(2);
		pen.setAntiAlias(true);
		fileRead = new FileRead(context);
		data = fileRead.readData();
		count = 0;
	}

	@Override
	public void run() {

		float yPoint;

		
		Canvas canvas = holder.lockCanvas(new Rect(0, 0, WIDTH, HEIGHT));
		canvas.drawColor(Color.parseColor("#00f0f0f0"));
		while (true) {
			try {
				yPoint = (Float) data.get(count++);
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
		


