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

import com.example.petcase.Domain.HealthRecord;
import com.example.petcase.EditHealthRecordActivity;
import com.example.petcase.EditReminderActivity;
import com.example.petcase.HomeFragment;
import com.example.petcase.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class HealthRecordAdapter extends RecyclerView.Adapter<HealthRecordAdapter.HealthRecordViewHolder> {
    private List<HealthRecord> healthRecordList;

    public HealthRecordAdapter(List<HealthRecord> healthRecordList, HomeFragment homeFragment) {
        this.healthRecordList = healthRecordList;
    }

    @NonNull
    @Override
    public HealthRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.health_record_item, parent, false);
        return new HealthRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HealthRecordViewHolder holder, int position) {
        HealthRecord healthRecord = healthRecordList.get(position);



        if (healthRecord.getDate() != null) {
            holder.txtDate.setText(healthRecord.getDate());
        } else {
            holder.txtDate.setText("N/A"); // Hiển thị giá trị mặc định nếu null
        }

        if (healthRecord.getDescription() != null) {
            holder.txtDescription.setText(healthRecord.getDescription());
        } else {
            holder.txtDescription.setText("No description"); // Giá trị mặc định
        }

        if (healthRecord.getVaccine() != null) {
            holder.txtVaccine.setText(healthRecord.getVaccine());
        } else {
            holder.txtVaccine.setText("No vaccine info"); // Giá trị mặc định
        }

        // Thiết lập sự kiện click cho nút Sửa
        holder.ImgHealthRecord.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditHealthRecordActivity.class);

            intent.putExtra("HEALTHRECORD_ID",  healthRecord.getHealthRecordId());
            intent.putExtra("petId",  healthRecord.getPetId());
            intent.putExtra("HEALTHRECORD_DATE",  healthRecord.getDate());
            intent.putExtra("HEALTHRECORD_DESCRIPTION",  healthRecord.getDescription());
            intent.putExtra("HEALTHRECORD_VACCINE",  healthRecord.getVaccine());
            v.getContext().startActivity(intent);
        });


        // Thiết lập sự kiện click cho nút Xóa
        holder.btnDeleteHealthRecord.setOnClickListener(v -> {
            if (healthRecord.getHealthRecordId() == null) {
                Toast.makeText(v.getContext(), "User ID is null", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("HealthRecord").child(healthRecord.getHealthRecordId());

            myRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(v.getContext(), "healthRecord deleted successfully", Toast.LENGTH_SHORT).show();
                    // Kiểm tra `position` có hợp lệ không trước khi xóa
                    if (position >= 0 && position < healthRecordList.size()) {
                        healthRecordList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, healthRecordList.size());  // Cập nhật lại vị trí
                    }
                } else {
                    Exception e = task.getException();
                    if (e != null) {
                        Log.e("Firebase", "Error deleting healthRecord", e);
                    }
                    Toast.makeText(v.getContext(), "Failed to delete healthRecord", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(v.getContext(), "Firebase operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
        });
    }




    @Override
    public int getItemCount() {
        return healthRecordList.size();
    }

    public static class HealthRecordViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate, txtDescription, txtVaccine;
        ImageView ImgHealthRecord;
        Button btnDeleteHealthRecord;

        public HealthRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtVaccine = itemView.findViewById(R.id.txtVaccine);
            ImgHealthRecord = itemView.findViewById(R.id.ImgHealthRecord);
            btnDeleteHealthRecord = itemView.findViewById(R.id.btnDeleteHealthRecord);

        }
    }

    // Phương thức cập nhật danh sách HealthRecord
    public void updateHealthRecordList(List<HealthRecord> newHealthRecordList) {
        this.healthRecordList = newHealthRecordList;
        notifyDataSetChanged();  // Cập nhật giao diện sau khi thay đổi dữ liệu
    }
}
