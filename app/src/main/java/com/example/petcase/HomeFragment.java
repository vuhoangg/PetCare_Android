package com.example.petcase;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.Intent;
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
import com.example.petcase.Adapter.UserAdapter;
import com.example.petcase.Domain.HealthRecord;
import com.example.petcase.Domain.Reminders;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReminderAdapter reminderAdapter;
    private UserAdapter userAdapter;
    private List<Reminders> reminderList;
    private List<HealthRecord> healthRecordList = new ArrayList<>();
    private ImageView petAvatar;
    private TextView petName, petAge;
    private String userId, petId;

    private ValueEventListener healthRecordListener;
    private Query healthRecordQuery;

    public HomeFragment() {
        // Constructor rỗng cần thiết cho Fragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout cho fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Liên kết các View cho pet
        petAvatar = view.findViewById(R.id.petAvatar);
        petName = view.findViewById(R.id.petName);
        petAge = view.findViewById(R.id.petAge);

        // Khởi tạo RecyclerView và Adapter
        initRecyclerView(view);

        // Lấy dữ liệu từ SharedPreferences
//        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Pet", Context.MODE_PRIVATE);
//        String petId = sharedPreferences.getString("petId", null);
        userId = getArguments().getString("USER_ID");
        petId = getArguments().getString("petId");


        // Nếu đã có petId lưu trữ, thì lấy dữ liệu Pet, Reminder, HealthRecord
        if (petId != null && userId != null ) {
            fetchPetDataOn(petId , userId);
        } else {
            // Nếu chưa chọn pet, yêu cầu người dùng chọn
            petName.setText("Select a pet");
            petAge.setText("Unknown age");
            petAvatar.setImageResource(R.drawable.corgi01);
        }



        // Nhận userId từ arguments
        if (getArguments() != null) {
            userId = getArguments().getString("USER_ID");
        }

//         Hiển thị userId để kiểm tra
//        TextView userIdTextView = view.findViewById(R.id.userIdTextView);
//        userIdTextView.setText("User ID: " + userId);

        fetchUserData(userId);



        // Set OnClickListeners for CardViews
        setupCardViewListeners(view);
        return view;
    }

    private void initRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewReminders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reminderList = new ArrayList<>();
        reminderAdapter = new ReminderAdapter(reminderList, this);
        recyclerView.setAdapter(reminderAdapter);
    }

    private void showReminders() {
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(reminderAdapter);
        reminderAdapter.notifyDataSetChanged();
    }

    private void showUser() {
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(userAdapter);
        userAdapter.notifyDataSetChanged();
    }

    private void showHealthRecords() {
        recyclerView.setVisibility(View.VISIBLE);

        // Kiểm tra xem RecyclerView đã có adapter hay chưa
        if (recyclerView.getAdapter() instanceof HealthRecordAdapter) {
            // Nếu adapter đã tồn tại, chỉ cần thông báo cập nhật
            HealthRecordAdapter healthRecordAdapter = (HealthRecordAdapter) recyclerView.getAdapter();
            healthRecordAdapter.notifyDataSetChanged();
        } else {
            // Nếu adapter chưa tồn tại, khởi tạo adapter mới
            HealthRecordAdapter healthRecordAdapter = new HealthRecordAdapter(healthRecordList, this);
            recyclerView.setAdapter(healthRecordAdapter);
        }
    }
    // get Information Reminderdata by pet ID
    private void fetchReminderData(String petId) {
        if (petId == null) {
            Toast.makeText(getContext(), "Please select a pet to see reminders.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference reminderRef = FirebaseDatabase.getInstance().getReference("Reminders");

        reminderRef.orderByChild("petId").equalTo(petId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        reminderList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Reminders reminder = dataSnapshot.getValue(Reminders.class);
                            if (reminder != null) {
                                reminderList.add(reminder);
                            }
                        }

                        showReminders();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error fetching reminders: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // lấy thông tin HealthRecord dựa trên petId
    private void fetchHealthRecordData(String petId) {
        if (petId == null) {
            Toast.makeText(getContext(), "Please select a pet to see health records.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference healthRecordRef = FirebaseDatabase.getInstance()
                .getReference("HealthRecord");

        healthRecordRef.orderByChild("petId").equalTo(petId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        healthRecordList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            HealthRecord healthRecord = dataSnapshot.getValue(HealthRecord.class);
                            if (healthRecord != null) {
                                healthRecordList.add(healthRecord);
                            }

                        }

                        showHealthRecords();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error fetching health records: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

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
            Log.d(TAG, "User Button Clicked");
            showUser();
        });

        petAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), SelectPet.class);
            intent.putExtra("USER_ID", userId);
            // Kiểm tra xem userId có được truyền sang hay không

            Toast.makeText(getContext(), "User Name Id "+ userId, Toast.LENGTH_SHORT).show();
// Thêm userId vào Intent
            startActivityForResult(intent, 100);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == requireActivity().RESULT_OK && data != null) {
            // Lấy dữ liệu trả về
            String petId = data.getStringExtra("petId");
            String petNameStr = data.getStringExtra("petName");
            String petAvatarUrl = data.getStringExtra("petAvatar");
            String petBirth = data.getStringExtra("birth");

            petName.setText(petNameStr != null ? petNameStr : "Unknown");
            petAge.setText(petBirth != null ? petBirth : "Unknown age");

            Glide.with(requireContext())
                    .load(petAvatarUrl != null && !petAvatarUrl.isEmpty() ? petAvatarUrl : R.drawable.corgi01)
                    .placeholder(R.drawable.corgi01)
                    .error(R.drawable.corgi01)
                    .into(petAvatar);

            // Lưu petId vào SharedPreferences
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Pet", Context.MODE_PRIVATE);
            sharedPreferences.edit().putString("petId", petId).apply();

            // Gọi lại fetchPetData sau khi có petId
            fetchPetData(petId);
        }
    }



    // lấy thông tin Pet dựa vào Id

    private void fetchPetData(String petId) {
        if (petId == null) {
            Toast.makeText(getContext(), "Pet ID not found. Please select a pet.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference petRef = FirebaseDatabase.getInstance().getReference("Pet").child(petId);
        petRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String age = snapshot.child("birth").getValue(String.class);
                    String avatarUrl = snapshot.child("imageUrl").getValue(String.class);

                    petName.setText(name != null ? name : "Unknown");
                    petAge.setText(age != null ? age : "Unknown age");

                    Glide.with(requireContext())
                            .load(avatarUrl != null && !avatarUrl.isEmpty() ? avatarUrl : R.drawable.corgi01)
                            .placeholder(R.drawable.corgi01)
                            .error(R.drawable.corgi01)
                            .into(petAvatar);

                    fetchReminderData(petId);
                    fetchHealthRecordData(petId);
                } else {
                    Toast.makeText(getContext(), "Pet data not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error fetching pet data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    // lấy Pet Từ UserId và PetId
    private void fetchPetDataOn (String userId, String petId) {
        if (userId == null || petId == null) {
            Toast.makeText(getContext(), "User ID or Pet ID not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference petRef = FirebaseDatabase.getInstance().getReference("Pet");
        petRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean petFound = false;

                for (DataSnapshot petSnapshot : snapshot.getChildren()) {
                    String petUserId = petSnapshot.child("userId_FK").getValue(String.class);
                    String currentPetId = petSnapshot.child("petId").getValue(String.class);

                    if (userId.equals(petUserId) && petId.equals(currentPetId)) {
                        petFound = true;

                        String name = petSnapshot.child("name").getValue(String.class);
                        String age = petSnapshot.child("birth").getValue(String.class);
                        String avatarUrl = petSnapshot.child("imageUrl").getValue(String.class);

                        petName.setText(name != null ? name : "Unknown");
                        petAge.setText(age != null ? age : "Unknown age");

                        Glide.with(requireContext())
                                .load(avatarUrl != null && !avatarUrl.isEmpty() ? avatarUrl : R.drawable.corgi01)
                                .placeholder(R.drawable.corgi01)
                                .error(R.drawable.corgi01)
                                .into(petAvatar);

                        fetchReminderData(petId);
                        fetchHealthRecordData(petId);
                        break; // Thoát vòng lặp vì đã tìm thấy pet
                    }
                }

                if (!petFound) {
                    Toast.makeText(getContext(), "Pet data not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error fetching pet data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchUserData(String userId) {
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(getContext(), "User ID not found. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userName = snapshot.child("userName").getValue(String.class);
                    String userEmail = snapshot.child("email").getValue(String.class);
                    Log.d(TAG, "User Name from Firebase: " + userName);

                    Toast.makeText(getContext(), "User Name"+ userName , Toast.LENGTH_SHORT).show();

                    // Cập nhật thông tin User trên giao diện
                    TextView userNameTextView = requireView().findViewById(R.id.userNameTextView);
//                    TextView userEmailTextView = requireView().findViewById(R.id.userEmailTextView);

                    userNameTextView.setText(userName != null ? userName : "Unknown User");
//                    userEmailTextView.setText(userEmail != null ? userEmail : "Unknown Email");
                } else {
                    Toast.makeText(getContext(), "User data not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error fetching user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
