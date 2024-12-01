package com.example.petcase;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petcase.Domain.Reminders;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.UUID;

public class EditReminderActivity extends AppCompatActivity {

    private Button btnEditPet, btnGoback, btnDate, btnTime;
    private EditText titleInput, notesInput;
    private TextView lblDateAndTime, btnUpdate;

    // Date and Time format
    private DateFormat fmtDateAndTime = DateFormat.getDateTimeInstance();

    // Calendar instance
    Calendar myCalendar = Calendar.getInstance();

    // Firebase Database reference
    DatabaseReference databaseReminders;

    // Date picker listener
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    // Time picker listener
    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        @Override
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reminder);

        // Initialize UI components
        lblDateAndTime = findViewById(R.id.lblDateAndTime);
        btnDate = findViewById(R.id.btnDate);
        btnTime = findViewById(R.id.btnTime);
        btnUpdate = findViewById(R.id.btnUpdate);
        titleInput = findViewById(R.id.titleInput);




        databaseReminders = FirebaseDatabase.getInstance().getReference("Reminders");

        // Date Picker
        btnDate.setOnClickListener(v -> new DatePickerDialog(EditReminderActivity.this, d,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        // Time Picker
        btnTime.setOnClickListener(v -> new TimePickerDialog(EditReminderActivity.this, t,
                myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE), true).show());

        // Save Reminder
        btnUpdate.setOnClickListener(v -> {
            String message = titleInput.getText().toString().trim();
            String date = DateFormat.getDateInstance().format(myCalendar.getTime());
            String time = DateFormat.getTimeInstance().format(myCalendar.getTime());

            if (message.isEmpty()) {
                Toast.makeText(EditReminderActivity.this, "Please enter a title!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get petId from SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("Pet", Context.MODE_PRIVATE);
            String petId = sharedPreferences.getString("petId", null);

            if (petId == null) {
                Toast.makeText(EditReminderActivity.this, "No pet selected! Please select a pet first.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Generate unique reminderId
            String reminderId = UUID.randomUUID().toString();

            // Create Reminder object
            Reminders reminder = new Reminders(reminderId, petId, date, time, message);

            // Save Reminder to Firebase
            databaseReminders.child(reminderId).setValue(reminder)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Check if we can schedule exact alarms only on API 31 or higher
                            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            if (alarmManager != null) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    // Only check canScheduleExactAlarms on API 31 and above
                                    if (alarmManager.canScheduleExactAlarms()) {
                                        try {
                                            // Set Alarm
                                            Intent intent = new Intent(EditReminderActivity.this, AlarmReceiver.class);
                                            intent.putExtra("MESSAGE", message);

                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                                    EditReminderActivity.this,
                                                    0,
                                                    intent,
                                                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                                            );

                                            alarmManager.setExact(
                                                    AlarmManager.RTC_WAKEUP,
                                                    myCalendar.getTimeInMillis(),
                                                    pendingIntent
                                            );

                                            Toast.makeText(EditReminderActivity.this, "Reminder updated for your pet!", Toast.LENGTH_SHORT).show();
                                            finish(); // Close the activity after saving
                                        } catch (SecurityException e) {
                                            Toast.makeText(EditReminderActivity.this, "Failed to set alarm. Permission not granted.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(EditReminderActivity.this, "Exact alarms are not allowed. Please enable them in settings.", Toast.LENGTH_SHORT).show();
                                        // Optionally, navigate user to settings
                                        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                                        startActivity(intent);
                                    }
                                } else {
                                    // For devices with lower than API 31, we can still set the alarm without the exact alarm permission check
                                    try {
                                        // Set Alarm
                                        Intent intent = new Intent(EditReminderActivity.this, AlarmReceiver.class);
                                        intent.putExtra("MESSAGE", message);

                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                                EditReminderActivity.this,
                                                0,
                                                intent,
                                                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                                        );

                                        alarmManager.setExact(
                                                AlarmManager.RTC_WAKEUP,
                                                myCalendar.getTimeInMillis(),
                                                pendingIntent
                                        );

                                        Toast.makeText(EditReminderActivity.this, "Reminder updated for your pet!", Toast.LENGTH_SHORT).show();
                                        finish(); // Close the activity after saving
                                    } catch (SecurityException e) {
                                        Toast.makeText(EditReminderActivity.this, "Failed to set alarm. Permission not granted.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(EditReminderActivity.this, "Failed to update reminder.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Initialize the label with the current date and time
        updateLabel();
    }
}
