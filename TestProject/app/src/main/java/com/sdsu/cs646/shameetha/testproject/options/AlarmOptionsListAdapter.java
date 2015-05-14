package com.sdsu.cs646.shameetha.testproject.options;

import android.content.Context;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.sdsu.cs646.shameetha.testproject.Alarm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class AlarmOptionsListAdapter extends BaseAdapter implements Serializable {

	private Context context;
	private Alarm alarm;
	private List<AlarmOptions> options = new ArrayList<AlarmOptions>();
	private final String[] repeatDays = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
	private final String[] alarmDifficulties = {"Easy","Medium","Hard"};
	
	private String[] alarmTones;
	private String[] alarmTonePaths;
	
	public AlarmOptionsListAdapter(Context context, Alarm alarm) {
		setContext(context);

				Log.d("AlarmOptionsListAdapter", "Loading Ringtones...");
				
				RingtoneManager ringtoneMgr = new RingtoneManager(getContext());
				
				ringtoneMgr.setType(RingtoneManager.TYPE_ALARM);
				
				Cursor alarmsCursor = ringtoneMgr.getCursor();
				
				alarmTones = new String[alarmsCursor.getCount()+1];
				alarmTones[0] = "Silent"; 
				alarmTonePaths = new String[alarmsCursor.getCount()+1];
				alarmTonePaths[0] = "";
				
				if (alarmsCursor.moveToFirst()) {		    			
					do {
						alarmTones[alarmsCursor.getPosition()+1] = ringtoneMgr.getRingtone(alarmsCursor.getPosition()).getTitle(getContext());
						alarmTonePaths[alarmsCursor.getPosition()+1] = ringtoneMgr.getRingtoneUri(alarmsCursor.getPosition()).toString();
					}while(alarmsCursor.moveToNext());					
				}
				Log.d("AlarmOptionsListAdapter", "Finished Loading " + alarmTones.length + " Ringtones.");
				alarmsCursor.close();
	    setAlarm(alarm);
	}

	@Override
	public int getCount() {
		return options.size();
	}

	@Override
	public Object getItem(int position) {
		return options.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AlarmOptions alarmOptions = (AlarmOptions) getItem(position);
		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		switch (alarmOptions.getType()) {
		case BOOLEAN:
			if(null == convertView || convertView.getId() != android.R.layout.simple_list_item_checked)
			convertView = layoutInflater.inflate(android.R.layout.simple_list_item_checked, null);

			CheckedTextView checkedTextView = (CheckedTextView) convertView.findViewById(android.R.id.text1);
			checkedTextView.setText(alarmOptions.getTitle());
			checkedTextView.setChecked((Boolean) alarmOptions.getValue());
			break;
		case INTEGER:
		case STRING:
		case LIST:
		case MULTIPLE_LIST:
		case TIME:
		default:
			if(null == convertView || convertView.getId() != android.R.layout.simple_list_item_2)
			convertView = layoutInflater.inflate(android.R.layout.simple_list_item_2, null);
			
			TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
			text1.setTextSize(18);
			text1.setText(alarmOptions.getTitle());
			
			TextView text2 = (TextView) convertView.findViewById(android.R.id.text2);
			text2.setText(alarmOptions.getSummary());
			break;
		}

		return convertView;
	}

//	public Alarm getMathAlarm() {
//		for(AlarmOptions preference : options){
//			switch(preference.getKey()){
//				case ALARM_ACTIVE:
//					alarm.setAlarmActive((Boolean) preference.getValue());
//					break;
//				case ALARM_NAME:
//					alarm.setAlarmName((String) preference.getValue());
//					break;
//				case ALARM_TIME:
//					alarm.setAlarmTime((String) preference.getValue());
//					break;
//				case ALARM_DIFFICULTY:
//					alarm.setDifficulty(Alarm.Difficulty.valueOf((String)preference.getValue()));
//					break;
//				case ALARM_TONE:
//					alarm.setAlarmTonePath((String) preference.getValue());
//					break;
//				case ALARM_VIBRATE:
//					alarm.setVibrate((Boolean) preference.getValue());
//					break;
//				case ALARM_REPEAT:
//					alarm.setDays((Alarm.Day[]) preference.getValue());
//					break;
//			}
//		}
//
//		return alarm;
//	}

	public void setAlarm(Alarm alarm) {
		this.alarm = alarm;
		options.clear();
		options.add(new AlarmOptions(AlarmOptions.Key.ALARM_ACTIVE, "Active", null, null, alarm.getAlarmActive(), AlarmOptions.Type.BOOLEAN));
		options.add(new AlarmOptions(AlarmOptions.Key.ALARM_NAME, "Label", alarm.getAlarmName(), null, alarm.getAlarmName(), AlarmOptions.Type.STRING));
		options.add(new AlarmOptions(AlarmOptions.Key.ALARM_TIME, "Set time", alarm.getAlarmTimeString(), null, alarm.getAlarmTime(), AlarmOptions.Type.TIME));
		options.add(new AlarmOptions(AlarmOptions.Key.ALARM_REPEAT, "Repeat", alarm.getRepeatDaysString(), repeatDays, alarm.getDays(), AlarmOptions.Type.MULTIPLE_LIST));
		options.add(new AlarmOptions(AlarmOptions.Key.ALARM_DIFFICULTY, "Difficulty", alarm.getDifficulty().toString(), alarmDifficulties, alarm.getDifficulty(), AlarmOptions.Type.LIST));
		
			Uri alarmToneUri = Uri.parse(alarm.getAlarmTonePath());
			Ringtone alarmTone = RingtoneManager.getRingtone(getContext(), alarmToneUri);
		
		if(alarmTone instanceof Ringtone && !alarm.getAlarmTonePath().equalsIgnoreCase("")){
			options.add(new AlarmOptions(AlarmOptions.Key.ALARM_TONE, "Ringtone", alarmTone.getTitle(getContext()), alarmTones, alarm.getAlarmTonePath(), AlarmOptions.Type.LIST));
		}else{
			options.add(new AlarmOptions(AlarmOptions.Key.ALARM_TONE, "Ringtone", getAlarmTones()[0], alarmTones, null, AlarmOptions.Type.LIST));
		}
		
		options.add(new AlarmOptions(AlarmOptions.Key.ALARM_VIBRATE, "Vibrate", null, null, alarm.getVibrate(), AlarmOptions.Type.BOOLEAN));
	}

	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

//	public String[] getRepeatDays() {
//		return repeatDays;
//	}
//
//	public String[] getAlarmDifficulties() {
//		return alarmDifficulties;
//	}

	public String[] getAlarmTones() {
		return alarmTones;
	}

	public String[] getAlarmTonePaths() {
		return alarmTonePaths;
	}

}
