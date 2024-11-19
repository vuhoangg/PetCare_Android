package com.example.petcase.Adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcase.CRUDUserActivity;
import com.example.petcase.Domain.User;
import com.example.petcase.EditUserActivity;
import com.example.petcase.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;

    public UserAdapter(List<User> userList, CRUDUserActivity crudUserActivity) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userName.setText(user.getUserName());
        holder.userEmail.setText(user.getEmail());

        // Thiết lập sự kiện click cho nút Sửa
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditUserActivity.class);
            intent.putExtra("USER_ID", user.getUserId());
            intent.putExtra("USER_NAME", user.getUserName());
            intent.putExtra("USER_EMAIL", user.getEmail());
            intent.putExtra("USER_PHONE", user.getPhoneNumber());
            intent.putExtra("USER_ADDRESS", user.getAddress());
            v.getContext().startActivity(intent);
        });

        // Thiết lập sự kiện click cho nút Xóa
        holder.btnDelete.setOnClickListener(v -> {
            if (user.getUserId() == null) {
                Toast.makeText(v.getContext(), "User ID is null", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("User").child(user.getUserId());

            myRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(v.getContext(), "User deleted successfully", Toast.LENGTH_SHORT).show();
                    // Kiểm tra `position` có hợp lệ không trước khi xóa
                    if (position >= 0 && position < userList.size()) {
                        userList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, userList.size());  // Cập nhật lại vị trí
                    }
                } else {
                    Exception e = task.getException();
                    if (e != null) {
                        Log.e("Firebase", "Error deleting user", e);
                    }
                    Toast.makeText(v.getContext(), "Failed to delete user", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(v.getContext(), "Firebase operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userEmail;
        Button btnEdit, btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userEmail = itemView.findViewById(R.id.userEmail);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
