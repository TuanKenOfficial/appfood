package com.example.app_food_basic.Fragment.QuanAn;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.app_food_basic.Model.ModelHoaDon;
import com.example.app_food_basic.databinding.FragmentDoanhThuBinding;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class DoanhThu extends Fragment {
    private FragmentDoanhThuBinding binding;
    private ArrayList<ModelHoaDon> modelHoaDonArrayList;
    private FirebaseAuth firebaseAuth;
    private MonthYearPickerDialogFragment monthYearPickerDialogFragment;
    private double tong, parseDouble;
    private int thang, nam, count;
    public DoanhThu() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDoanhThuBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        firebaseAuth = FirebaseAuth.getInstance();
        binding.chonThoiGian.setOnClickListener(view1 -> {
            chonThoiGian();
        });
        binding.xoaHet.setOnClickListener(view1 -> {
            binding.tongDonHang.setText("");
            binding.doanhThu.setText("");
            binding.chonThoiGian.setText("");
        });
        return view;
    }

    private void chonThoiGian() {
        count = 0;
        tong = 0;
        parseDouble = 0;
        Calendar calendar = Calendar.getInstance();
        thang = calendar.get(Calendar.MONTH);
        nam = calendar.get(Calendar.YEAR);
        MonthYearPickerDialogFragment dialogFragment = MonthYearPickerDialogFragment.getInstance(thang, nam);
        dialogFragment.show(requireActivity().getSupportFragmentManager(), null);
        dialogFragment.setOnDateSetListener((year, monthOfYear) -> {
            String thoiGian = "0" + (monthOfYear + 1) + "/" + year;
            binding.chonThoiGian.setText(thoiGian);
            truyVanThangNam(thoiGian);
        });
    }
    private void truyVanThangNam(String thoiGian) {
        Calendar calendar = Calendar.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DonHang");
        reference.orderByChild("uid_quanAn").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            // lấy dl từ csdl
                            String doanhThu = "" + ds.child("tongHd").getValue();
                            String ngayDat = "" + ds.child("ngayDat").getValue();

                            calendar.setTimeInMillis(Long.parseLong(ngayDat));
                            // rồi chuyển đổi nó sang chuỗi
                            String ngay_dat = DateFormat.format("MM/yyyy", calendar).toString();
                            if (thoiGian.equals(ngay_dat)) {
                                parseDouble = Double.parseDouble(doanhThu);
                                tong = tong + parseDouble;
                                count++;
                            } else  {
                                tong = 0;
                                count = 0;
                                parseDouble = 0;
                                binding.doanhThu.setText(tong + "đ");
                                binding.tongDonHang.setText(count + " đơn");
                            }

                        }
                        binding.doanhThu.setText(tong + "đ");
                        binding.tongDonHang.setText(count + " đơn");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}