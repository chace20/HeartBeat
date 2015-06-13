package com.uestc.hb.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.uestc.hb.R;

import android.net.Uri;
import android.text.ClipboardManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ToolUtil {
	public static final int TIME_STAMP = 0x2325;
	public static final int TIME_COMMON = 0x1242;

	/**
	 * 根据mode获取当前时间,
	 * TIME_STAMP 获取时间戳,
	 * TIME_COMMON 获取普通时间
	 * 
	 * @param mode
	 * @return time
	 */
	public static String getCurrentTime(int mode) {
		long time = System.currentTimeMillis();
		if (mode == TIME_STAMP)
			return time + "";
		return getCommonTimeByStamp(time + "");
	}

	/**
	 * 通过时间戳获取正常时间
	 * 
	 * @param stamp
	 * @return time
	 */
	public static String getCommonTimeByStamp(String stamp) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long stampL = Long.parseLong(stamp);
		String d = format.format(new Date(stampL));
		return d;
	}


	public static int getInt(byte[] bytes) {
		return (0xff & bytes[0]) | (0xff00 & (bytes[1] << 8))
				| (0xff0000 & (bytes[2] << 16))
				| (0xff000000 & (bytes[3] << 24));
	}

	public static float getFloat(byte[] bytes) {
		return Float.intBitsToFloat(getInt(bytes));
	}
	
	/**
	 * 启动Activity
	 * 
	 * @param context
	 * @param cls
	 */
	public static void startActivity(Context context, Class<?> cls) {
		Intent i = new Intent(context, cls);
		context.startActivity(i);
		((Activity) context).overridePendingTransition(R.anim.push_up_in,
				R.anim.push_up_out);
	}

	/**
	 * 启动activity
	 * 
	 * @param context
	 * @param intent
	 */
	public static void startActivity(Context context, Intent intent) {
		context.startActivity(intent);
		((Activity) context).overridePendingTransition(R.anim.push_up_in,
				R.anim.push_up_out);
	}

}
