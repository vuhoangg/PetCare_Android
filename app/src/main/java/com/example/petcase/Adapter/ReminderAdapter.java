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
import com.example.petcase.Domain.Pet;
import com.example.petcase.Domain.Reminders;
import com.example.petcase.EditPetActivity;
import com.example.petcase.HomeFragment;
import com.example.petcase.PetFragment;
import com.example.petcase.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;




public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder > {
    private List<Reminders> reminderList;

    public ReminderAdapter(List<Reminders> remiderList, HomeFragment HomeFragment) {
        this.reminderList = remiderList;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_item, parent, false);
        return new ReminderViewHolder (view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder  holder, int position) {
        Reminders reminder = reminderList.get(position);

        holder.edtMasagerReminder.setText(reminder.getMessage());
        holder.edtDateReminder.setText(reminder.getDate());


    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView edtMasagerReminder, edtDateReminder;



        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            edtMasagerReminder = itemView.findViewById(R.id.edtMasagerReminder);
            edtDateReminder = itemView.findViewById(R.id.edtDateReminder);
        }
    }
}


