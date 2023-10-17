package com.example.app_food_basic.Fragment.QuanAn;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.app_food_basic.Adapter.AdapterSanPham;
import com.example.app_food_basic.Model.ModelSanPham;
import com.example.app_food_basic.R;
import com.example.app_food_basic.databinding.FragmentTrangChuBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TrangChu extends Fragment {
    private FragmentTrangChuBinding binding;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    private String[] cameraPermissions;
    private String[] storagePermissions;
    private Uri image_uri;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private ArrayList<ModelSanPham> modelSanPhams = new ArrayList<>();
    private AdapterSanPham adapterSanPham;

    private GridLayoutManager gridLayoutManager;
    public TrangChu() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTrangChuBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        firebaseAuth = FirebaseAuth.getInstance();
        binding.themMoi.setOnClickListener(view1 -> dialogThemMoi());
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        loadSanPham();
        binding.timKiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterSanPham.getFilter().filter(charSequence);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }
    private void loadSanPham() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SanPham");
        reference.orderByChild("uid")
                .equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        modelSanPhams.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelSanPham sanPham = ds.getValue(ModelSanPham.class);
                            modelSanPhams.add(sanPham);
                        }
                        if (modelSanPhams.size() == 0) {
                            binding.relativeLayout.setVisibility(View.GONE);
                            binding.empty.setVisibility(View.VISIBLE);
                        } else {
                            binding.relativeLayout.setVisibility(View.VISIBLE);
                            binding.empty.setVisibility(View.GONE);
                            adapterSanPham = new AdapterSanPham(getActivity(), modelSanPhams);
                            gridLayoutManager = new GridLayoutManager(getActivity(), 2);
                            binding.sanPhamRv.setLayoutManager(gridLayoutManager);
                            binding.sanPhamRv.setAdapter(adapterSanPham);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    // dialog thêm món mới
    private EditText tenSpEt, giaGocEt, moTaEt, giaGiamEt;
    private TextView tiLeTv;
    private String tenSp, giaGoc, moTa, tiLe, giamCon;
    private ImageView hinhSP;
    private boolean coGiamGia = false;
    private SwitchCompat giamGiaSwitch;
    private AlertDialog dialog;
    private double giaGocDouble, giaGiamDouble, tiLeDouble;
    private void dialogThemMoi() {
        // gán view vào dialog
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_them_moi, null);
        tenSpEt = view.findViewById(R.id.tenSP);
        giaGocEt = view.findViewById(R.id.giaCa);
        moTaEt = view.findViewById(R.id.moTa);
        giaGiamEt = view.findViewById(R.id.giamCon);
        tiLeTv = view.findViewById(R.id.tiLe);
        giamGiaSwitch = view.findViewById(R.id.coGiamGia);
        hinhSP = view.findViewById(R.id.hinhSP);
        giaGiamEt.setVisibility(View.GONE);
        tiLeTv.setVisibility(View.GONE);
        // hiển thị phần trăm giảm giá nếu có giảm
        giamGiaSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                giaGiamEt.setVisibility(View.VISIBLE);
                tiLeTv.setVisibility(View.VISIBLE);
            } else {
                giaGiamEt.setVisibility(View.GONE);
                tiLeTv.setVisibility(View.GONE);
            }
        });
        hinhSP.setOnClickListener(view1 -> option());
        Button themSp = view.findViewById(R.id.themSp);
        // khởi tạo dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        builder.setView(view);
        dialog = builder.create();
        // hiển thị dialog
        dialog.show();
        themSp.setOnClickListener(view1 -> {
            checkData();

        });

    }
    // kiểm tra dl nhập vào
    private void checkData() {
        tenSp = tenSpEt.getText().toString().trim();
        giaGoc = giaGocEt.getText().toString().trim();
        moTa = moTaEt.getText().toString().trim();
        coGiamGia = giamGiaSwitch.isChecked();
        giaGocDouble = Double.parseDouble(giaGoc);

        if (image_uri == null) {
            new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog)
                    .setTitle("Thêm thất bại")
                    .setMessage("Món ăn phải có hình ảnh mới được chứ bạn")
                    .setPositiveButton("OK", null).show();
            return;
        }
        if (tenSp.isEmpty()) {
            tenSpEt.setError("Bạn không được bỏ trống tên của món ăn");
            return;
        }
        if (giaGoc.isEmpty()) {
            giaGocEt.setError("Bạn phải điền giá của món ăn");
            return;
        }
        if (moTa.isEmpty()) {
            moTaEt.setError("Hãy mô tả món ăn của bạn");
            return;
        }
        if (coGiamGia) {
            giamCon = giaGiamEt.getText().toString().trim();
            giaGiamDouble = Double.parseDouble(giamCon);
            tiLeDouble = (giaGocDouble - giaGiamDouble) / giaGocDouble * 100;
            int lamTron = (int) Math.round(tiLeDouble);
            tiLeTv.setText("" + lamTron);
            tiLe = String.valueOf(lamTron);
            if (giamCon.isEmpty()) {
                giaGiamEt.setError("Hãy nhập giá giảm món ăn này");
                return;
            }
            if (giaGiamDouble >= giaGocDouble) {
                new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog)
                        .setTitle("Lỗi")
                        .setMessage("Giá giảm phải nhỏ hơn giá gốc của món ăn")
                        .setPositiveButton("OK", null).show();
                return;
            }

        } else {
            giamCon = "0";
            tiLe = "";
        }

        themMoi();
    }
    // tiến hành thêm món ăn sau khi kiểm tra
    private void themMoi() {
        progressDialog.setMessage("Đang thêm món ăn");
        progressDialog.show();
        final String timestamp = ""+System.currentTimeMillis();
        String path = "hinh_anh_mon_an/" + "" + timestamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(path);
        storageReference.putFile(image_uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
            while (!task.isSuccessful());
            Uri downloadImageUri = task.getResult();
            if (task.isSuccessful()) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("maSp", ""+timestamp);
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
                reference.child(timestamp)
                        .updateChildren(hashMap)
                        .addOnSuccessListener(unused -> {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog)
                                    .setMessage("Thêm thành công")
                                    .setPositiveButton("OK", (dialogInterface, i) -> dialog.dismiss()).show();
                        }).addOnFailureListener(e -> {
                            new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog)
                                    .setMessage("Thêm thất bại, lỗi: " + e.getMessage())
                                    .setPositiveButton("OK", null).show();
                        });

            }
        }).addOnFailureListener(e -> {

        });

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
                hinhSP.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                hinhSP.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}