package com.example.petcase;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LightSensorActivity extends AppCompatActivity {
    private LightSensorHandler lightSensorHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_sensor);

        lightSensorHandler = new LightSensorHandler(this);
        lightSensorHandler.startListening(lux -> {
            // Chuyển giá trị ánh sáng sang trang tiếp theo
            Intent intent = new Intent(LightSensorActivity.this, InputDataSensor.class);
            intent.putExtra("light_value", lux);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lightSensorHandler.stopListening();
    }
}