package com.example.petcase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ các View
        initViews();

        // Xử lý sự kiện
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
    }

    private void setupListeners() {
        // Xử lý sự kiện nút đăng nhập
        btnLogin.setOnClickListener(v -> loginUser());

        // Xử lý sự kiện chuyển sang màn hình đăng ký
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Kiểm tra nhập liệu
        if (!validateInput(email, password)) {
            return;
        }

        // Đăng nhập với Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Đăng nhập thành công
                        FirebaseUser user = mAuth.getCurrentUser();
                        handleSuccessfulLogin(user);
                    } else {
                        // Đăng nhập thất bại
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ email và mật khẩu", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void handleSuccessfulLogin(FirebaseUser user) {
        if (user != null) {
            // Lấy thông tin USER_ID từ FirebaseUser
            String newUserId = user.getUid();

            // Lấy thông tin SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("Pet", Context.MODE_PRIVATE);
            String currentUserId = sharedPreferences.getString("USER_ID", null);

            // Kiểm tra nếu tài khoản mới khác tài khoản hiện tại
            if (currentUserId != null && !currentUserId.equals(newUserId)) {
                // Xóa dữ liệu tài khoản cũ
                sharedPreferences.edit().clear().apply();
            }

            // Lưu thông tin tài khoản mới
            sharedPreferences.edit()
                    .putString("USER_ID", newUserId)
                    .apply();

            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

            // Chuyển sang MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("USER_ID", newUserId);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
        }
    }

}
