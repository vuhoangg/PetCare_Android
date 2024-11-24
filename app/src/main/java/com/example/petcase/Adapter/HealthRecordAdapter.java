package com.example.petcase.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcase.Domain.HealthRecord;
import com.example.petcase.HomeFragment;
import com.example.petcase.R;

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

        // Kiểm tra null trước khi hiển thị
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
    }

    @Override
    public int getItemCount() {
        return healthRecordList.size();
    }

    public static class HealthRecordViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate, txtDescription, txtVaccine;

        public HealthRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtVaccine = itemView.findViewById(R.id.txtVaccine);
        }
    }

    // Phương thức cập nhật danh sách HealthRecord
    public void updateHealthRecordList(List<HealthRecord> newHealthRecordList) {
        this.healthRecordList = newHealthRecordList;
        notifyDataSetChanged();  // Cập nhật giao diện sau khi thay đổi dữ liệu
    }
}
