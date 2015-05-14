package com.sdsu.cs646.shameetha.testproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.sdsu.cs646.shameetha.testproject.options.AlarmOptionsActivity;
import com.sdsu.cs646.shameetha.testproject.service.AlarmServiceBroadcastReceiver;


public abstract class BaseActivity  extends ActionBarActivity implements android.view.View.OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_main, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_item_new) {
            Intent newAlarmIntent = new Intent(this, AlarmOptionsActivity.class);
            startActivity(newAlarmIntent);
        }
		return super.onOptionsItemSelected(item);
	}

	protected void callMathAlarmScheduleService() {
		Intent mathAlarmServiceIntent = new Intent(this, AlarmServiceBroadcastReceiver.class);
		sendBroadcast(mathAlarmServiceIntent, null);
	}
}
