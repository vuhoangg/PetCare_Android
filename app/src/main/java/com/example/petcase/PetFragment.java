package com.example.petcase;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.petcase.Adapter.PetAdapter;
import com.example.petcase.Domain.Pet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PetFragment extends Fragment {

    private RecyclerView recyclerView;
    private PetAdapter petAdapter;
    private List<Pet> petList;
    private Button addPetButton;

    public PetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pet, container, false);

        // Khởi tạo RecyclerView và thiết lập Adapter
        recyclerView = view.findViewById(R.id.recyclerViewPet);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        petList = new ArrayList<>();
        petAdapter = new PetAdapter(petList, this); // Sửa lại tên Adapter thành PetAdapter
        recyclerView.setAdapter(petAdapter);


        // Ánh xạ button AddPet
        addPetButton = view.findViewById(R.id.AddPet);
        addPetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo Intent để mở AddPetActivity
                Intent intent = new Intent(getActivity(), AddPetActivity.class);
                // Sử dụng startActivityForResult để nhận kết quả
                startActivityForResult(intent, 1);
            }
        });

        // Lấy dữ liệu từ Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Pet"); // Đảm bảo tham chiếu đúng
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                petList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Pet pet = dataSnapshot.getValue(Pet.class);
                    if (pet != null) {
                        // Kiểm tra và đảm bảo rằng các thuộc tính quan trọng không null
                        if (pet.getName() != null && pet.getImageUrl() != null && pet.getBirth() != null) {
                            petList.add(pet);
                        } else {
                            Log.e("PetData", "Dữ liệu không hợp lệ: " + pet.toString());
                        }
                    }
                }
                petAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view; // Trả về view sau khi đã thiết lập các thành phần
    }
}
