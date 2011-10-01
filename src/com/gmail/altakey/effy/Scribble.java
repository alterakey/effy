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

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Color;

public class Scribble
{
	private static Scribble instance;

	public Bitmap bitmap;
	public int width;
	public int height;
	
	private Scribble(int w, int h)
	{
		this.width = w;
		this.height = h;
		this.bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
		this.clear();
		instance = this;
	}

	public static Scribble setup(int w, int h)
	{
		return new Scribble(w, h);
	}

	public static Scribble getInstance()
	{
		return instance;
	}

	public void recycle()
	{
		this.bitmap.recycle();
		this.bitmap = null;
		instance = null;
	}

	public void clear()
	{
		bitmap.eraseColor(0x00000000);
	}
}
