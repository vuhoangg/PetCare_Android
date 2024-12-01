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
import com.example.petcase.EditReminderActivity;
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


        // Thiết lập sự kiện click cho nút Sửa
        holder.ImgReminder.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditReminderActivity.class);
            intent.putExtra("REMINDER_ID", reminder.getReminderId());
            intent.putExtra("PET_ID", reminder.getPetId());
            intent.putExtra("REMINDER_DATE", reminder.getDate());
            intent.putExtra("REMINDER_TIME", reminder.getTime());
            intent.putExtra("REMINDER_MESSAGE", reminder.getMessage());
            v.getContext().startActivity(intent);
        });



        // Thiết lập sự kiện click cho nút Xóa
        holder.btnDeleteReminder.setOnClickListener(v -> {
            if (reminder.getReminderId() == null) {
                Toast.makeText(v.getContext(), "User ID is null", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Reminders").child(reminder.getReminderId());

            myRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(v.getContext(), "reminderList deleted successfully", Toast.LENGTH_SHORT).show();
                    // Kiểm tra `position` có hợp lệ không trước khi xóa
                    if (position >= 0 && position < reminderList.size()) {
                        reminderList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, reminderList.size());  // Cập nhật lại vị trí
                    }
                } else {
                    Exception e = task.getException();
                    if (e != null) {
                        Log.e("Firebase", "Error deleting reminderList", e);
                    }
                    Toast.makeText(v.getContext(), "Failed to delete reminderList", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(v.getContext(), "Firebase operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
        });


    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView edtMasagerReminder, edtDateReminder;
        ImageView ImgReminder;
        Button btnDeleteReminder;



        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            edtMasagerReminder = itemView.findViewById(R.id.edtMasagerReminder);
            edtDateReminder = itemView.findViewById(R.id.edtDateReminder);
            ImgReminder = itemView.findViewById(R.id.ImgReminder);
            btnDeleteReminder = itemView.findViewById(R.id.btnDeleteReminder);


        }
    }
}


