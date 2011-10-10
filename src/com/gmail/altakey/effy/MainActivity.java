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
import android.content.Intent;
import android.widget.Toast;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		Button bb = (Button)findViewById(R.id.button1);
		bb.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intents = new Intent(MainActivity.this, MainService.class);
				if(MainService.isRunning){
					stopService(intents);
					Toast.makeText(MainActivity.this, "stop", Toast.LENGTH_LONG).show();
				}else{
					startService(intents);
					Toast.makeText(MainActivity.this, "start", Toast.LENGTH_LONG).show();
				}
			}
		});
    }
}
