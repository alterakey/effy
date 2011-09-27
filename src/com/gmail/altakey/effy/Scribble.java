/**
 * Copyright (C) 2011 Takahiro Yoshimura <taky@cs.monolithworks.co.jp>
 * All rights reserved.
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

	public static Scribble getInstance(int w, int h)
	{
		if (instance != null)
		{
			if (instance.width != w || instance.height != h)
			{
				throw new InvalidSizeError(
					String.format("scribble size does not match ((%d, %d) <=> (%d, %d))",
								  instance.width, instance.height, w, h)
					);
			}
			return instance;
		}
		return new Scribble(w, h);
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
