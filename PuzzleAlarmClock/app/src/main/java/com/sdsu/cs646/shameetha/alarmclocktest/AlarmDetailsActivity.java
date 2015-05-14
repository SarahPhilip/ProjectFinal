package com.sdsu.cs646.shameetha.alarmclocktest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;


public class AlarmDetailsActivity extends ActionBarActivity {

    private AlarmDBHelper mDBHelper = new AlarmDBHelper(this);
    private AlarmModel mAlarmDetails;
    private TimePicker mTimePicker;
    private EditText mEditName;
    private AlarmToggleButton mChkWeekly;
    private AlarmToggleButton mVibrate;
    private TextView mToneSelection;
    private ArrayList<Integer> mSelectedItems;
    boolean checked[] = new boolean[7];
    int difficulty;
    int questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alarm_details);

        getSupportActionBar().setTitle("Create New Alarm");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTimePicker = (TimePicker) findViewById(R.id.alarm_details_time_picker);
        mEditName = (EditText) findViewById(R.id.alarm_details_name);
        mChkWeekly = (AlarmToggleButton) findViewById(R.id.alarm_details_repeat_weekly);
        mVibrate = (AlarmToggleButton) findViewById(R.id.alarm_details_vibrate);
        mToneSelection = (TextView) findViewById(R.id.alarm_label_tone_selection);

        long id = getIntent().getExtras().getLong("id");
        if (id == -1) {
            mAlarmDetails = new AlarmModel();
        }
        else {
            mAlarmDetails = mDBHelper.getAlarm(id);
            mTimePicker.setCurrentMinute(mAlarmDetails.timeMinute);
            mTimePicker.setCurrentHour(mAlarmDetails.timeHour);
            mEditName.setText(mAlarmDetails.name);
            mChkWeekly.setChecked(mAlarmDetails.repeatWeekly);
            mVibrate.setChecked(mAlarmDetails.vibration);
            mToneSelection.setText(RingtoneManager.getRingtone(this, mAlarmDetails.alarmTone).getTitle(this));
        }

        final LinearLayout ringToneContainer = (LinearLayout) findViewById(R.id.alarm_ringtone_container);
        ringToneContainer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select ringtone for notifications:");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                if (mAlarmDetails.alarmTone != null) {
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, mAlarmDetails.alarmTone);
                } else {
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                }
                startActivityForResult(intent, 1);
            }
        });

        final RelativeLayout repeatDaysContainer = (RelativeLayout) findViewById(R.id.repeat_days_container);
        Arrays.fill(checked, Boolean.FALSE);
        if(mAlarmDetails.getRepeatingDay(mAlarmDetails.SUNDAY)){
            checked[mAlarmDetails.SUNDAY] = true;
        }
        if(mAlarmDetails.getRepeatingDay(mAlarmDetails.MONDAY)){
            checked[mAlarmDetails.MONDAY] = true;
        }
        if(mAlarmDetails.getRepeatingDay(mAlarmDetails.TUESDAY)){
            checked[mAlarmDetails.TUESDAY] = true;
        }
        if(mAlarmDetails.getRepeatingDay(mAlarmDetails.WEDNESDAY)){
            checked[mAlarmDetails.WEDNESDAY] = true;
        }
        if(mAlarmDetails.getRepeatingDay(mAlarmDetails.THURSDAY)){
            checked[mAlarmDetails.THURSDAY] = true;
        }
        if(mAlarmDetails.getRepeatingDay(mAlarmDetails.FRIDAY)){
            checked[mAlarmDetails.FRIDAY] = true;
        }
        if(mAlarmDetails.getRepeatingDay(mAlarmDetails.SATURDAY)){
            checked[mAlarmDetails.SATURDAY] = true;
        }
        if(areAllFalse(checked)) {
            checked[(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1)] = true;
            mAlarmDetails.setRepeatingDay((Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1),true);
        }
        setRepeatingDays();
        repeatDaysContainer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSelectedItems = new ArrayList<Integer>();

                if(mAlarmDetails.getRepeatingDay(mAlarmDetails.SUNDAY)){
                    mSelectedItems.add(mAlarmDetails.SUNDAY);
                }
                if(mAlarmDetails.getRepeatingDay(mAlarmDetails.MONDAY)){
                    mSelectedItems.add(mAlarmDetails.MONDAY);
                }
                if(mAlarmDetails.getRepeatingDay(mAlarmDetails.TUESDAY)){
                    mSelectedItems.add(mAlarmDetails.TUESDAY);
                }
                if(mAlarmDetails.getRepeatingDay(mAlarmDetails.WEDNESDAY)){
                    mSelectedItems.add(mAlarmDetails.WEDNESDAY);
                }
                if(mAlarmDetails.getRepeatingDay(mAlarmDetails.THURSDAY)){
                    mSelectedItems.add(mAlarmDetails.THURSDAY);
                }
                if(mAlarmDetails.getRepeatingDay(mAlarmDetails.FRIDAY)){
                    mSelectedItems.add(mAlarmDetails.FRIDAY);
                }
                if(mAlarmDetails.getRepeatingDay(mAlarmDetails.SATURDAY)){
                    mSelectedItems.add(mAlarmDetails.SATURDAY);
                }
                if(areAllFalse(checked)) {
                    mSelectedItems.add(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(AlarmDetailsActivity.this);
                builder.setTitle("Choose the days")
                        .setMultiChoiceItems(R.array.week_days, checked, new DialogInterface.OnMultiChoiceClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    mSelectedItems.add(which);
                                } else {
                                    if (mSelectedItems.size() > 1) {
                                        mSelectedItems.remove(Integer.valueOf(which));
                                    }
                                    else {
                                        ((AlertDialog) dialog).getListView().setItemChecked(which, true);
                                        checked[which] = true;
                                    }
                                }
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                mAlarmDetails.setRepeatingDay(AlarmModel.SUNDAY, checked[AlarmModel.SUNDAY]);
                                mAlarmDetails.setRepeatingDay(AlarmModel.MONDAY, checked[AlarmModel.MONDAY]);
                                mAlarmDetails.setRepeatingDay(AlarmModel.TUESDAY, checked[AlarmModel.TUESDAY]);
                                mAlarmDetails.setRepeatingDay(AlarmModel.WEDNESDAY, checked[AlarmModel.WEDNESDAY]);
                                mAlarmDetails.setRepeatingDay(AlarmModel.THURSDAY, checked[AlarmModel.THURSDAY]);
                                mAlarmDetails.setRepeatingDay(AlarmModel.FRIDAY, checked[AlarmModel.FRIDAY]);
                                mAlarmDetails.setRepeatingDay(AlarmModel.SATURDAY, checked[AlarmModel.SATURDAY]);
                                setRepeatingDays();
                            }
                        })

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .show();
            }
        });

        final LinearLayout difficultyContainer = (LinearLayout) findViewById(R.id.difficulty_container);
        difficulty = mAlarmDetails.getDifficulty();
        setDifficultyString();
        difficultyContainer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSelectedItems = new ArrayList<Integer>();
                AlertDialog.Builder builder = new AlertDialog.Builder(AlarmDetailsActivity.this);
                builder.setTitle("Choose the level")
                        .setSingleChoiceItems(R.array.difficulty_levels, mAlarmDetails.getDifficulty(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                difficulty = which;
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                mAlarmDetails.setDifficulty(difficulty);
                                setDifficultyString();
                            }
                        })

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .show();
            }
        });
        final LinearLayout questionsContainer = (LinearLayout) findViewById(R.id.questions_container);
        questions = mAlarmDetails.getNumberOfQuestions();
        TextView questionSelection = (TextView) findViewById(R.id.question_numbers);
        questionSelection.setText(String.valueOf(questions));
        questionsContainer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AlarmDetailsActivity.this);
                builder.setTitle("Choose the number of Questions")
                        .setSingleChoiceItems(R.array.questions, mAlarmDetails.getNumberOfQuestions()-1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                questions = which+1;
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                mAlarmDetails.setNumberOfQuestions(questions);
                                TextView questionSelection = (TextView) findViewById(R.id.question_numbers);
                                questionSelection.setText(String.valueOf(questions));
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // removes the AlertDialog in the screen
                            }
                        })
                        .show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1: {
                    mAlarmDetails.alarmTone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                    TextView txtToneSelection = (TextView) findViewById(R.id.alarm_label_tone_selection);
                    txtToneSelection.setText(RingtoneManager.getRingtone(this, mAlarmDetails.alarmTone).getTitle(this));

                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarm_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
            case R.id.action_save_alarm_details: {
                updateModelFromLayout();
                setDay();
                AlarmBroadcastReceiver.cancelAlarms(this);

                if (mAlarmDetails.id < 0) {
                    mDBHelper.createAlarm(mAlarmDetails);
                } else {
                    mDBHelper.updateAlarm(mAlarmDetails);
                }

                AlarmBroadcastReceiver.setAlarms(this);

                setResult(RESULT_OK);
                finish();
                break;
            }
            case R.id.action_delete_alarm_details: {
                final long alarmId = mAlarmDetails.id;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Please confirm")
                        .setTitle("Delete alarm?")
                        .setCancelable(true)
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AlarmBroadcastReceiver.cancelAlarms(AlarmDetailsActivity.this);
                                mDBHelper.deleteAlarm(alarmId);
                                AlarmBroadcastReceiver.setAlarms(AlarmDetailsActivity.this);
                                setResult(RESULT_OK);
                                finish();
                            }
                        }).show();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /** Update the alarm details from the layout. */
    private void updateModelFromLayout() {

        TimePicker timePicker = (TimePicker) findViewById(R.id.alarm_details_time_picker);
        mAlarmDetails.timeMinute = timePicker.getCurrentMinute().intValue();
        mAlarmDetails.timeHour = timePicker.getCurrentHour().intValue();
        mAlarmDetails.name = mEditName.getText().toString();
        mAlarmDetails.repeatWeekly = mChkWeekly.isChecked();
        mAlarmDetails.vibration = mVibrate.isChecked();
        mAlarmDetails.setRepeatingDay(AlarmModel.SUNDAY, checked[AlarmModel.SUNDAY]);
        mAlarmDetails.setRepeatingDay(AlarmModel.MONDAY, checked[AlarmModel.MONDAY]);
        mAlarmDetails.setRepeatingDay(AlarmModel.TUESDAY, checked[AlarmModel.TUESDAY]);
        mAlarmDetails.setRepeatingDay(AlarmModel.WEDNESDAY, checked[AlarmModel.WEDNESDAY]);
        mAlarmDetails.setRepeatingDay(AlarmModel.THURSDAY, checked[AlarmModel.THURSDAY]);
        mAlarmDetails.setRepeatingDay(AlarmModel.FRIDAY, checked[AlarmModel.FRIDAY]);
        mAlarmDetails.setRepeatingDay(AlarmModel.SATURDAY, checked[AlarmModel.SATURDAY]);
        mAlarmDetails.isEnabled = true;
        mAlarmDetails.repeatOnce = false;
        mAlarmDetails.setDifficulty(difficulty);
        mAlarmDetails.setNumberOfQuestions(questions);
    }

    /** Update repeating days in alarm object.. */
    private void setDay() {
        Boolean result = false;
        for (int i = 0; i < 7; ++i) {
            if(mAlarmDetails.getRepeatingDay(i))
                result = true;
            mAlarmDetails.setRepeatingDay(i, mAlarmDetails.getRepeatingDay(i));
        }
        if(!result)
            mAlarmDetails.setRepeatingDay((Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1), true);
    }

    /** Check if the array contains all false values. Return Boolean value. */
    public static boolean areAllFalse(boolean[] array)
    {
        for(boolean b : array)
            if(b)
                return false;
        return true;
    }

    /** Set the difficulty level as String. */
    public void setDifficultyString () {
        String difficultyString = "EASY";
        if(mAlarmDetails.getDifficulty() == 0){
            difficultyString = "EASY";
        }
        else if (mAlarmDetails.getDifficulty() == 1) {
            difficultyString = "MEDIUM";
        }
        else if (mAlarmDetails.getDifficulty() == 2) {
            difficultyString = "HARD";
        }

        TextView difficultySelection = (TextView) findViewById(R.id.difficulty_level);
        difficultySelection.setText(difficultyString);
    }

    /** Set repeating days in the view. */
    public void setRepeatingDays() {
        updateTextColor((TextView) findViewById(R.id.alarm_item_sunday), mAlarmDetails.getRepeatingDay(AlarmModel.SUNDAY));
        updateTextColor((TextView) findViewById(R.id.alarm_item_monday), mAlarmDetails.getRepeatingDay(AlarmModel.MONDAY));
        updateTextColor((TextView) findViewById(R.id.alarm_item_tuesday), mAlarmDetails.getRepeatingDay(AlarmModel.TUESDAY));
        updateTextColor((TextView) findViewById(R.id.alarm_item_wednesday), mAlarmDetails.getRepeatingDay(AlarmModel.WEDNESDAY));
        updateTextColor((TextView) findViewById(R.id.alarm_item_thursday), mAlarmDetails.getRepeatingDay(AlarmModel.THURSDAY));
        updateTextColor((TextView) findViewById(R.id.alarm_item_friday), mAlarmDetails.getRepeatingDay(AlarmModel.FRIDAY));
        updateTextColor((TextView) findViewById(R.id.alarm_item_saturday), mAlarmDetails.getRepeatingDay(AlarmModel.SATURDAY));

    }

    /** Update the color of selected repeating day values. */
    private void updateTextColor(TextView view, boolean isOn) {
        if (isOn) {
            view.setTextColor(Color.GREEN);
        } else {
            view.setTextColor(Color.BLACK);
        }
    }
}
