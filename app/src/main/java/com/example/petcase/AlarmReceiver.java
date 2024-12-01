package com.example.petcase;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver implements SensorEventListener {
    private static MediaPlayer mediaPlayer; // Đặt mediaPlayer là static để có thể truy cập toàn cục
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static final int SHAKE_THRESHOLD = 500;
    private long lastUpdate;
    private float last_x, last_y, last_z;

    private static final String CHANNEL_ID = "alarm_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "onReceive triggered");

        String action = intent.getAction();
        if ("STOP_ALARM".equals(action)) {
            Log.d("AlarmReceiver", "Stop alarm action received");
            stopAlarm();
            return;
        }

        // Lấy thông điệp từ Intent
        String reminderMessage = intent.getStringExtra("MESSAGE");
        if (reminderMessage == null || reminderMessage.isEmpty()) {
            reminderMessage = "Default Reminder";
        }

        // Gửi thông báo
        sendNotification(context, reminderMessage);

        // Phát âm thanh báo thức
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.lofichill);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }

        // Kích hoạt rung
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(40000, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(40000);
            }
        }

        // Kích hoạt cảm biến gia tốc
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    private void sendNotification(Context context, String reminderMessage) {
        // Tạo Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alarm Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for Alarm Notifications");
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Tạo PendingIntent để mở MainActivity
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

// Tạo PendingIntent để tắt báo thức
        Intent stopAlarmIntent = new Intent(context, AlarmReceiver.class);
        stopAlarmIntent.setAction("STOP_ALARM");
        PendingIntent stopAlarmPendingIntent = PendingIntent.getBroadcast(
                context,
                1,
                stopAlarmIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );


        // Tạo Notification với nút "Tắt báo thức"
        @SuppressLint({"NewApi", "LocalSuppress"}) Notification notification = new Notification.Builder(context, CHANNEL_ID)
                .setContentTitle("Alarm Triggered!")
                .setContentText("Reminder: " + reminderMessage)
                .setSmallIcon(R.drawable.baseline_edit_note_24)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.baseline_edit_note_24, "Tắt", stopAlarmPendingIntent) // Nút tắt báo thức
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, notification);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long curTime = System.currentTimeMillis();
        if ((curTime - lastUpdate) > 100) {
            long diffTime = curTime - lastUpdate;
            lastUpdate = curTime;

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
            if (speed > SHAKE_THRESHOLD) {
                Log.d("AlarmReceiver", "Shake detected! Stopping alarm.");
                stopAlarm();
            }

            last_x = x;
            last_y = y;
            last_z = z;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void stopAlarm() {
        Log.d("AlarmReceiver", "stopAlarm() called");
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
            Log.d("AlarmReceiver", "MediaPlayer stopped and released.");
        }

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            sensorManager = null;
            Log.d("AlarmReceiver", "SensorManager unregistered.");
        }
    }
}
