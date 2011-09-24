package com.gmail.altakey.effy;

import android.app.Service;
import android.app.PendingIntent;
import android.app.Notification;
import android.app.NotificationManager;
import android.os.IBinder;
import android.os.Binder;
import android.content.Intent;
import android.util.Log;

public class MainService extends Service {
	private NotificationManager notificationManager;
	public static boolean isRunning;

	private final int NOTIFICATION = 0xdeadbeef;
	private final IBinder binder = new MainBinder();

	public class MainBinder extends Binder
	{
		MainService getService()
		{
			return MainService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		showNotifyIcon();
		isRunning = true;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		notificationManager.cancel(NOTIFICATION);
		isRunning = false;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.i("MainService", "Received start id " + startId + ": " + intent);
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return binder;
	}
	
	private void showNotifyIcon()
	{
		CharSequence text = "タップすると画面を表示します";
		Notification notification = new Notification(R.drawable.icon, text, System.currentTimeMillis());
		PendingIntent contentIntent =
			PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
		notification.setLatestEventInfo(
				this,
				"MainService",
				text,
				contentIntent);
		notificationManager.notify(NOTIFICATION, notification);
	}
}
