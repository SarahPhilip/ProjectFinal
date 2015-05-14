package com.sdsu.cs646.shameetha.alarmclocktest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;


public class AlarmAlertActivity extends ActionBarActivity {

    private MediaPlayer mMediaPlayer;
    private AlarmDBHelper mDBHelper = new AlarmDBHelper(this);
    private AlarmModel mAlarm;
    private puzzleQuestions mPuzzleQuestions;
    private Vibrator mVibrator;
    private boolean mAlarmActive;
    private Uri mTone;
    private boolean mAlarmVibrate;
    AlertDialog dialog;
    Button positiveButton;
    int numberOfQuestions;
    int difficulty;
    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Alarm");
        final Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        setContentView(R.layout.activity_alert);
        String name = getIntent().getStringExtra(AlarmBroadcastReceiver.NAME);
        difficulty = getIntent().getIntExtra(AlarmBroadcastReceiver.DIFFICULTY, 0);
        numberOfQuestions = getIntent().getIntExtra(AlarmBroadcastReceiver.QUESTIONS, 1);
        numberOfQuestions *= 2;
        mAlarmVibrate = getIntent().getBooleanExtra(AlarmBroadcastReceiver.VIBRATE, false);
        this.setTitle(name);
        id = getIntent().getLongExtra(AlarmBroadcastReceiver.ID, -1);
        mTone = Uri.parse(getIntent().getExtras().getString(AlarmBroadcastReceiver.TONE));
        TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.d(getClass().getSimpleName(), "Incoming call: "
                                + incomingNumber);
                        try {
                            mMediaPlayer.pause();
                        } catch (IllegalStateException e) {

                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        Log.d(getClass().getSimpleName(), "Call State Idle");
                        try {
                            mMediaPlayer.start();
                        } catch (IllegalStateException e) {

                        }
                        break;
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
        startAlarm();
        final EditText input = new EditText(this);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
            switch (difficulty) {
                case 0:
                    mPuzzleQuestions = new puzzleQuestions(3);
                    break;
                case 1:
                    mPuzzleQuestions = new puzzleQuestions(4);
                    break;
                case 2:
                    mPuzzleQuestions = new puzzleQuestions(5);
                    break;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(AlarmAlertActivity.this);
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setTitle(mPuzzleQuestions.toString());
            builder.setMessage("?");
            builder.setView(input);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    if (checkAnswer(input.getText().toString())) {
                        if (numberOfQuestions > 0) {
                            dialog.dismiss();
                            callQuestion();
                        } else {
                            dialog.dismiss();
                            mAlarmActive = false;
                            try {
                                mMediaPlayer.stop();
                            } catch (IllegalStateException ILException) {
                            }
                            try {
                                mMediaPlayer.release();
                            } catch (Exception e) {

                            }
                            mAlarm = mDBHelper.getAlarm(id);
                            if (!mAlarm.repeatWeekly && !alarmRepeating())
                                mAlarm.isEnabled = false;
                            mDBHelper.offAlarm(mAlarm);
                            setResult(RESULT_OK);
                            AlarmAlertActivity.this.finish();
                        }
                    } else {
                        dialog.dismiss();
                        callQuestion();
                    }
                }
            });
            builder.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    dialog.dismiss();
                    callQuestion();
                }
            });

            dialog = builder.create();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            dialog.setCancelable(false);
            input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    input.post(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                        }
                    });
                }
            });
            input.requestFocus();
            dialog.show();
            positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setEnabled(false);
            input.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    checkAnswer(input.getText().toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });
    }
    @Override
    protected void onResume() {
        super.onResume();
        mAlarmActive = true;
    }

    @Override
    public void onBackPressed() {
        if (!mAlarmActive) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try {
            if (mVibrator != null)
                mVibrator.cancel();
        } catch (Exception e) {

        }
        try {
            mMediaPlayer.stop();
        } catch (Exception e) {

        }
        try {
            mMediaPlayer.release();
        } catch (Exception e) {

        }
        super.onDestroy();
    }

    /** Starts the alarm sound and vibrator, if set. */
    private void startAlarm() {

        if (mAlarmVibrate) {
            mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] pattern = { 1000, 200, 200, 200 };
            mVibrator.vibrate(pattern, 0);
        }
        mMediaPlayer = new MediaPlayer();
        try {
            if(getIntent().getExtras().getString(AlarmBroadcastReceiver.TONE) == "")
                mTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mMediaPlayer.setVolume(1.0f, 1.0f);
            mMediaPlayer.setDataSource(this, mTone);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepare();
            mMediaPlayer.start();

        } catch (Exception e) {
            mMediaPlayer.release();
            mAlarmActive = false;
        }
    }

    /** Checks if the entered string is the correct answer or nor. Returns Boolean value. */
    public boolean isAnswerCorrect(String answer) {
        boolean correct = false;
        try {
            correct = mPuzzleQuestions.getResult() == Float.parseFloat(answer);
        } catch (NumberFormatException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return correct;
    }

    /** Check the answer, and if its correct, enables the positivite button.  */
    public boolean checkAnswer(String answer) {
        if (!mAlarmActive)
            return false;
        if (isAnswerCorrect(answer)) {
            positiveButton.setEnabled(true);
            numberOfQuestions --;
            return true;
        }
        else {
            positiveButton.setEnabled(false);
            return false;
        }
    }

    /** Shows n number of questions, one after the other.  */
    public void callQuestion() {
        final EditText input = new EditText(this);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        switch (difficulty) {
            case 0:
                mPuzzleQuestions = new puzzleQuestions(3);
                break;
            case 1:
                mPuzzleQuestions = new puzzleQuestions(4);
                break;
            case 2:
                mPuzzleQuestions = new puzzleQuestions(5);
                break;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(AlarmAlertActivity.this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle(mPuzzleQuestions.toString());
        builder.setMessage("?");
        builder.setView(input);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if(checkAnswer(input.getText().toString())) {
                    if (numberOfQuestions > 0) {
                        dialog.dismiss();
                        callQuestion();
                    } else {
                        dialog.dismiss();
                        mAlarmActive = false;
                        try {
                            mMediaPlayer.stop();
                        } catch (IllegalStateException ILException) {
                        }
                        try {
                            mMediaPlayer.release();
                        } catch (Exception e) {

                        }
                        mAlarm = mDBHelper.getAlarm(id);
                        mAlarm.isEnabled = false;
                        mDBHelper.offAlarm(mAlarm);
                        setResult(RESULT_OK);
                        AlarmAlertActivity.this.finish();
                    }
                }
                else {
                    dialog.dismiss();
                    callQuestion();
                }
            }
        });
        builder.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                dialog.dismiss();
                callQuestion();
            }
        });
        dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.setCancelable(false);
        dialog.show();
        positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(false);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Log.v("afterEditText", input.getText().toString());
                checkAnswer(input.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    /** Checks if the alarm is set for more than one day. Returns Boolean value. */
    private boolean alarmRepeating() {
        int numberOfRepeatingDays = 0;
        if (mAlarm.getRepeatingDay(mAlarm.SUNDAY)) {
            numberOfRepeatingDays++;
        }
        if (mAlarm.getRepeatingDay(mAlarm.MONDAY)) {
            numberOfRepeatingDays++;
        }
        if (mAlarm.getRepeatingDay(mAlarm.TUESDAY)) {
            numberOfRepeatingDays++;
        }
        if (mAlarm.getRepeatingDay(mAlarm.WEDNESDAY)) {
            numberOfRepeatingDays++;
        }
        if (mAlarm.getRepeatingDay(mAlarm.THURSDAY)) {
            numberOfRepeatingDays++;
        }
        if (mAlarm.getRepeatingDay(mAlarm.FRIDAY)) {
            numberOfRepeatingDays++;
        }
        if (mAlarm.getRepeatingDay(mAlarm.SATURDAY)) {
            numberOfRepeatingDays++;
        }
        if(numberOfRepeatingDays >= 2)
            return true;
        else
            return false;
    }
}
