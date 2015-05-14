package com.sdsu.cs646.shameetha.alarmclocktest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;


public class AlarmListActivity extends ActionBarActivity {

    private AlarmListAdapter mAdapter;
    private AlarmDBHelper mDBHelper = new AlarmDBHelper(this);
    private Context mContext;
    private ListView mAlarmListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_alarm_list);
        mAlarmListView = (ListView)findViewById(R.id.alarmList);
        mAdapter = new AlarmListAdapter(this, mDBHelper.getAlarms());
        mAlarmListView.setAdapter(mAdapter);
    }

    @Override
    public void onResume(){
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarm_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add_new_alarm: {
                startDetailsActivity(-1);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            mAdapter.setAlarms(mDBHelper.getAlarms());
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            mAdapter = new AlarmListAdapter(this, mDBHelper.getAlarms());
            mAlarmListView.setAdapter(mAdapter);
        }
    }

    /** Set the alarm as enavled */
    public void setAlarmEnabled(long id, boolean isEnabled) {
        AlarmBroadcastReceiver.cancelAlarms(this);
        AlarmModel model = mDBHelper.getAlarm(id);
        mDBHelper.updateAlarm(model);
        AlarmBroadcastReceiver.setAlarms(this);
    }

    /** Start AlarmDetailsActivity for result */
    public void startDetailsActivity(long id) {
        Intent intent = new Intent(this, AlarmDetailsActivity.class);
        intent.putExtra("id", id);
        startActivityForResult(intent, 0);
    }

    public void deleteAlarm(long id) {
        final long alarmId = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please confirm")
                .setTitle("Delete alarm?")
                .setCancelable(true)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlarmBroadcastReceiver.cancelAlarms(mContext);
                        mDBHelper.deleteAlarm(alarmId);
                        mAdapter.setAlarms(mDBHelper.getAlarms());
                        mAdapter.notifyDataSetChanged();
                        AlarmBroadcastReceiver.setAlarms(mContext);
                    }
                }).show();
    }
}
