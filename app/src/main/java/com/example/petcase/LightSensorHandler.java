package com.example.petcase;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Lớp xử lý cảm biến ánh sáng.
 */
public class LightSensorHandler {
    private final SensorManager sensorManager;
    private final Sensor lightSensor;
    private SensorEventListener listener;

    /**
     * Constructor: Khởi tạo LightSensorHandler với ngữ cảnh ứng dụng.
     *
     * @param context Context từ ứng dụng
     */
    public LightSensorHandler(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    /**
     * Bắt đầu lắng nghe dữ liệu từ cảm biến ánh sáng.
     *
     * @param callback Callback khi giá trị ánh sáng thay đổi
     */
    public void startListening(OnLightSensorChangedCallback callback) {
        if (lightSensor == null) {
            return; // Thiết bị không hỗ trợ cảm biến ánh sáng
        }

        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (callback != null) {
                    float lux = event.values[0]; // Giá trị ánh sáng hiện tại
                    callback.onLightSensorChanged(lux);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Không cần xử lý trong trường hợp này
            }
        };

        sensorManager.registerListener(listener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Dừng lắng nghe cảm biến ánh sáng.
     */
    public void stopListening() {
        if (listener != null) {
            sensorManager.unregisterListener(listener);
            listener = null;
        }
    }

    /**
     * Interface callback cho việc xử lý ánh sáng.
     */
    public interface OnLightSensorChangedCallback {
        void onLightSensorChanged(float lux);
    }

}
