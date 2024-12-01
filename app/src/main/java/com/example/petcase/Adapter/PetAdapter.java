package com.example.petcase.Adapter;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petcase.AddHealthRecord;
import com.example.petcase.Domain.Pet;
import com.example.petcase.EditPetActivity;
import com.example.petcase.PetFragment;
import com.example.petcase.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {
    private List<Pet> petList;

    public PetAdapter(List<Pet> petList, PetFragment PetFragment) {
        this.petList = petList;

    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pet_item, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = petList.get(position);
        Glide.with(holder.newPetimg.getContext())
                .load(pet.getImageUrl())
                .placeholder(R.drawable.corgi01) // Ảnh tạm khi đang tải
                .error(R.drawable.baseline_pets_24) // Ảnh lỗi nếu không tải được
                .into(holder.newPetimg);
        holder.newPetname.setText(pet.getName());

        holder.newPetimg.setOnClickListener(v -> {
            // Create an intent to open AddHealthRecordActivity
            Intent intent = new Intent(v.getContext(), AddHealthRecord.class);

            // Pass the pet data to the new activity
            intent.putExtra("petId", pet.getPetId());
            // Mở HomeActivity
            v.getContext().startActivity(intent);
        });


        // Thiết lập sự kiện click cho nút Sửa
        holder.btnPetUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditPetActivity.class);
            intent.putExtra("PET_ID", pet.getPetId());
            intent.putExtra("PET_NAME", pet.getName());
            intent.putExtra("PET_COLOR", pet.getColor());
            intent.putExtra("PET_IMG", pet.getImageUrl());
            intent.putExtra("PET_BIRTH", pet.getBirth());
            intent.putExtra("PET_SEX", pet.getSex());
            intent.putExtra("PET_WEIGHT", pet.getWeight());
            intent.putExtra("PET_NOTE", pet.getNote());
            intent.putExtra("PET_USERID", pet.getUserId_FK());
            v.getContext().startActivity(intent);
        });

        // Thiết lập sự kiện click cho nút Xóa
        holder.btnPetDelete.setOnClickListener(v -> {
            if (pet.getPetId() == null) {
                Toast.makeText(v.getContext(), "Pet ID is null", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Pet").child(pet.getPetId());

            myRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(v.getContext(), "Pet deleted successfully", Toast.LENGTH_SHORT).show();
                    // Kiểm tra `position` có hợp lệ không trước khi xóa
                    if (position >= 0 && position < petList.size()) {
                        petList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, petList.size());  // Cập nhật lại vị trí
                    }
                } else {
                    Exception e = task.getException();
                    if (e != null) {
                        Log.e("Firebase", "Error deleting Pet", e);
                    }
                    Toast.makeText(v.getContext(), "Failed to delete Pet", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(v.getContext(), "Firebase operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
        });


    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public static class PetViewHolder extends RecyclerView.ViewHolder {
        TextView newPetname;
        ImageView newPetimg;
        Button  btnPetDelete, btnPetUpdate;


        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            newPetimg = itemView.findViewById(R.id.new_img);
            newPetname = itemView.findViewById(R.id.new_product_name);
            btnPetUpdate = itemView.findViewById(R.id.btnPetUpdate);
            btnPetDelete = itemView.findViewById(R.id.btnPetDelete);
        }
    }
}

