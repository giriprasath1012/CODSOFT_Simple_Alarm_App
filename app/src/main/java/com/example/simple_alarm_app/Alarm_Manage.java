package com.example.simple_alarm_app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Alarm_Manage extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private String adetails[][];
    HashMap<String, String> item;
    ArrayList<HashMap<String, String>> alist;
    SimpleAdapter sa;
    ListView lst;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_manage);

        databaseHelper = new DatabaseHelper(this);

        lst = findViewById(R.id.ListViewAlarms);

        ArrayList<String> dbData = databaseHelper.getAllAlarm();
        adetails = new String[dbData.size()][3];
        alist = new ArrayList<>();

        for (int i = 0; i < dbData.size(); i++) {
            adetails[i] = new String[3];
            String arrData = dbData.get(i);
            String[] strData = arrData.split(java.util.regex.Pattern.quote("$"));
            adetails[i][0] = strData[0];
            adetails[i][1] = formatTime(Integer.parseInt(strData[1]), Integer.parseInt(strData[2]));
            adetails[i][2] = strData[3].equals("1") ? "On" : "Off";

            item = new HashMap<>();
            item.put("id", adetails[i][0]);
            item.put("hour", adetails[i][1]);
            item.put("is_active", adetails[i][2]);
            alist.add(item);
        }

        sa = new SimpleAdapter(this, alist,
                R.layout.list_alarm_item,
                new String[]{"hour", "is_active"},
                new int[]{R.id.line_1, R.id.line_2}
        );

        lst.setAdapter(sa);

        sa.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if (view.getId() == R.id.line_2 && data != null) {
                    final ToggleButton toggleButton = (ToggleButton) view;
                    toggleButton.setChecked(data.equals("On"));
                    toggleButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            int position = lst.getPositionForView((View) v.getParent());
                            String clickedItemId = alist.get(position).get("id");
                            String clickedtime = alist.get(position).get("hour");
                            updateItem(clickedItemId, toggleButton.isChecked() ? true : false,clickedtime);
                        }
                    });
                    return true;
                }
                return false;
            }
        });
    }

    public void onDeleteButtonClick(View view) {

        int position = lst.getPositionForView((View) view.getParent());
        String clickedItemId = alist.get(position).get("id");
        deleteItem(clickedItemId);
    }

    private void deleteItem(String itemId) {

        databaseHelper.deleteAlarm(Integer.parseInt(itemId));
        Toast.makeText(getApplicationContext(), "Alarm Deleted", Toast.LENGTH_SHORT).show();

        updateListView();
    }



    private void updateItem(String itemId, boolean status, String time) {
        Toast.makeText(getApplicationContext(), "Alarm Updated", Toast.LENGTH_SHORT).show();

        int newStatus = status ? 1 : 0;

        if (newStatus == 1) {
            String time_in_24 = convert12to24(time);


            int hours = Integer.parseInt(time_in_24.split(":")[0]);
            int minutes = Integer.parseInt(time_in_24.split(":")[1]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hours);
            calendar.set(Calendar.MINUTE, minutes);
            calendar.set(Calendar.SECOND, 0);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

        databaseHelper.updateAlarm(Integer.parseInt(itemId), newStatus);
        updateListView();
    }



    private void updateListView() {
        // Update the list view after database operations
        ArrayList<String> dbData = databaseHelper.getAllAlarm();
        adetails = new String[dbData.size()][3];
        alist = new ArrayList<>();

        for (int i = 0; i < dbData.size(); i++) {
            adetails[i] = new String[3];
            String arrData = dbData.get(i);
            String[] strData = arrData.split(java.util.regex.Pattern.quote("$"));
            adetails[i][0] = strData[0];
            adetails[i][1] = formatTime(Integer.parseInt(strData[1]), Integer.parseInt(strData[2]));
            adetails[i][2] = strData[3].equals("1") ? "On" : "Off";

            item = new HashMap<>();
            item.put("id", adetails[i][0]);
            item.put("hour", adetails[i][1]);
            item.put("is_active", adetails[i][2]);
            alist.add(item);
        }

        sa = new SimpleAdapter(this, alist,
                R.layout.list_alarm_item,
                new String[]{"hour", "is_active"},
                new int[]{R.id.line_1, R.id.line_2}
        );


        lst.setAdapter(sa);
        sa.notifyDataSetChanged();
    }

    public static String convert12to24(String time12) {

        String[] timeParts = time12.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1].substring(0, 2));
        String period = timeParts[1].substring(3);


        if (period.equalsIgnoreCase("PM") && hours != 12) {
            hours += 12;
        } else if (period.equalsIgnoreCase("AM") && hours == 12) {
            hours = 0;
        }


        return String.format("%02d:%02d", hours, minutes);
    }

    private String formatTime(int hour, int minute) {
        String period = "AM";
        if (hour >= 12) {
            period = "PM";
            if (hour > 12) {
                hour -= 12;
            }
        }
        return String.format("%02d:%02d %s", hour, minute, period);
    }
}
