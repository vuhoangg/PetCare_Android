package com.example.petcase.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petcase.Domain.Pet;
import com.example.petcase.R;

import java.util.List;

public class SelectPetAdapter extends RecyclerView.Adapter<SelectPetAdapter.PetViewHolder> {
    private List<Pet> petList;
    private OnPetSelectedListener listener;

    public interface OnPetSelectedListener {
        void onPetSelected(Pet pet);
    }

    public SelectPetAdapter(List<Pet> petList, OnPetSelectedListener listener) {
        this.petList = petList;
        this.listener = listener;
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

        Glide.with(holder.petImage.getContext())
                .load(pet.getImageUrl())
                .placeholder(R.drawable.corgi01)
                .error(R.drawable.baseline_pets_24)
                .into(holder.petImage);
        holder.petName.setText(pet.getName());

        // Click event
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPetSelected(pet);
            }
        });
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public static class PetViewHolder extends RecyclerView.ViewHolder {
        ImageView petImage;
        TextView petName;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            petImage = itemView.findViewById(R.id.new_img);
            petName = itemView.findViewById(R.id.new_product_name);
        }
    }
}
