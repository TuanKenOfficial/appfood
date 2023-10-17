package com.example.app_food_basic.Activity.QuanAn;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.app_food_basic.R;
import com.example.app_food_basic.databinding.ActivityCapNhatSpBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

public class CapNhatSP extends AppCompatActivity {
    private ActivityCapNhatSpBinding binding;
    private String maSp;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    private String[] cameraPermissions;
    private String[] storagePermissions;
    private double gia_goc, gia_giam, tiLePhanTram;
    private Uri image_uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCapNhatSpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        maSp = getIntent().getStringExtra("maSp");
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this, R.style.CustomAlertDialog);
        progressDialog.setCanceledOnTouchOutside(false);
        loadSanPham(maSp);
        binding.coGiamGia.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                binding.giamCon.setVisibility(View.VISIBLE);
                binding.tiLe.setVisibility(View.VISIBLE);
            } else {
                binding.giamCon.setVisibility(View.GONE);
                binding.tiLe.setVisibility(View.GONE);
            }
        });
        binding.capNhat.setOnClickListener(view1 -> checkData());
        binding.hinhAnh.setOnClickListener(view1 -> option());
    }

    private void loadSanPham(String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SanPham");
        reference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String hinhAnh = "" + snapshot.child("hinhAnh").getValue();
                String tenSp = "" + snapshot.child("tenSp").getValue();
                String giaGoc = "" + snapshot.child("giaGoc").getValue();
                String moTa = "" + snapshot.child("moTa").getValue();
                String coGiamGia = "" + snapshot.child("coGiamGia").getValue();
                String giaGiam = "" + snapshot.child("giaGiam").getValue();
                String tiLe = "" + snapshot.child("tiLeGiam").getValue();
                Picasso.get().load(hinhAnh).fit().centerCrop()
                        .placeholder(R.drawable.shopivhd)
                        .error(R.drawable.shopivhd)
                        .into(binding.hinhAnh);
                binding.tenSP.setText(tenSp);
                binding.giaCa.setText(giaGoc);
                binding.moTa.setText(moTa);
                if (coGiamGia.equals("true")) {
                    binding.coGiamGia.setChecked(true);
                    binding.giamCon.setText(giaGiam);
                    binding.tiLe.setText(tiLe);
                } else {
                    binding.giamCon.setVisibility(View.GONE);
                    binding.tiLe.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void capNhat(String id) {
        // cập nhật khi món ăn đã có sẵn ảnh.
        if (image_uri == null) {
            progressDialog.setMessage("Đang cập nhật...");
            progressDialog.show();
            final String timestamp = ""+System.currentTimeMillis();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("tenSp", ""+tenSp);
            hashMap.put("moTa", ""+moTa);
            hashMap.put("giaGoc", ""+giaGoc);
            hashMap.put("giaGiam", ""+giamCon);
            hashMap.put("tiLeGiam", ""+tiLe);
            hashMap.put("coGiamGia", ""+coGiamGia);
            hashMap.put("timestamp", ""+timestamp);
            hashMap.put("uid", ""+firebaseAuth.getUid());
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SanPham");
            reference.child(id).updateChildren(hashMap).addOnSuccessListener(unused -> {
                progressDialog.dismiss();
                new AlertDialog.Builder(this, R.style.CustomAlertDialog)
                        .setMessage("Cập nhật thành công")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            onBackPressed();
                            super.finish();
                        }).show();
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                new AlertDialog.Builder(this, R.style.CustomAlertDialog)
                        .setMessage("Cập nhật thất bại, lỗi: " + e.getMessage())
                        .setPositiveButton("OK", null).show();
            });
        } else capNhatHinhAnh(id);

    }
    private void capNhatHinhAnh(String id) {
        progressDialog.setMessage("Đang cập nhật...");
        progressDialog.show();
        final String timestamp = ""+System.currentTimeMillis();
        String path = "hinh_anh_mon_an/" + "" + id;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(path);
        storageReference.putFile(image_uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
            while (!task.isSuccessful());
            Uri downloadImageUri = task.getResult();
            if (task.isSuccessful()) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("tenSp", ""+tenSp);
                hashMap.put("moTa", ""+moTa);
                hashMap.put("hinhAnh", ""+downloadImageUri);
                hashMap.put("giaGoc", ""+giaGoc);
                hashMap.put("giaGiam", ""+giamCon);
                hashMap.put("tiLeGiam", ""+tiLe);
                hashMap.put("coGiamGia", ""+coGiamGia);
                hashMap.put("timestamp", ""+timestamp);
                hashMap.put("uid", ""+firebaseAuth.getUid());
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SanPham");
                reference.child(id)
                        .updateChildren(hashMap)
                        .addOnSuccessListener(unused -> {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(getApplicationContext(), R.style.CustomAlertDialog)
                                    .setMessage("Cập nhật thành công")
                                    .setPositiveButton("OK", (dialogInterface, i) -> {
                                        onBackPressed();
                                        super.finish();
                                    }).show();
                        }).addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(getApplicationContext(), R.style.CustomAlertDialog)
                                    .setMessage("Cập nhật thất bại, lỗi: " + e.getMessage())
                                    .setPositiveButton("OK", null).show();
                        });

            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            new AlertDialog.Builder(getApplicationContext(), R.style.CustomAlertDialog)
                    .setMessage("Cập nhật thất bại, lỗi: " + e.getMessage())
                    .setPositiveButton("OK", null).show();
        });
    }
    private String tenSp, giaGoc, moTa, tiLe, giamCon;
    protected boolean coGiamGia = false;
    private void checkData() {
        tenSp = binding.tenSP.getText().toString().trim();
        giaGoc = binding.giaCa.getText().toString().trim();
        gia_goc = Double.parseDouble(giaGoc);
        moTa = binding.moTa.getText().toString().trim();
        coGiamGia = binding.coGiamGia.isChecked();
        if (tenSp.isEmpty()) {
            binding.tenSP.setError("Bạn không được bỏ trống tên của món ăn");
            return;
        }
        if (giaGoc.isEmpty()) {
            binding.giaCa.setError("Bạn phải điền giá của món ăn");
            return;
        }
        if (moTa.isEmpty()) {
            binding.moTa.setError("Hãy mô tả món ăn của bạn");
            return;
        }
        if (coGiamGia) {
            giamCon = binding.giamCon.getText().toString().trim();
            gia_giam = Double.parseDouble(giamCon);
            // lấy tỉ lệ % giảm
            tiLePhanTram = (gia_goc - gia_giam) / gia_goc * 100;
            int lamTron = (int) Math.round(tiLePhanTram);
            binding.tiLe.setText("" + lamTron);
            tiLe = String.valueOf(lamTron);
            if (gia_giam >= gia_goc) {
                binding.giamCon.setError("Lỗi, giá giảm phải nhỏ hơn giá gốc");
                return;
            }
            if (giamCon.isEmpty()) {
                binding.giamCon.setError("Hãy nhập giá giảm món ăn này");
                return;
            }

        } else {
            giamCon = "0";
            tiLe = "";
        }
        capNhat(maSp);
    }


    // chọn hình từ camera hoặc thư viện
    private void option() {
        String[] luaChon = {"Camera", "Thư viện"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn ảnh từ").setItems(luaChon, (dialog, which) -> {
            if (which == 0) {
                if (checkCameraPermission()) {
                    pickFromCamera();
                } else requestCameraPermission();
            } else {
                if (checkStoragePermission()) {
                    pickFromGallery();
                } else requestStoragePermission();
            }
        }).show();
    }
    // kiểm tra người dùng có cấp quyền truy cập camera hay chưa
    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    // nếu chưa cấp quyền thì gửi yêu cầu
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }
    // chụp hình
    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Image Description");
        image_uri = getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }
    // kiểm tra người dùng có cấp quyền truy cập bộ nhớ hay chưa
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }
    // nếu chưa cấp quyền thì gửi yêu cầu
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }
    // lấy hình từ thư viện
    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(getApplicationContext(), "Lỗi", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(getApplicationContext(), "Lỗi", Toast.LENGTH_SHORT).show();

                    }
                }
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = Objects.requireNonNull(data).getData();
                binding.hinhAnh.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                binding.hinhAnh.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}