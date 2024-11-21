package com.example.petcase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcase.Domain.Pet;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddPetActivity extends AppCompatActivity {

    private Button btnSavePet, btnGoback;
    private EditText edtPetName, edtPetBirth, edtColor, edtimageUrl, edtSex, edtWeight, edtNote, edtuserId_FK;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_pet);

        // Ánh xạ Button từ XML bằng id
        btnSavePet = findViewById(R.id.btnEditPet);
        btnGoback = findViewById(R.id.btnGoback);
        edtPetName = findViewById(R.id.edtPetName);
        edtPetBirth = findViewById(R.id.edtPetBirth);
        edtColor = findViewById(R.id.edtColor);
        edtimageUrl = findViewById(R.id.edtimageUrl);
        edtSex = findViewById(R.id.edtSex);
        edtWeight = findViewById(R.id.edtWeight);
        edtNote = findViewById(R.id.edtNote);
        edtuserId_FK = findViewById(R.id.edtuserId_FK);


        // Đặt sự kiện onClick cho Button
        btnSavePet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Lấy dữ liệu từ các EditText
                String petName = edtPetName.getText().toString();
                String petBirth = edtPetBirth.getText().toString();
                String Color = edtColor.getText().toString();
                String imageUrl = edtimageUrl.getText().toString();
                String Sex = edtSex.getText().toString();
                String weightString = edtWeight.getText().toString(); // Lấy giá trị từ EditText

                Double Weight = null;
                if (!weightString.isEmpty()) {
                    try {
                        // Ép kiểu từ String sang Double (trả về đối tượng Double)
                        Weight = Double.valueOf(weightString);
                        // Tiến hành sử dụng giá trị weight
                    } catch (NumberFormatException e) {
                        // Nếu giá trị không hợp lệ, xử lý ngoại lệ
                        Toast.makeText(AddPetActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Xử lý khi người dùng không nhập giá trị
                    Toast.makeText(AddPetActivity.this, "Weight cannot be empty", Toast.LENGTH_SHORT).show();
                }

                String Note = edtNote.getText().toString();
                String userId_FK = edtuserId_FK.getText().toString();

                // Tạo kết nối đến Firebase
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Pet");

//
                String petId = String.valueOf(System.currentTimeMillis());

//                Pet newPet = new Pet(petId, "Haha" , "petBirth" , "Sex1", "imageUrl", 45, "Color", "Note" ,"userId_FK ");
                Pet newPet = new Pet(petId, petName, petBirth, Sex, imageUrl, Weight, Color, Note, userId_FK);
//                Toast.makeText(CRUDUserActivity.this, newUser.userName.toString() , Toast.LENGTH_SHORT).show();
                // Lưu dữ liệu vào Firebases
                if (petId != null) {
                    myRef.child(petId).setValue(newPet)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AddPetActivity.this, "User added successfully!", Toast.LENGTH_SHORT).show();
                                    // Trả về kết quả và đóng Activity
                                    setResult(RESULT_OK, new Intent());
                                    finish();
                                } else {
                                    Toast.makeText(AddPetActivity.this, "Failed to add user", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(AddPetActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                }

            }
        });
        btnGoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddPetActivity.this, "Go back", Toast.LENGTH_SHORT).show();

                // Trả về kết quả và đóng Activity
                setResult(RESULT_OK, new Intent());
                finish();
            }
        });




    }
}