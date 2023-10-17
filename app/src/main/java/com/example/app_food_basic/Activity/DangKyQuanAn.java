package com.example.app_food_basic.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_food_basic.R;
import com.example.app_food_basic.databinding.ActivityDangKyQuanAnBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class DangKyQuanAn extends AppCompatActivity {
    private ActivityDangKyQuanAnBinding binding;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDangKyQuanAnBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        binding.dangKy.setOnClickListener(view1 -> dangKy());

    }
    private String tenQuan, sDT, tinhTP, quanHuyen, diaChi, email, matKhau, nhapLaiMK;
    private void dangKy() {
        // get data
        tenQuan = binding.tenQuan.getText().toString().trim();
        sDT = binding.sDT.getText().toString().trim();
        tinhTP = binding.tinhTP.getText().toString().trim();
        quanHuyen = binding.quanHuyen.getText().toString().trim();
        diaChi = binding.diaChi.getText().toString().trim();
        email = binding.email.getText().toString().trim();
        matKhau = binding.matKhau.getText().toString().trim();
        nhapLaiMK = binding.nhapLaiMK.getText().toString().trim();
        if (tenQuan.isEmpty()) {
            binding.tenQuan.setError("Tên quán không được bỏ trống");
            return;
        }
        if (sDT.isEmpty()) {
            binding.sDT.setError("SĐT không được bỏ trống");
            return;
        }
        if (tinhTP.isEmpty()) {
            binding.tinhTP.setError("Tỉnh/thành phố không được bỏ trống");
            return;
        }
        if (quanHuyen.isEmpty()) {
            binding.quanHuyen.setError("Quận/huyện không được bỏ trống");
            return;
        }
        if (diaChi.isEmpty()) {
            binding.diaChi.setError("Địa chỉ không được bỏ trống");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.email.setError("Sai định dạng email");
            return;
        }
        if (matKhau.isEmpty() || matKhau.length() < 6) {
            binding.matKhau.setError("Mật khẩu phải dài hơn 6 ký tự");
            return;
        }
        if (nhapLaiMK.isEmpty() || !nhapLaiMK.equals(matKhau)) {
            binding.quanHuyen.setError("Mật khẩu không trùng khớp");
            return;
        }
        taoTaiKhoan();

    }

    private void taoTaiKhoan() {// tạo tài khoản dựa trên email và mật khẩu đã nhập
        progressDialog.setMessage("Đang tạo tài khoản");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email, matKhau)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        saveData();
                        new AlertDialog.Builder(this, R.style.CustomAlertDialog)
                                .setTitle("Đăng ký")
                                .setMessage("Đăng ký thành công, bạn hãy kiểm tra mail để xác thực tài khoản nhé")
                                .setPositiveButton("OK", (dialogInterface, i) -> {
                                    xacThucEmail();
                                    startActivity(new Intent(getApplicationContext(), DangNhap.class));
                                    finish();
                                }).show();
                    }
                }).addOnFailureListener(e -> {
                    new AlertDialog.Builder(this, R.style.CustomAlertDialog)
                            .setTitle("Đăng ký")
                            .setMessage("Đăng ký thất bại, lý do: " + e.getMessage())
                            .setPositiveButton("OK", null)
                            .show();
                });
    }

    private void xacThucEmail() {// xác thực email
        Objects.requireNonNull(firebaseAuth.getCurrentUser()).sendEmailVerification()
                .addOnSuccessListener(unused -> {
                    // bỏ trống, tùy ý thêm thông báo

                }).addOnFailureListener(e -> {
                    new AlertDialog.Builder(this, R.style.CustomAlertDialog)
                            .setTitle("Xác thực tài khoản")
                            .setMessage("Gửi liên kết yêu cầu xác thực thất bại, lý do: " + e.getMessage())
                            .setPositiveButton("OK", null)
                            .show();
                });
    }
    // lưu thông tin vào csdl
    private void saveData() {
        HashMap<String, Object> hashMap = new HashMap<>();
        progressDialog.setMessage("Đang lưu thông tin");
        final String timestamp = "" + System.currentTimeMillis();
        String quocGia = "Việt Nam";
        hashMap.put("uid", "" + firebaseAuth.getUid());
        hashMap.put("email", "" + email);
        hashMap.put("tenQuan", "" + tenQuan);
        hashMap.put("sdt", "" + sDT);
        hashMap.put("quocGia", "" + quocGia);
        hashMap.put("tinhTP", "" + tinhTP);
        hashMap.put("quanHuyen", "" + quanHuyen);
        hashMap.put("diaChi", "" + diaChi);
        hashMap.put("timestamp", "" + timestamp);
        hashMap.put("taiKhoan", "QuanAn");
        hashMap.put("online", "true");
        hashMap.put("avatar", "");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("TaiKhoan");
        reference.child(Objects.requireNonNull(firebaseAuth.getUid()))
                .setValue(hashMap)
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Lưu thành công", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(this, R.style.CustomAlertDialog)
                            .setTitle("Lưu thông tin")
                            .setMessage("Không thể lưu thông tin, lý do: " + e.getMessage())
                            .setPositiveButton("OK", null)
                            .show();
                });
    }
}