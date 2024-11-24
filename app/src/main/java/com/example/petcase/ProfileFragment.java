package com.example.petcase;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;

public class ProfileFragment extends Fragment {

    // Date and Time format
    DateFormat fmtDateAndTime = DateFormat.getDateTimeInstance();
    TextView lblDateAndTime;
    TextView btnSave;
    EditText titleInput;


    // Calendar instance
    Calendar myCalendar = Calendar.getInstance();

    // Firebase Database reference
    DatabaseReference databaseReminders;

    // Date picker listener
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    // Time picker listener
    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            myCalendar.set(Calendar.MINUTE, minute);
            updateLabel();
        }
    };

    // Update label with selected date and time
    private void updateLabel() {
        lblDateAndTime.setText(fmtDateAndTime.format(myCalendar.getTime()));
    }

    @SuppressLint("ScheduleExactAlarm")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI components
        lblDateAndTime = view.findViewById(R.id.lblDateAndTime);
        Button btnDate = view.findViewById(R.id.btnDate);
        Button btnTime = view.findViewById(R.id.btnTime);
        btnSave = view.findViewById(R.id.btnSave);
        titleInput = view.findViewById(R.id.titleInput);

        // Initialize Firebase Database
        databaseReminders = FirebaseDatabase.getInstance().getReference("reminders");

        // Date Picker
        btnDate.setOnClickListener(v -> new DatePickerDialog(getContext(), d,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        // Time Picker
        btnTime.setOnClickListener(v -> new TimePickerDialog(getContext(), t,
                myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE), true).show());

        // Phần trong sự kiện Save
        btnSave.setOnClickListener(v -> {
            String reminderMessage = lblDateAndTime.getText().toString();

            if (reminderMessage.isEmpty()) {
                Toast.makeText(getContext(), "Please select a date and time!", Toast.LENGTH_SHORT).show();
            } else {
                // Lưu vào Firebase (nếu cần)
                String reminderId = databaseReminders.push().getKey();
                if (reminderId != null) {
                    databaseReminders.child(reminderId).setValue(reminderMessage);
                }
                String title = titleInput.getText().toString().trim();
                // Thiết lập báo thức
                AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getContext(), AlarmReceiver.class);
                intent.putExtra("MESSAGE", "It's time for: " + title);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        myCalendar.getTimeInMillis(),
                        pendingIntent
                );

                Toast.makeText(getContext(), "Reminder saved and alarm set!", Toast.LENGTH_SHORT).show();
            }
        });

        updateLabel();
        return view;
    }
}
