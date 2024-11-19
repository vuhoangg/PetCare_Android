package com.example.petcase;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.petcase.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo EdgeToEdge (chỉ hỗ trợ từ Android 13 trở lên)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            EdgeToEdge.enable(this);
        }

        // Khởi tạo binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Hiển thị fragment mặc định khi khởi tạo
        replaceFragment(new HomeFragment());

        // Xử lý sự kiện cho BottomNavigationView
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();// Cấu trúc switch chính xác
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
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}
