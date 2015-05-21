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

	/**
	 * 复制内容到剪贴板
	 * 
	 * @param context
	 * @param content
	 * @param alertmsg
	 */
	public static void copyToClipboard(Context context, String content,
			String alertmsg) {
		ClipboardManager clip = (ClipboardManager) context
				.getSystemService(context.CLIPBOARD_SERVICE);
		clip.setText(content);
		Toast.makeText(context, alertmsg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 分享内容给好友
	 * 
	 * @param context
	 * @param title
	 * @param content
	 */
	public static void shareWithOther(Context context, String title,
			String content) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, title);
		intent.putExtra(Intent.EXTRA_TEXT, content);
		context.startActivity(Intent.createChooser(intent, title));
	}

	/**
	 * 去评分
	 * 
	 * @param context
	 */
	public static void markMarketScore(Context context) {
		String mAddress = "market://details?id=" + context.getPackageName();
		Intent marketIntent = new Intent("android.intent.action.VIEW");
		marketIntent.setData(Uri.parse(mAddress));
		try {
			context.startActivity(marketIntent);
		} catch (Exception e) {
			Toast.makeText(context, "未找到应用市场", Toast.LENGTH_SHORT).show();
		}
	}
}
