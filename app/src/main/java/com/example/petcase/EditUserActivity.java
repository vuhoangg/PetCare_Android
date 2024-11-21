package com.example.petcase;

// EditUserActivity.java
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petcase.Domain.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditUserActivity extends AppCompatActivity {

    private EditText edtUserName, edtEmail, edtPhone, edtAddresss;
    private Button btnSaveUser;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        // Ánh xạ các View
        edtUserName = findViewById(R.id.edtPetName);
        edtEmail = findViewById(R.id.edtBirthPet);
        edtPhone = findViewById(R.id.edtColor);
        edtAddresss = findViewById(R.id.edtAddresss);
        btnSaveUser = findViewById(R.id.btnSaveUser);

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");
        edtUserName.setText(intent.getStringExtra("USER_NAME"));
        edtEmail.setText(intent.getStringExtra("USER_EMAIL"));
        edtPhone.setText(intent.getStringExtra("USER_PHONE"));
        edtAddresss.setText(intent.getStringExtra("USER_ADDRESS"));

        // Thiết lập sự kiện lưu thông tin khi người dùng nhấn nút "Save"
        btnSaveUser.setOnClickListener(v -> {
            // Lấy dữ liệu người dùng từ EditText
            String userName = edtUserName.getText().toString();
            String userEmail = edtEmail.getText().toString();
            String userPhone = edtPhone.getText().toString();
            String userAddress = edtAddresss.getText().toString();

            // Cập nhật dữ liệu vào Firebase
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("User").child(userId);

            User updatedUser = new User(userId, userName, userEmail, userPhone, userAddress);

            myRef.setValue(updatedUser).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(EditUserActivity.this, "User updated successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại màn hình trước đó
                } else {
                    Toast.makeText(EditUserActivity.this, "Failed to update user", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
