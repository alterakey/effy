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

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.MotionEvent;
import android.content.Intent;
import android.widget.Toast;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.ImageView;
import android.view.Window;
import android.view.WindowManager;

import android.util.Log;
import android.util.DisplayMetrics;

public class DrawActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(
			WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN
		);
		this.setup();
        setContentView(R.layout.draw);
		this.refresh();
    }

	private void setup()
	{
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);

		this.getWindow().setLayout(dm.widthPixels, dm.heightPixels);
		Scribble.setup(dm.widthPixels, dm.heightPixels);
	}

	private void shutdown()
	{
		Scribble.getInstance().recycle();
	}

	private class Snapshot
	{
		public float x;
		public float y;

	}

	public Snapshot snapshot;

	private void plot(float x, float y, int color)
	{
		Bitmap bitmap = Scribble.getInstance().bitmap;
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setStrokeWidth(5.0f);
		if (this.snapshot == null)
			canvas.drawPoint(x, y, paint);
		else
			canvas.drawLine(this.snapshot.x, this.snapshot.y, x, y, paint);
		refresh();
	}

	private void refresh()
	{
		ImageView view = (ImageView)findViewById(R.id.view);
		view.setImageDrawable(new BitmapDrawable(Scribble.getInstance().bitmap));
	}

    @Override
    protected void onResume() {
        super.onResume();
		this.setup();
		this.refresh();
    }

    @Override
    protected void onPause() {
        super.onPause();
		this.shutdown();
    }

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		
		switch (action & MotionEvent.ACTION_MASK) 
		{
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
		case MotionEvent.ACTION_MOVE:
			float x = event.getRawX();
			float y = event.getRawY();
			plot(x, y, 0xffffffff);
			if (this.snapshot == null)
				this.snapshot = new Snapshot();
			this.snapshot.x = x;
			this.snapshot.y = y;
			break;
		case MotionEvent.ACTION_UP:
			this.snapshot = null;
			return false;
		default:
			return false;
		}
		return true;
	}
}
