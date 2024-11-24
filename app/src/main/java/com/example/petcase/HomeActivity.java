package com.example.petcase;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // Layout chứa HomeFragment

        // Khởi tạo Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Thiết lập BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId(); // Lấy ID của mục được chọn

            if (itemId == R.id.Home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.Pet) {
                selectedFragment = new PetFragment();
            } else if (itemId == R.id.Profile) {
                selectedFragment = new ProfileFragment();
            } else if (itemId == R.id.Setting) {
                selectedFragment = new SettingFragment();
            }

            // Thay thế fragment hiện tại bằng fragment đã chọn
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, selectedFragment)
                        .commit();
            }

            return true;
        });
    }
}
