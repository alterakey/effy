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

/**
 * Uses portions of code from the 光学☆迷彩 project, which
 * is available from https://github.com/matsumo/kougaku-meisai .
 * 
 * Original copyright information:
 * 
 * Copyright (C) 2011 matsumo All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
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

import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.hardware.Camera;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.content.Context;

public class MainService extends Service {
	private NotificationManager notificationManager;
	public static boolean isRunning;

	private final int NOTIFICATION = 0xdeadbeef;
	private final IBinder binder = new MainBinder();

	private WindowManager wm;
	private ImageView iView;
	private Bitmap pbmp;
	private Camera camera;

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

	private View.OnTouchListener touchListener = new View.OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event)
		{
			Log.d("IV", "touched!!!");
			return true;
		}
	};

	private void setup()
	{
		this.wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);

		this.iView = new ImageView(getApplicationContext());
		this.iView.setScaleType(ScaleType.CENTER_CROP);
		this.iView.setAlpha(64);
		this.iView.setOnTouchListener(this.touchListener);

		LayoutParams params3 = new LayoutParams();
		params3.width = LayoutParams.FILL_PARENT;
		params3.height = LayoutParams.FILL_PARENT;
		params3.type = LayoutParams.TYPE_SYSTEM_OVERLAY;
		params3.format = PixelFormat.TRANSLUCENT;
		params3.flags = LayoutParams.FLAG_LAYOUT_IN_SCREEN | LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		params3.gravity = Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL;

		camera = Camera.open();
		Camera.Parameters parameters = camera.getParameters();
		List<Camera.Size> pre = parameters.getSupportedPreviewSizes();
		int ii = 0;
		for(int i=0; i<pre.size(); i++){
			Log.d("", String.format("x=%d,y=%d", pre.get(i).width, pre.get(i).height));
			if(pre.get(ii).width > pre.get(i).width) ii = i;
		}

		final int pwidth = pre.get(ii).width;
		final int pheight = pre.get(ii).height;
		final byte[] cameraBuffer = new byte[pwidth*pheight*3/2+16];
		this.pbmp = Bitmap.createBitmap(pwidth, pheight, Bitmap.Config.ARGB_8888);
		parameters.setPreviewSize(pwidth , pheight);
		camera.setParameters(parameters);
		camera.addCallbackBuffer(cameraBuffer);
		camera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback(){
			@Override
			public void onPreviewFrame(byte[] data, Camera cam) {
				if(cnt % 4 == 0){
					int[] rgb = new int[(pwidth * pheight)];
					try { 
							decodeYUV420SP(rgb, data, pwidth, pheight);
							pbmp.setPixels(rgb, 0, pwidth, 0, 0, pwidth, pheight);
							Matrix mtx = new Matrix();
							mtx.postRotate(90);
							Bitmap rotatedBMP = Bitmap.createBitmap(pbmp, 0, 0, pwidth, pheight, mtx, true);
							BitmapDrawable bmd = new BitmapDrawable(rotatedBMP);
							iView.setImageDrawable(bmd);
					} catch (Exception e) { 
						e.printStackTrace();
					} 
				}
				cnt++;
				camera.addCallbackBuffer(cameraBuffer);
			}
		});
		camera.startPreview();

		wm.addView(iView, params3);
	}

	/**
	 * YUV420データをBitmapに変換します
	 * @param rgb
	 * @param yuv420sp
	 * @param width
	 * @param height
	 */
	private static final void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
		final int frameSize = width * height;
		for (int j = 0, yp = 0; j < height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
			for (int i = 0; i < width; i++, yp++) {
				int y = (0xff & ((int) yuv420sp[yp])) - 16;
				if (y < 0) y = 0;
				if ((i & 1) == 0) {
						v = (0xff & yuv420sp[uvp++]) - 128;
						u = (0xff & yuv420sp[uvp++]) - 128;
				}
				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 833 * v - 400 * u);
				int b = (y1192 + 2066 * u);
				if (r < 0) r = 0; else if (r > 262143) r = 262143;
				if (g < 0) g = 0; else if (g > 262143) g = 262143;
				if (b < 0) b = 0; else if (b > 262143) b = 262143;
				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			}
		}
	}

	private void shutdown()
	{
		this.wm.removeView(iView);
		iView.setFocusable(false);
		iView.setOnTouchListener(null);
		
		this.camera.setPreviewCallback(null);
		this.camera.stopPreview();
		this.camera.release();
		this.camera = null;
		this.pbmp.recycle();
		this.pbmp = null;
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
