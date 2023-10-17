package com.example.app_food_basic.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_food_basic.Activity.KhachHang.HomeKH;
import com.example.app_food_basic.Activity.QuanAn.Home;
import com.example.app_food_basic.R;
import com.example.app_food_basic.databinding.ActivityDangNhapBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class DangNhap extends AppCompatActivity {
    private ActivityDangNhapBinding binding;
    private ProgressDialog dialog;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDangNhapBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        firebaseAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        binding.dangKy.setOnClickListener(view1 -> startActivity(new Intent(getApplicationContext(), DangKy.class)));
        binding.quenMK.setOnClickListener(view1 -> startActivity(new Intent(getApplicationContext(), QuenMK.class)));
        binding.dangNhap.setOnClickListener(view1 -> dangNhap());

    }
    private String email, matKhau;
    private void dangNhap() {
        email = binding.email.getText().toString().trim();
        matKhau = binding.matKhau.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            new AlertDialog.Builder(this, R.style.CustomAlertDialog)
                    .setTitle("Không thể đăng nhập")
                    .setMessage("Sai định dạng email")
                    .setPositiveButton("OK", null).show();
            return;
        }
        if (matKhau.isEmpty()){
            binding.matKhau.setError("Mật khẩu trống");
            return;
        }
        dialog.setMessage("Đang đăng nhập");
        dialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, matKhau)
                .addOnSuccessListener(authResult -> {
                    // Nếu bạn muốn email đã được xác thực mới có thể đăng nhập thì đóng dòng online()
                    // và mở dòng xacThuc()
                    xacThuc();
//                    online();
                }).addOnFailureListener(e -> {
                    dialog.dismiss();
                    new AlertDialog.Builder(DangNhap.this, R.style.CustomAlertDialog)
                            .setTitle("Không thể đăng nhập")
                            .setMessage("Lỗi: " + e.getMessage())
                            .setPositiveButton("OK", null).show();
                });
    }

    private void online() {
        dialog.setMessage("Đang kiểm tra");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online","true");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("TaiKhoan");
        ref.child(Objects.requireNonNull(firebaseAuth.getUid())).updateChildren(hashMap)
                .addOnSuccessListener(aVoid -> {// thành công
                    // kiểm tra người dùng
                    kiemTra();
                })
                .addOnFailureListener(e -> {
                    // thất bại
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Lỗi mạng"+e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void xacThuc() {
        if (!Objects.requireNonNull(firebaseAuth.getCurrentUser()).isEmailVerified()) {
            new AlertDialog.Builder(DangNhap.this, R.style.CustomAlertDialog)
                    .setTitle("Không thể đăng nhập")
                    .setMessage("Bạn phải xác thực email trước đó")
                    .setPositiveButton("OK", (dialogInterface, i) -> dialog.dismiss()).show();
        } else {
            online();
        }
    }
    private void kiemTra() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("TaiKhoan");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            String accountType = ""+ds.child("taiKhoan").getValue();
                            if (accountType.equals("QuanAn")){
                                dialog.dismiss();
                                startActivity(new Intent(getApplicationContext(), Home.class));
                                finish();
                            }
                            else {
                                dialog.dismiss();
                                startActivity(new Intent(getApplicationContext(), HomeKH.class));
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}