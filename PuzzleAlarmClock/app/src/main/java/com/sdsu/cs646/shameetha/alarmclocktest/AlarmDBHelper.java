package com.sdsu.cs646.shameetha.alarmclocktest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shameetha on 4/29/15.
 */
public class AlarmDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "puzzleAlarm.db";
    private static final String SQL_CREATE_ALARM = "CREATE TABLE IF NOT EXISTS " + AlarmTable.Alarm.TABLE_NAME + " (" +
            AlarmTable.Alarm._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            AlarmTable.Alarm.COLUMN_NAME_ALARM_NAME + " TEXT," +
            AlarmTable.Alarm.COLUMN_NAME_ALARM_TIME_HOUR + " INTEGER," +
            AlarmTable.Alarm.COLUMN_NAME_ALARM_TIME_MINUTE + " INTEGER," +
            AlarmTable.Alarm.COLUMN_NAME_ALARM_REPEAT_DAYS + " TEXT," +
            AlarmTable.Alarm.COLUMN_NAME_ALARM_REPEAT_WEEKLY + " BOOLEAN," +
            AlarmTable.Alarm.COLUMN_NAME_ALARM_TONE + " TEXT," +
            AlarmTable.Alarm.COLUMN_NAME_ALARM_ENABLED + " BOOLEAN," +
            AlarmTable.Alarm.COLUMN_NAME_ALARM_REPEAT_ONCE + " BOOLEAN," +
            AlarmTable.Alarm.COLUMN_NAME_ALARM_DIFFICULTY + " INTEGER," +
            AlarmTable.Alarm.COLUMN_NAME_ALARM_QUESTIONS + " INTEGER," +
            AlarmTable.Alarm.COLUMN_NAME_ALARM_VIBRATION + " BOOLEAN" +
            " )";
    private static final String SQL_DELETE_ALARM =
            "DROP TABLE IF EXISTS " + AlarmTable.Alarm.TABLE_NAME;

    public AlarmDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ALARM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ALARM);
        onCreate(db);
    }

    private AlarmModel populateModel(Cursor c) {
        AlarmModel model = new AlarmModel();
        model.id = c.getLong(c.getColumnIndex(AlarmTable.Alarm._ID));
        model.name = c.getString(c.getColumnIndex(AlarmTable.Alarm.COLUMN_NAME_ALARM_NAME));
        model.timeHour = c.getInt(c.getColumnIndex(AlarmTable.Alarm.COLUMN_NAME_ALARM_TIME_HOUR));
        model.timeMinute = c.getInt(c.getColumnIndex(AlarmTable.Alarm.COLUMN_NAME_ALARM_TIME_MINUTE));
        model.repeatWeekly = c.getInt(c.getColumnIndex(AlarmTable.Alarm.COLUMN_NAME_ALARM_REPEAT_WEEKLY)) == 0 ? false : true;
        model.vibration = c.getInt(c.getColumnIndex(AlarmTable.Alarm.COLUMN_NAME_ALARM_VIBRATION)) == 0 ? false : true;
        model.alarmTone = c.getString(c.getColumnIndex(AlarmTable.Alarm.COLUMN_NAME_ALARM_TONE)) != "" ? Uri.parse(c.getString(c.getColumnIndex(AlarmTable.Alarm.COLUMN_NAME_ALARM_TONE))) : null;
        model.isEnabled = c.getInt(c.getColumnIndex(AlarmTable.Alarm.COLUMN_NAME_ALARM_ENABLED)) == 0 ? false : true;
        model.repeatOnce = c.getInt(c.getColumnIndex(AlarmTable.Alarm.COLUMN_NAME_ALARM_REPEAT_ONCE)) == 0 ? false : true;
        model.difficulty = c.getInt(c.getColumnIndex(AlarmTable.Alarm.COLUMN_NAME_ALARM_DIFFICULTY));
        model.numberOfQuestions = c.getInt(c.getColumnIndex(AlarmTable.Alarm.COLUMN_NAME_ALARM_QUESTIONS));


        String[] repeatingDays = c.getString(c.getColumnIndex(AlarmTable.Alarm.COLUMN_NAME_ALARM_REPEAT_DAYS)).split(",");
        for (int i = 0; i < repeatingDays.length; ++i) {
            model.setRepeatingDay(i, repeatingDays[i].equals("false") ? false : true);
        }

        return model;
    }

    private ContentValues populateContent(AlarmModel model) {
        ContentValues values = new ContentValues();
        values.put(AlarmTable.Alarm.COLUMN_NAME_ALARM_NAME, model.name);
        values.put(AlarmTable.Alarm.COLUMN_NAME_ALARM_TIME_HOUR, model.timeHour);
        values.put(AlarmTable.Alarm.COLUMN_NAME_ALARM_TIME_MINUTE, model.timeMinute);
        values.put(AlarmTable.Alarm.COLUMN_NAME_ALARM_REPEAT_WEEKLY, model.repeatWeekly);
        values.put(AlarmTable.Alarm.COLUMN_NAME_ALARM_VIBRATION, model.vibration);
        values.put(AlarmTable.Alarm.COLUMN_NAME_ALARM_TONE, model.alarmTone != null ? model.alarmTone.toString() : "");
        values.put(AlarmTable.Alarm.COLUMN_NAME_ALARM_ENABLED, model.isEnabled);
        values.put(AlarmTable.Alarm.COLUMN_NAME_ALARM_REPEAT_ONCE, model.repeatOnce);
        String repeatingDays = "";
        for (int i = 0; i < 7; ++i) {
            repeatingDays += model.getRepeatingDay(i) + ",";
        }

        values.put(AlarmTable.Alarm.COLUMN_NAME_ALARM_REPEAT_DAYS, repeatingDays);
        values.put(AlarmTable.Alarm.COLUMN_NAME_ALARM_DIFFICULTY, model.difficulty);
        values.put(AlarmTable.Alarm.COLUMN_NAME_ALARM_QUESTIONS, model.numberOfQuestions);
        return values;
    }

    /** Create a new alarm. */
    public boolean createAlarm(AlarmModel model) {
        ContentValues values = populateContent(model);
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            database = this.getWritableDatabase();
            if (database.isOpen()) {
                database.beginTransaction();
                database.insert(AlarmTable.Alarm.TABLE_NAME, null, values);
                database.setTransactionSuccessful();
                return true;
            } else {
                return true;
            }
        } catch (SQLiteException e) {
            Log.e("PuzzleDB", e.getMessage());
            return false;
        } finally {
            database.endTransaction();
            closeDB();
        }
    }

    /** Update the alarm. */
    public boolean updateAlarm(AlarmModel model) {
        ContentValues values = populateContent(model);
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            database = this.getWritableDatabase();
            if (database.isOpen()) {
                database.beginTransaction();
                database.update(AlarmTable.Alarm.TABLE_NAME, values, AlarmTable.Alarm._ID + " = ?", new String[]{String.valueOf(model.id)});
                database.setTransactionSuccessful();
                return true;
            } else {
                return true;
            }
        } catch (SQLiteException e) {
            Log.e("PuzzleDB", e.getMessage());
            return false;
        } finally {
            database.endTransaction();
            closeDB();
        }
    }

    /** Get alarm with id. */
    public AlarmModel getAlarm(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + AlarmTable.Alarm.TABLE_NAME + " WHERE " + AlarmTable.Alarm._ID + " = " + id;

        Cursor c = db.rawQuery(select, null);

        if (c.moveToNext()) {
            return populateModel(c);
        }
        c.close();
        closeDB();
        return null;
    }

    /** Get the list of all alarms. */
    public List<AlarmModel> getAlarms() {
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + AlarmTable.Alarm.TABLE_NAME;

        Cursor c = db.rawQuery(select, null);

        List<AlarmModel> alarmList = new ArrayList<AlarmModel>();

        while (c.moveToNext()) {
            alarmList.add(populateModel(c));
        }

        if (!alarmList.isEmpty()) {
            return alarmList;
        }
        return null;
    }

    /** Delete the alarm. */
    public boolean deleteAlarm(long id) {
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            database = this.getWritableDatabase();
            if (database.isOpen()) {
                database.beginTransaction();
                database.delete(AlarmTable.Alarm.TABLE_NAME, AlarmTable.Alarm._ID + " = ?", new String[]{String.valueOf(id)});
                database.setTransactionSuccessful();
                return true;
            } else {
                return true;
            }
        } catch (SQLiteException e) {
            Log.e("PuzzleDB", e.getMessage());
            return false;
        } finally {
            database.endTransaction();
            closeDB();
        }
    }

    /** Switch off the alarm */
    public boolean offAlarm(AlarmModel alarm) {
        ContentValues values = new ContentValues();
        values.put(AlarmTable.Alarm.COLUMN_NAME_ALARM_ENABLED, alarm.isEnabled);
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            database = this.getWritableDatabase();
            if (database.isOpen()) {
                database.beginTransaction();
                database.update(AlarmTable.Alarm.TABLE_NAME, values, AlarmTable.Alarm._ID + " = ?", new String[]{String.valueOf(alarm.id)});
                database.setTransactionSuccessful();
                return true;
            } else {
                return true;
            }
        } catch (SQLiteException e) {
            Log.e("puzzleDB", e.getMessage());
            return false;
        } finally {
            database.endTransaction();
            closeDB();
        }
    }

    /** Close the database. */
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}

