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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.util.Log;
import android.util.DisplayMetrics;

import com.example.android.apis.graphics.ColorPickerDialog;

public class DrawActivity extends Activity implements ColorPickerDialog.OnColorChangedListener
{
	private Paint paint = new Paint();

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

		this.initialSetup();
		this.setup();
        setContentView(R.layout.draw);
		this.refresh();
    }

	private void initialSetup()
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

		int color = pref.getInt("_color", 0xffffffff);
		this.paint.setColor(color);
		this.paint.setStrokeWidth(5.0f);
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
		ImageView view = (ImageView)findViewById(R.id.view);
		view.setImageDrawable(new BitmapDrawable(Scribble.bitmap_cover));
	}

	private void restyle()
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

		int alpha = Integer.parseInt(pref.getString("drop_alpha", "192"));
		findViewById(R.id.view).setBackgroundColor(alpha << 24);
	}

	private class Snapshot
	{
		public float x;
		public float y;

	}

	public Snapshot snapshot;

	private void plot(float x, float y)
	{
		Bitmap bitmap = Scribble.getInstance().bitmap;
		Canvas canvas = new Canvas(bitmap);
		if (this.snapshot == null)
			canvas.drawPoint(x, y, this.paint);
		else
			canvas.drawLine(this.snapshot.x, this.snapshot.y, x, y, this.paint);
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
		this.restyle();
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
			plot(x, y);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.draw, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId())
		{
		case R.id.menu_pen_color:
			new ColorPickerDialog(this, this, this.paint.getColor()).show();
			return true;
		case R.id.menu_preferences:
			startActivity(new Intent(this, ConfigActivity.class));
			return true;
		case R.id.menu_close:
			Intent intents = new Intent(DrawActivity.this, MainService.class);
			stopService(intents);
			Toast.makeText(DrawActivity.this, getText(R.string.service_stopped), Toast.LENGTH_LONG).show();
			this.finish();
		}
		return true;
	}

	public void colorChanged(int color)
	{
		SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
		edit.putInt("_color", color);
		edit.commit();

		this.paint.setColor(color);
	}
}
