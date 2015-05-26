package com.uestc.hb.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class Util {
	//发送通知
	public static void toNotiFy(Context context, int icon,
			String title, int noId, Intent intent,
			String content) {

		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = new Notification(icon, "",System.currentTimeMillis());
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			notification.tickerText = title;
			notification.defaults=Notification.DEFAULT_SOUND;
		// Update the notification.
		PendingIntent pendingIntent = PendingIntent.getActivity(context, noId,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(context, title, content,
				pendingIntent);
		nm.notify(noId, notification);
	}
}
