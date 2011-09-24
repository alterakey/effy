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
