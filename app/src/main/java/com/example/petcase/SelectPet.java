package com.example.petcase;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcase.Adapter.SelectPetAdapter;
import com.example.petcase.Domain.Pet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SelectPet extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SelectPetAdapter selectPetAdapter;
    private List<Pet> petList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pet);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo adapter với callback
        selectPetAdapter = new SelectPetAdapter(petList, this::onPetSelected);
        recyclerView.setAdapter(selectPetAdapter);

        // Truy xuất dữ liệu từ Firebase
        fetchPetsFromFirebase();
    }

    private void fetchPetsFromFirebase() {
        DatabaseReference petRef = FirebaseDatabase.getInstance().getReference("Pet");
        petRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                petList.clear(); // Xóa danh sách cũ trước khi thêm mới
                for (DataSnapshot petSnapshot : snapshot.getChildren()) {
                    Pet pet = petSnapshot.getValue(Pet.class);
                    if (pet != null) {
                        pet.setPetId(petSnapshot.getKey()); // Lưu `petId`
                        petList.add(pet);
                    }
                }
                if (petList.isEmpty()) {
                    Toast.makeText(SelectPet.this, "Không có thú cưng để hiển thị.", Toast.LENGTH_SHORT).show();
                }
                selectPetAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SelectPet.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onPetSelected(Pet pet) {
        // Trả về dữ liệu khi người dùng chọn thú cưng
        Intent resultIntent = new Intent();
        resultIntent.putExtra("petId", pet.getPetId());
        resultIntent.putExtra("petName", pet.getName());
        resultIntent.putExtra("petAvatar", pet.getImageUrl());
        resultIntent.putExtra("birth", pet.getBirth());
        setResult(RESULT_OK, resultIntent);
        finish(); // Kết thúc activity sau khi chọn
    }
}
