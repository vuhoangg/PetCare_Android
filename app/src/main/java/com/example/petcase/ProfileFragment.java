package com.example.petcase;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.petcase.Domain.Reminders;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.UUID;

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
        databaseReminders = FirebaseDatabase.getInstance().getReference("Reminders");

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
            String reminderMessage = titleInput.getText().toString().trim();
            String date = DateFormat.getDateInstance().format(myCalendar.getTime());
            String time = DateFormat.getTimeInstance().format(myCalendar.getTime());

            if (reminderMessage.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a title!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lấy petId từ SharedPreferences
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Pet", Context.MODE_PRIVATE);
            String petId = sharedPreferences.getString("petId", null);

            if (petId == null) {
                Toast.makeText(getContext(), "No pet selected! Please select a pet first.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo reminderId tự động
            String reminderId = UUID.randomUUID().toString(); // Tạo UUID duy nhất cho mỗi reminder

            // Tạo đối tượng Reminder để cập nhật
            Reminders reminder = new Reminders(reminderId, petId, date, time, reminderMessage);

            // Cập nhật Reminder trong Firebase
            databaseReminders.child(reminderId).setValue(reminder)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Thiết lập báo thức
                            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                            Intent intent = new Intent(getContext(), AlarmReceiver.class);
                            intent.putExtra("MESSAGE", reminderMessage); // Chuyển thông điệp của reminder vào Intent

                            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                    getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                            alarmManager.setExact(
                                    AlarmManager.RTC_WAKEUP,
                                    myCalendar.getTimeInMillis(),
                                    pendingIntent
                            );

                            Toast.makeText(getContext(), "Reminder updated for your pet!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to update reminder.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        updateLabel();
        return view;
    }
}
