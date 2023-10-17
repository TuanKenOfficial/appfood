package com.example.app_food_basic.Activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_food_basic.R;
import com.example.app_food_basic.databinding.ActivityQuenMkBinding;
import com.google.firebase.auth.FirebaseAuth;

public class QuenMK extends AppCompatActivity {
    private ActivityQuenMkBinding binding;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuenMkBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        firebaseAuth = FirebaseAuth.getInstance();
        binding.troVe.setOnClickListener(view1 -> {
            super.finish();
            onBackPressed();
        });
        binding.khoiPhuc.setOnClickListener(view1 -> khoiPhucMk());
    }

    private void khoiPhucMk() {
        String email = binding.email.getText().toString().trim();
        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(unused -> {
            new AlertDialog.Builder(this, R.style.CustomAlertDialog)
                    .setMessage("Một đường liên kết đặt lại mật khẩu đã gửi đến mail của bạn, hãy kiểm tra nhé")
                    .setPositiveButton("OK", null).show();

        }).addOnFailureListener(e -> {
            new AlertDialog.Builder(this, R.style.CustomAlertDialog)
                    .setMessage("Lỗi: " + e.getMessage())
                    .setPositiveButton("OK", null).show();

        });
    }
}