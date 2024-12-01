package com.example.petcase;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petcase.Domain.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText etEmail, etPassword, etName, etPhone, etAddress;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private DataSnapshot databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ view
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);

        btnRegister = findViewById(R.id.btnRegister);

        // Xử lý sự kiện đăng ký
        btnRegister.setOnClickListener(v -> registerUser());
    }


    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        // Kiểm tra nhập liệu
        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Đăng ký tài khoản trong Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Lấy User ID từ Firebase Authentication
                        String userId = mAuth.getCurrentUser().getUid();

                        // Kết nối tới Firebase Realtime Database
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference userRef = database.getReference("User");

                        // Tạo đối tượng User để lưu
                        User newUser = new User(userId, name, email, phone, address, password);

                        // Lưu thông tin người dùng vào Realtime Database
                        userRef.child(userId).setValue(newUser)
                                .addOnCompleteListener(saveTask -> {
                                    if (saveTask.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công và dữ liệu đã được lưu", Toast.LENGTH_SHORT).show();
                                        finish(); // Quay lại màn hình đăng nhập
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Lưu dữ liệu thất bại: " + saveTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(RegisterActivity.this, "Lỗi khi lưu dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    } else {
                        // Đăng ký thất bại
                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
