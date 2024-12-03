package com.example.petcase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.petcase.databinding.ActivityMainBinding;

/**
 * MainActivity của ứng dụng, quản lý giao diện chính và cảm biến ánh sáng.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private LightSensorHandler lightSensorHandler;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra xem có User ID không
        String userId = getIntent().getStringExtra("USER_ID");
        if (userId == null || userId.isEmpty()) {
            navigateToLogin();
            return;
        }

        // Áp dụng EdgeToEdge (Android 13 trở lên)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            EdgeToEdge.enable(this);
        }

        // Khởi tạo binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Hiển thị fragment mặc định khi khởi tạo
        replaceFragment(new HomeFragment());

        // Xử lý sự kiện cho BottomNavigationView
        // Xử lý sự kiện cho BottomNavigationView
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Cấu trúc switch chính xác
            if (itemId == R.id.Home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.Pet) {
                replaceFragment(new PetFragment());
            } else if (itemId == R.id.Profile) {
                replaceFragment(new ProfileFragment());
            } else if (itemId == R.id.Setting) {
                replaceFragment(new SettingFragment());
            } else {
                return false;
            }
            return true;
        });

        // Áp dụng padding cho systemBars
        ViewCompat.setOnApplyWindowInsetsListener(binding.frameLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo và bắt đầu LightSensorHandler
        lightSensorHandler = new LightSensorHandler(this);
        lightSensorHandler.startListening(this::adjustScreenBrightness);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Dừng LightSensorHandler
        if (lightSensorHandler != null) {
            lightSensorHandler.stopListening();
        }
    }

    /**
     * Thay thế fragment hiện tại bằng fragment mới.
     *
     * @param fragment Fragment cần hiển thị
     */
    private void replaceFragment(Fragment fragment) {
        String userId = getIntent().getStringExtra("USER_ID");
        if (userId != null) {
            Bundle bundle = new Bundle();
            bundle.putString("USER_ID", userId);
            fragment.setArguments(bundle);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();
    }

    /**
     * Điều chỉnh độ sáng màn hình dựa trên giá trị ánh sáng.
     *
     * @param lux Giá trị ánh sáng nhận được từ cảm biến
     */
    private void adjustScreenBrightness(float lux) {
        // Kiểm tra và yêu cầu quyền WRITE_SETTINGS nếu cần
        if (!Settings.System.canWrite(this)) {
            checkAndRequestWriteSettingsPermission();
            return;
        }

        int brightness = (int) (lux / 10);
        brightness = Math.max(10, Math.min(brightness, 255));

        Log.d("LightSensor", "Lux: " + lux + ", Brightness: " + brightness);
        Toast.makeText(this, "Lux: " + lux + " -> Brightness: " + brightness, Toast.LENGTH_SHORT).show();

        Settings.System.putInt(
                getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS,
                brightness
        );
    }

    private void checkAndRequestWriteSettingsPermission() {
        if (!Settings.System.canWrite(this)) {
            // Hiển thị thông báo yêu cầu cấp quyền
            Toast.makeText(this, "Ứng dụng cần quyền để thay đổi cài đặt hệ thống.", Toast.LENGTH_LONG).show();

            // Chuyển đến màn hình cài đặt quyền
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    /**
     * Điều hướng tới màn hình đăng nhập.
     */
    private void navigateToLogin() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
