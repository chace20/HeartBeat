package com.uestc.hb.ecg;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;

import android.content.Context;

public class FileRead {

	private String fileName = "test.txt";
	private Context context;
	private ArrayList<Number> data = new ArrayList<Number>();
	public FileRead(Context context){
		
		this.context = context;
	}

	public ArrayList<Number> readData() {

		try {

			InputStream in = context.getResources().getAssets().open(fileName);
			LineNumberReader lbr = new LineNumberReader(new InputStreamReader(in));

			String tempString = "";

			while ((tempString = lbr.readLine()) != null) {

				data.add(Float.parseFloat(tempString));
			}
			lbr.close();
			return data;
		} catch (Exception e) {
			
			e.printStackTrace();
			data.add(0);
			return data;
		}
	}

}
