package com.example.petcase;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SettingFragment extends Fragment {
    LinearLayout btnLayoutAccount;
    ImageView btnLogout; // Thêm nút logout

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        // Gán view cho btnLayoutAccount
        btnLayoutAccount = view.findViewById(R.id.btnLayoutAccount);
        btnLayoutAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo Intent để mở CRUDUserActivity
                Intent intent = new Intent(getActivity(), CRUDUserActivity.class);
                startActivity(intent);
            }
        });

        // Gán view cho nút đăng xuất
        btnLogout = view.findViewById(R.id.btnLogout); // ID của nút đăng xuất trong layout
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        return view;
    }

    private void logoutUser() {
        // Đăng xuất khỏi Firebase
        FirebaseAuth.getInstance().signOut();

        // Hiển thị thông báo đăng xuất thành công
        Toast.makeText(getActivity(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

        // Điều hướng người dùng trở lại màn hình đăng nhập
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        // Kết thúc Activity cha nếu có
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
