package com.example.app_food_basic.Fragment.KhachHang;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.app_food_basic.Activity.KhachHang.GioHang;
import com.example.app_food_basic.Adapter.AdapterSanPham;
import com.example.app_food_basic.Model.ModelSanPham;
import com.example.app_food_basic.R;
import com.example.app_food_basic.databinding.FragmentTrangChuKhBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class TrangChuKH extends Fragment {
    private FragmentTrangChuKhBinding binding;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<ModelSanPham> sanPhamArrayList = new ArrayList<>();
    private AdapterSanPham adapterSanPham;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    public TrangChuKH() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTrangChuKhBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getActivity(), R.style.CustomAlertDialog);
        progressDialog.setCanceledOnTouchOutside(false);
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
        binding.gioHang.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity(), GioHang.class));
        });
        loadSanPhamTheoDiaChi();
        return view;
    }
    private void loadSanPhamTheoDiaChi() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("TaiKhoan");
        reference.child(Objects.requireNonNull(firebaseAuth.getUid())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // lấy dl từ firebase để hiển thị lên view
                String quanHuyen = "" + snapshot.child("quanHuyen").getValue();
                diaChi(quanHuyen);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void diaChi(String diaChi) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("TaiKhoan");
        reference.orderByChild("taiKhoan").equalTo("QuanAn").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String quanHuyen = "" + ds.child("quanHuyen").getValue();
                    if (quanHuyen.equals(diaChi)) {
                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("SanPham");
                        reference2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                sanPhamArrayList.clear();
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    ModelSanPham modelSanPham = ds.getValue(ModelSanPham.class);
                                    sanPhamArrayList.add(modelSanPham);
                                }

                                if (sanPhamArrayList.size() == 0) {
                                    binding.relativeLayout.setVisibility(View.GONE);
                                    binding.linearLayout.setVisibility(View.VISIBLE);
                                } else {
                                    binding.relativeLayout.setVisibility(View.VISIBLE);
                                    binding.linearLayout.setVisibility(View.GONE);
                                    gridLayoutManager = new GridLayoutManager(getActivity(), 2);
                                    adapterSanPham = new AdapterSanPham(getActivity(), sanPhamArrayList);
                                    binding.sanPhamRv.setLayoutManager(gridLayoutManager);
                                    binding.sanPhamRv.setAdapter(adapterSanPham);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}