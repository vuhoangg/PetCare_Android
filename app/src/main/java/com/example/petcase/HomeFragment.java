package com.example.petcase;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.petcase.Adapter.HealthRecordAdapter;
import com.example.petcase.Adapter.ReminderAdapter;
import com.example.petcase.Domain.HealthRecord;
import com.example.petcase.Domain.Reminders;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReminderAdapter reminderAdapter;
    private List<Reminders> reminderList;
    private List<HealthRecord> healthRecordList= new ArrayList<>();
    private ImageView petAvatar;
    private TextView petName, petAge;

    public HomeFragment() {
        // Constructor rỗng cần thiết cho Fragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout cho fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo RecyclerView và Adapter
        initRecyclerView(view);

        // Liên kết các View cho pet
        petAvatar = view.findViewById(R.id.petAvatar);
        petName = view.findViewById(R.id.petName);
        petAge = view.findViewById(R.id.petAge);

        // Kiểm tra và nhận dữ liệu từ Bundle
        handleBundleData();

        // Lấy dữ liệu từ Firebase và cập nhật thông tin Pet và Reminder
        fetchPetData();
        fetchReminderData();
        fetchHealthRecordData();

        // Set OnClickListeners for CardViews
        setupCardViewListeners(view);
        return view;
    }

    // Khởi tạo RecyclerView và Adapter
    private void initRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewReminders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reminderList = new ArrayList<>();
        reminderAdapter = new ReminderAdapter(reminderList, this);
        recyclerView.setAdapter(reminderAdapter);
    }

    private void showReminders() {
        // Hiển thị danh sách Reminders
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(reminderAdapter);
        reminderAdapter.notifyDataSetChanged();
    }

    private void showHealthRecords() {
        // Hiển thị danh sách HealthRecords
        recyclerView.setVisibility(View.VISIBLE);
        HealthRecordAdapter healthRecordAdapter = new HealthRecordAdapter(healthRecordList, this);
        recyclerView.setAdapter(healthRecordAdapter);
        healthRecordAdapter.notifyDataSetChanged();
    }
    // Xử lý dữ liệu nhận từ Bundle
    private void handleBundleData() {
        if (getArguments() != null) {
            String petId = getArguments().getString("petId", "1732174110044"); // Cung cấp giá trị mặc định nếu không có petId
            String petNameFromBundle = getArguments().getString("petName", "Unknown");
            String petAvatarFromBundle = getArguments().getString("petAvatar", "");

            // Lưu dữ liệu vào SharedPreferences
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("PetData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Log.d(TAG, "Pet ID: " + petId);
            Log.d(TAG, "Pet Name: " + petNameFromBundle);
            Log.d(TAG, "Pet Avatar: " + petAvatarFromBundle);
        } else {
            Log.d(TAG, "No data received from PetFragment");
        }
    }

    // Lấy dữ liệu Reminder từ Firebase
    private void fetchReminderData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Reminders");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reminderList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Reminders reminder = dataSnapshot.getValue(Reminders.class);
                    if (reminder != null) {
                        reminderList.add(reminder);
                    }
                }
                reminderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Lấy dữ liệu HealthRecord từ Firebase
    private void fetchHealthRecordData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("HealthRecord");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                healthRecordList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    HealthRecord healthRecord = dataSnapshot.getValue(HealthRecord.class);
                    if (healthRecord != null) {
                        healthRecordList.add(healthRecord);
                    }
                }
                // Nếu đang hiển thị HealthRecords, thông báo Adapter mới
                if (recyclerView.getAdapter() instanceof HealthRecordAdapter) {
                    ((HealthRecordAdapter) recyclerView.getAdapter()).notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Set up OnClickListeners for CardViews
    private void setupCardViewListeners(View view) {
        view.findViewById(R.id.calendarButton).setOnClickListener(v -> {
            Log.d(TAG, "Calendar Button Clicked");
            showReminders();
        });

        view.findViewById(R.id.notesButton).setOnClickListener(v -> {
            Log.d(TAG, "Notes Button Clicked");
            showHealthRecords();
        });

        view.findViewById(R.id.HealthRecordButton).setOnClickListener(v -> {
            Log.d(TAG, "Health Record Button Clicked");
            showHealthRecords();
        });
    }
    // Lấy dữ liệu Pet từ Firebase
    private void fetchPetData() {
        // Lấy petId từ Bundle
        String petId = getArguments() != null ? getArguments().getString("petId") : "1732174110044";  // Kiểm tra petId trước khi sử dụng

        if (petId != null) {
            DatabaseReference petRef = FirebaseDatabase.getInstance().getReference("Pet").child(petId);
            petRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Lấy dữ liệu pet từ snapshot
                        String name = snapshot.child("name").getValue(String.class);
                        String age = snapshot.child("birth").getValue(String.class);
                        String avatarUrl = snapshot.child("imageUrl").getValue(String.class);

                        // Hiển thị thông tin pet
                        petName.setText(name != null ? name : "Unknown");
                        petAge.setText(age != null ? age : "Unknown age");

                        // Sử dụng Glide để tải ảnh
                        Glide.with(requireContext())
                                .load(avatarUrl != null && !avatarUrl.isEmpty() ? avatarUrl : R.drawable.corgi01)
                                .placeholder(R.drawable.corgi01)
                                .error(R.drawable.corgi01)
                                .into(petAvatar);
                    } else {
                        Toast.makeText(getContext(), "Pet data not found!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Error fetching pet data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Pet ID is null", Toast.LENGTH_SHORT).show();
        }

    }
}
