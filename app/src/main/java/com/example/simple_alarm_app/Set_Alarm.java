package com.example.simple_alarm_app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class Set_Alarm extends AppCompatActivity {

    private static final int PICK_RINGTONE_REQUEST_CODE = 1;

    private TimePicker timePicker;
    private Button btnChooseTone;
    private Button btnSaveAlarm;

    private Uri selectedRingtoneUri;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        timePicker = findViewById(R.id.timePicker);
        btnChooseTone = findViewById(R.id.btnChooseTone);
        btnSaveAlarm = findViewById(R.id.btnSaveAlarm);

        databaseHelper = new DatabaseHelper(this);

        btnChooseTone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRingtonePicker();
            }
        });

        btnSaveAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAlarm();
            }
        });
    }

    private void openRingtonePicker() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Sound");
        startActivityForResult(intent, PICK_RINGTONE_REQUEST_CODE);
    }

    private void saveAlarm() {
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();

        if (selectedRingtoneUri != null) {

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            long result = databaseHelper.insertAlarm(hour, minute, selectedRingtoneUri.toString());

            if (result != -1)
            {

                setAlarm(calendar);

                startActivity(new Intent(Set_Alarm.this, MainActivity.class));
                Toast.makeText(Set_Alarm.this, "Alarm set!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(Set_Alarm.this, "Failed to save alarm", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(Set_Alarm.this, "Please choose an Alarm Tone", Toast.LENGTH_SHORT).show();
        }
    }

    private void setAlarm(Calendar calendar)
    {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_RINGTONE_REQUEST_CODE && resultCode == RESULT_OK) {
            selectedRingtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (selectedRingtoneUri != null) {
                Ringtone ringtone = RingtoneManager.getRingtone(this, selectedRingtoneUri);
                String selectedRingtoneTitle = ringtone.getTitle(this);

                Toast.makeText(Set_Alarm.this, "Selected Alarm Tone: " + selectedRingtoneTitle, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
