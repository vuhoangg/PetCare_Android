package com.example.petcase;

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
    private MediaPlayer mediaPlayer;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static final int SHAKE_THRESHOLD = 500; // Ngưỡng để phát hiện lắc
    private long lastUpdate;
    private float last_x, last_y, last_z;

    private static final String CHANNEL_ID = "alarm_channel"; // ID của channel

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "onReceive triggered");

        // Phát âm thanh báo thức
        mediaPlayer = MediaPlayer.create(context, R.raw.lofichill);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        // Kích hoạt rung
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(40000, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(40000); // Rung trong 20 giây
            }
        }

        // Kích hoạt cảm biến gia tốc
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }

        // Gửi thông báo
        sendNotification(context);
    }

    private void sendNotification(Context context) {
        // Kiểm tra và tạo Notification Channel nếu chưa có (Chỉ trên Android O trở lên)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Alarm Channel",
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Tạo PendingIntent để mở ứng dụng khi nhấn vào thông báo
        Intent notificationIntent = new Intent(context, MainActivity.class); // Thay MainActivity bằng activity bạn muốn mở
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Tạo Notification
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(context, CHANNEL_ID)
                    .setContentTitle("Alarm Triggered!")
                    .setContentText("Tap to stop the alarm.")
                    .setSmallIcon(R.drawable.baseline_edit_note_24)  // Thay bằng icon bạn muốn
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
        }

        // Gửi thông báo
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, notification); // ID thông báo là 1
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long curTime = System.currentTimeMillis();
        if ((curTime - lastUpdate) > 100) {  // Đảm bảo lắng nghe cảm biến chỉ khi có sự thay đổi lớn
            long diffTime = curTime - lastUpdate;
            lastUpdate = curTime;

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Tính toán tốc độ lắc
            float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

            Log.d("AlarmReceiver", "Speed: " + speed); // Log tốc độ lắc để kiểm tra
            Log.d("AlarmReceiver", "X: " + x + " Y: " + y + " Z: " + z);

            if (speed > SHAKE_THRESHOLD) {  // Nếu có lắc mạnh, tắt báo thức
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
        // Không cần xử lý
    }

    private void stopAlarm() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);  // Hủy đăng ký lắng nghe cảm biến sau khi báo thức dừng
        }
    }
}
