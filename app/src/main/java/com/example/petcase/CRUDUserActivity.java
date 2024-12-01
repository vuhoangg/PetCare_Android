package com.example.petcase;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcase.Adapter.UserAdapter;
import com.example.petcase.Domain.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CRUDUserActivity extends AppCompatActivity {

    // Khai báo biến Button
    private Button AddUser;
    private EditText edtUserName;
    private EditText edtEmail;
    private EditText edtPhone;
    private EditText edtAdress;
    private EditText edtPassword;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cruduser);


        // Ánh xạ Button từ XML bằng id
        AddUser = findViewById(R.id.AddUser);
        edtUserName = findViewById(R.id.edtPetName);
        edtEmail = findViewById(R.id.edtBirthPet);
        edtPhone = findViewById(R.id.edtColor);
        edtAdress = findViewById(R.id.edtimageUrl);
        edtPassword = findViewById(R.id.edtPassword);

        // Đặt sự kiện onClick cho Button
        AddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Lấy dữ liệu từ các EditText
                String userName = edtUserName.getText().toString();
                String userEmail = edtEmail.getText().toString();
                String userPhone = edtPhone.getText().toString();
                String userAddress = edtAdress.getText().toString();
                String userPassword = edtPassword.getText().toString();

                // Tạo kết nối đến Firebase
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("User");

                // Tạo một bản ghi mới với ID tự động
                String userId = myRef.push().getKey();

                User newUser = new User(userId, userName, userEmail, userPhone, userAddress, userPassword);
//                Toast.makeText(CRUDUserActivity.this, newUser.userName.toString() , Toast.LENGTH_SHORT).show();
                // Lưu dữ liệu vào Firebases
                if (userId != null) {
                    myRef.child(userId).setValue(newUser)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(CRUDUserActivity.this, "User added successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CRUDUserActivity.this, "Failed to add user", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(CRUDUserActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                }

            }
        });

        ///
        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this);
        recyclerView.setAdapter(userAdapter);

        // Lấy dữ liệu từ Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        userList.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CRUDUserActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
