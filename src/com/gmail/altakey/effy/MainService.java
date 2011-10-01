/**
 * Copyright (C) 2011 Takahiro Yoshimura
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.gmail.altakey.effy;

import java.util.List;

import android.app.Service;
import android.app.PendingIntent;
import android.app.Notification;
import android.app.NotificationManager;
import android.os.IBinder;
import android.os.Binder;
import android.content.Intent;
import android.util.Log;

import android.view.View;

public class MainService extends Service {
	private NotificationManager notificationManager;
	public static boolean isRunning;

	private final int NOTIFICATION = 0xdeadbeef;
	private final IBinder binder = new MainBinder();

	private int cnt;
	
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
		this.setup();
		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		showNotifyIcon();
		isRunning = true;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.shutdown();
		notificationManager.cancel(NOTIFICATION);
		isRunning = false;
	}

	private void setup()
	{
		Scribble.setup(1, 1);
	}

	private void shutdown()
	{
		Scribble.getInstance().recycle();
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
			PendingIntent.getActivity(this, 0, new Intent(this, DrawActivity.class), 0);
		notification.setLatestEventInfo(
				this,
				"MainService",
				text,
				contentIntent);
		notificationManager.notify(NOTIFICATION, notification);
	}
}
