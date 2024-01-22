package com.example.simple_alarm_app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;

public class AlarmUtils {

    private static Ringtone ringtone;
    private static final String ALARM_TRIGGER_ACTION = "com.example.simple_alarm_app.ALARM_TRIGGER";

    public static void playAlarm(Context context) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if (alarmSound == null) {
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        if (alarmSound != null) {
            ringtone = RingtoneManager.getRingtone(context, alarmSound);
            ringtone.play();
            sendAlarmTriggerBroadcast(context);
        }
    }

    public static void stopAlarm() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
    }

    public static void snoozeAlarm(Context context) {
        stopAlarm();


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent snoozeIntent = new Intent(context, AlarmReceiver.class);
        snoozeIntent.setAction(ALARM_TRIGGER_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        long snoozeTime = System.currentTimeMillis() + (5 * 60 * 1000);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent);
    }

    private static void sendAlarmTriggerBroadcast(Context context) {
        Intent intent = new Intent(ALARM_TRIGGER_ACTION);
        context.sendBroadcast(intent);
    }
}
