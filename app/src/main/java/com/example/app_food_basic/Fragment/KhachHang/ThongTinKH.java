package com.example.app_food_basic.Fragment.KhachHang;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.app_food_basic.Activity.DangNhap;
import com.example.app_food_basic.R;
import com.example.app_food_basic.databinding.FragmentThongTinKhBinding;
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

public class ThongTinKH extends Fragment {
    private FragmentThongTinKhBinding binding;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private ProgressDialog progressDialog;
    private static final  int CAMERA_REQUEST_CODE = 200;
    private static final  int STORAGE_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    private String[] cameraPermissions;
    private String[] storagePermissions;
    private Uri image_uri;
    public ThongTinKH() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentThongTinKhBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        progressDialog = new ProgressDialog(getActivity());
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Đang cập nhật");
        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("TaiKhoan");
        loadUser();
        binding.avatar.setOnClickListener(view1 -> option());
        binding.xacNhan.setOnClickListener(view1 -> capNhatTT());
        binding.dangXuat.setOnClickListener(view1 -> dangXuat());
        return view;
    }
    private void dangXuat() {
        new AlertDialog.Builder(requireActivity(), R.style.CustomAlertDialog)
                .setTitle("Đăng xuất khỏi ứng dụng")
                .setMessage("Bạn có muốn đăng xuất không?")
                .setPositiveButton("Có", (dialogInterface, i) -> {
                    startActivity(new Intent(requireActivity(), DangNhap.class));
                    firebaseAuth.signOut();
                })
                .setNegativeButton("Không", null).show();
    }
    private void loadUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("TaiKhoan");
        reference.child(Objects.requireNonNull(firebaseAuth.getUid()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // lấy dữ liệu từ csdl
                        String hoTen = "" + snapshot.child("hoTen").getValue();
                        String sdt = "" + snapshot.child("sdt").getValue();
                        String tinhTP = "" + snapshot.child("tinhTP").getValue();
                        String quanHuyen = "" + snapshot.child("quanHuyen").getValue();
                        String diaChi = "" + snapshot.child("diaChi").getValue();
                        String avatar = "" + snapshot.child("avatar").getValue();
                        // gán vào view
                        binding.hoTen.setText(hoTen);
                        binding.sdt.setText(sdt);
                        binding.tinhTP.setText(tinhTP);
                        binding.quanHuyen.setText(quanHuyen);
                        binding.diaChi.setText(diaChi);
                        try {
                            Picasso.get().load(avatar).fit().centerCrop()
                                    .placeholder(R.drawable.fork)
                                    .error(R.drawable.fork)
                                    .into(binding.avatar);
                        } catch (Exception e) {
                            binding.avatar.setImageResource(R.drawable.fork);
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    String hoTen, sdt, tinhTP, quanHuyen, diaChi;
    private void capNhatTT() {

        hoTen = binding.hoTen.getText().toString().trim();
        sdt = binding.sdt.getText().toString().trim();
        tinhTP = binding.tinhTP.getText().toString().trim();
        quanHuyen = binding.quanHuyen.getText().toString().trim();
        diaChi = binding.diaChi.getText().toString().trim();
        if (hoTen.isEmpty() || sdt.isEmpty() || tinhTP.isEmpty() || quanHuyen.isEmpty() || diaChi.isEmpty()) {
            dialog("Cập nhật thất bại", "Thông tin không được bỏ trống");
            return;
        }
        if (sdt.length() < 10) {
            dialog("Cập nhật thất bại", "Số điện thoại phải dài hơn hoặc bằng 10 ký tự");
            return;
        }
        xacNhan();
    }
    private void xacNhan() {
        // cập nhật thông tin khi không có avatar
        if (image_uri == null) {
            progressDialog.show();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("hoTen", "" + hoTen);
            hashMap.put("sdt", "" + sdt);
            hashMap.put("tinhTP", "" + tinhTP);
            hashMap.put("quanHuyen", "" + quanHuyen);
            hashMap.put("diaChi", "" + diaChi);
            reference.child(Objects.requireNonNull(firebaseAuth.getUid()))
                    .updateChildren(hashMap)
                    .addOnSuccessListener(unused -> {
                        progressDialog.dismiss();
                        dialog("Cập nhật thông tin", "Cập nhật thành công");

                    }).addOnFailureListener(e -> {
                        dialog("Cập nhật thông tin", "Cập nhật thất bại, lý do: " + e.getMessage());

                    });
        } else capNhatAvatar();

    }
    private void capNhatAvatar() {
        progressDialog.show();
        String filePathAndName = "avatar/" + "" + firebaseAuth.getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(image_uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());
            Uri uRL = uriTask.getResult();
            if (uriTask.isSuccessful()) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("hoTen", "" + hoTen);
                hashMap.put("sdt", "" + sdt);
                hashMap.put("tinhTP", "" + tinhTP);
                hashMap.put("quanHuyen", "" + quanHuyen);
                hashMap.put("diaChi", "" + diaChi);
                hashMap.put("avatar", "" + uRL);
                reference.child(Objects.requireNonNull(firebaseAuth.getUid()))
                        .updateChildren(hashMap)
                        .addOnSuccessListener(unused -> {
                            progressDialog.dismiss();
                            dialog("Cập nhật thông tin", "Cập nhật thành công");
                        }).addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            dialog("Cập nhật thông tin", "Cập nhật thất bại, lý do: " + e.getMessage());
                        });
            }

        }).addOnFailureListener(e -> {

        });
    }
    private void dialog(String title, String message) {
        new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    // chọn hình từ camera hoặc thư viện
    private void option() {
        String[] luaChon = {"Camera", "Thư viện"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
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
        boolean result = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    // nếu chưa cấp quyền thì gửi yêu cầu
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(requireActivity(), cameraPermissions, CAMERA_REQUEST_CODE);
    }
    // chụp hình
    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Image Description");
        image_uri = requireActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }
    // kiểm tra người dùng có cấp quyền truy cập bộ nhớ hay chưa
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }
    // nếu chưa cấp quyền thì gửi yêu cầu
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(requireActivity(), storagePermissions, STORAGE_REQUEST_CODE);
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
                        Toast.makeText(requireActivity(), "Lỗi", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(requireActivity(), "Lỗi", Toast.LENGTH_SHORT).show();

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
                binding.avatar.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                binding.avatar.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}