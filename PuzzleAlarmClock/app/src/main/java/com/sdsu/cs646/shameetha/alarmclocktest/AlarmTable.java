package com.sdsu.cs646.shameetha.alarmclocktest;

import android.provider.BaseColumns;

/**
 * Created by Shameetha on 4/29/15.
 */
public final class AlarmTable {

        public AlarmTable() {}

        public static abstract class Alarm implements BaseColumns {
            public static final String TABLE_NAME = "alarm";
            public static final String COLUMN_NAME_ALARM_NAME = "name";
            public static final String COLUMN_NAME_ALARM_TIME_HOUR = "hour";
            public static final String COLUMN_NAME_ALARM_TIME_MINUTE = "minute";
            public static final String COLUMN_NAME_ALARM_REPEAT_DAYS = "days";
            public static final String COLUMN_NAME_ALARM_REPEAT_WEEKLY = "weekly";
            public static final String COLUMN_NAME_ALARM_TONE = "tone";
            public static final String COLUMN_NAME_ALARM_ENABLED = "isEnabled";
            public static final String COLUMN_NAME_ALARM_REPEAT_ONCE = "repeatOnce";
            public static final String COLUMN_NAME_ALARM_DIFFICULTY = "difficulty";
            public static final String COLUMN_NAME_ALARM_QUESTIONS = "questions";
            public static final String COLUMN_NAME_ALARM_VIBRATION = "vibration";


        }

    }