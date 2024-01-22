package com.example.simple_alarm_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "alarms.db";
    private static final String TABLE_NAME = "alarms";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME +
                " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "hour INTEGER, " +
                "minute INTEGER, " +
                "ringtone_uri TEXT, " +
                "is_active INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertAlarm(int hour, int minute, String ringtoneUri) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("hour", hour);
        values.put("minute", minute);
        values.put("ringtone_uri", ringtoneUri);
        values.put("is_active", 1); // Assuming new alarms are active by default
        db.insert(TABLE_NAME, null, values);
        db.close();
        return 0;
    }


    public ArrayList getAllAlarm()
    {
        ArrayList<String> arr = new ArrayList<>();
        SQLiteDatabase db=getReadableDatabase();

        Cursor c =db.rawQuery("select * from alarms",null);
        if(c.moveToFirst())
        {
            do
            {
                arr.add(c.getString(0)+"$"+c.getString(1)+"$"+c.getString(2)+"$"+c.getString(4));
            }
            while(c.moveToNext());
        }
        db.close();
        return arr;
    }

    public void deleteAlarm(int alarmId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id=?", new String[]{Integer.toString(alarmId)});
        db.close();
    }

    public void updateAlarm(int alarmId, int newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_active", newStatus);

        db.update(TABLE_NAME, values, "id=?", new String[]{Integer.toString(alarmId)});

        db.close();
    }



    // Add other methods as needed for your alarm app

}
