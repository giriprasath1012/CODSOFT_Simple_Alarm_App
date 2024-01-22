package com.example.simple_alarm_app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AlarmNotification extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_notification);

        Button snooze = findViewById(R.id.btnsnooze);
        Button stop = findViewById(R.id.btnstop);

        snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Snooze !!! Alarm Ring in 5 mins", Toast.LENGTH_SHORT).show();
                AlarmUtils.snoozeAlarm(getApplicationContext());
                finish();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Alarm Off !!!!", Toast.LENGTH_SHORT).show();
                AlarmUtils.stopAlarm();
                finish();
            }
        });
    }
}
