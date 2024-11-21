package com.example.petcase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcase.Domain.Pet;
import com.example.petcase.Domain.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditPetActivity extends AppCompatActivity {

    private Button btnEditPet, btnGoback;
    private EditText edtPetName, edtPetBirth, edtColor, edtimageUrl, edtSex, edtWeight, edtNote, edtuserId_FK;
    private String petId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_pet);

        // Ánh xạ các View
        edtPetName = findViewById(R.id.edtPetName);
        edtPetBirth = findViewById(R.id.edtPetBirth);
        edtColor = findViewById(R.id.edtColor);
        edtimageUrl = findViewById(R.id.edtimageUrl);
        edtSex = findViewById(R.id.edtSex);
        edtWeight = findViewById(R.id.edtWeight);
        edtNote = findViewById(R.id.edtNote);
        edtuserId_FK = findViewById(R.id.edtuserId_FK);

        // Ánh xạ nút
        btnEditPet = findViewById(R.id.btnEditPet); // <--- Thêm dòng này
        btnGoback = findViewById(R.id.btnGoback);

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        petId = intent.getStringExtra("PET_ID");
        edtPetName.setText(intent.getStringExtra("PET_NAME"));
        edtColor.setText(intent.getStringExtra("PET_COLOR"));
        edtimageUrl.setText(intent.getStringExtra("PET_IMG"));
        edtPetBirth.setText(intent.getStringExtra("PET_BIRTH"));
        edtSex.setText(intent.getStringExtra("PET_SEX"));
//        edtWeight.setText(intent.getStringExtra("PET_WEIGHT"));

        // Kiểm tra xem giá trị có tồn tại trong Intent hay không
        String petWeight = intent.getStringExtra("PET_WEIGHT");
        if (petWeight != null) {
            edtWeight.setText(petWeight);
        }
        edtNote.setText(intent.getStringExtra("PET_NOTE"));
        edtuserId_FK.setText(intent.getStringExtra("PET_USERID"));




        // Thiết lập sự kiện lưu thông tin khi người dùng nhấn nút "Save"
        btnEditPet.setOnClickListener(v -> {
            // Lấy dữ liệu người dùng từ EditText
//            String PetName = edtPetName.getText().toString();
//            String Color = edtColor.getText().toString();
//            String imageUrl =  edtimageUrl.getText().toString();
//            String PetBirth = edtPetBirth.getText().toString();
//            String Sex = edtSex.getText().toString();
//            String Note = edtNote.getText().toString();
//            String userId_FK = edtuserId_FK.getText().toString();

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
                    Toast.makeText(EditPetActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Xử lý khi người dùng không nhập giá trị
                Toast.makeText(EditPetActivity.this, "Weight cannot be empty", Toast.LENGTH_SHORT).show();
            }

            String Note = edtNote.getText().toString();
            String userId_FK = edtuserId_FK.getText().toString();

            // Cập nhật dữ liệu vào Firebase
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Pet").child(petId);

            Pet updatePet = new Pet(petId, petName, petBirth, Sex, imageUrl, Weight, Color, Note, userId_FK);

            myRef.setValue(updatePet).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(EditPetActivity.this, "Pet updated successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại màn hình trước đó
                } else {
                    Toast.makeText(EditPetActivity.this, "Failed to update pet", Toast.LENGTH_SHORT).show();
                }
            });
        });
        // quay lại
        btnGoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditPetActivity.this, "Go back", Toast.LENGTH_SHORT).show();

                // Trả về kết quả và đóng Activity
                setResult(RESULT_OK, new Intent());
                finish();
            }
        });

    }
}