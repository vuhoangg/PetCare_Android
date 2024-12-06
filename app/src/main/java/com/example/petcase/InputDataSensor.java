package com.example.petcase;

import com.example.petcase.LightSensorHandler;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.CheckBox;
import android.provider.Settings;
import android.content.Intent;


public class InputDataSensor extends AppCompatActivity {

    private EditText editLightValue;
    private TextView textSavedData;
    private DatabaseReference firebaseDatabase;
    private LightSensorHandler lightSensorHandler;
    private CheckBox checkBoxSimulate;

    private boolean isSimulating = false; // Chế độ giả lập
    private float simulatedLightValue = -1; // Giá trị ánh sáng giả lập

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_data_sensor);

        editLightValue = findViewById(R.id.edit_light_value);
        Button buttonSave = findViewById(R.id.button_save);
        textSavedData = findViewById(R.id.text_saved_data);
        checkBoxSimulate = findViewById(R.id.checkbox_simulate);

        // Lắng nghe thay đổi chế độ giả lập
        checkBoxSimulate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isSimulating = isChecked;
            if (isSimulating && simulatedLightValue != -1) {
                editLightValue.setText(String.valueOf(simulatedLightValue)); // Hiển thị giá trị mô phỏng
            }
        });

        // Khởi tạo cảm biến ánh sáng
        lightSensorHandler = new LightSensorHandler(this);
        lightSensorHandler.startListening(lux -> {
            if (!isSimulating) {
                editLightValue.setText(String.valueOf(lux)); // Giá trị thực từ cảm biến
                setScreenBrightness(lux / 10); // Điều chỉnh độ sáng (chuẩn hóa giá trị lux)
            }
        });

        // Kết nối Firebase
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("LightSensorData");

        buttonSave.setOnClickListener(v -> {
            String lightValueStr = editLightValue.getText().toString();
            if (!lightValueStr.isEmpty()) {
                float lightValue = Float.parseFloat(lightValueStr);
                saveDataToFirebase(lightValue);

                if (isSimulating) {
                    simulatedLightValue = lightValue; // Lưu giá trị ánh sáng giả lập
                    editLightValue.setText(String.valueOf(simulatedLightValue)); // Hiển thị giá trị mô phỏng
                    setScreenBrightness(simulatedLightValue / 10); // Điều chỉnh độ sáng
                }
            }
        });

        // Lấy dữ liệu từ Firebase
        fetchDataFromFirebase();
        requestWriteSettingsPermission();
    }

    private void saveDataToFirebase(float lightValue) {
        String id = firebaseDatabase.push().getKey();
        if (id != null) {
            firebaseDatabase.child(id).setValue(lightValue).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Data saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to save data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void fetchDataFromFirebase() {
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StringBuilder data = new StringBuilder();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Float value = childSnapshot.getValue(Float.class);
                    if (value != null) {
                        data.append(value).append("\n");
                    }
                }
                textSavedData.setText(data.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InputDataSensor.this, "Failed to fetch data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestWriteSettingsPermission() {
        if (!Settings.System.canWrite(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void setScreenBrightness(float brightness) {
        // Giá trị brightness phải nằm trong khoảng 0 đến 1
        if (Settings.System.canWrite(this)) {
            int brightnessValue = (int) (brightness * 255); // Chuyển đổi sang 0-255
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightnessValue);
        } else {
            Toast.makeText(this, "Chưa cấp quyền thay đổi độ sáng.", Toast.LENGTH_SHORT).show();
        }
    }


}