package com.example.app_food_basic.Activity.KhachHang;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_food_basic.Adapter.AdapterGioHang;
import com.example.app_food_basic.Model.ModelGioHang;
import com.example.app_food_basic.R;
import com.example.app_food_basic.databinding.ActivityGioHangBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class GioHang extends AppCompatActivity {
    public ActivityGioHangBinding binding;
    private ArrayList<ModelGioHang> gioHangArrayList = new ArrayList<>();
    private AdapterGioHang adapterGioHang;
    private String uid;
    public double tongHoaDon;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private String diaChi, tenToi, tenQuan, sdt;
    private EasyDB easyDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGioHangBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        diaChi = prefs.getString("diaChi", "null"); //null, không truyền dữ liệu nào khác
        tenToi = prefs.getString("tenToi", "null"); //null, không truyền dữ liệu nào khác
        tenQuan = prefs.getString("tenQuan", "null"); //null, không truyền dữ liệu nào khác
        sdt = prefs.getString("sdtToi", "null"); //null, không truyền dữ liệu nào khác
        firebaseAuth = FirebaseAuth.getInstance();
        khoitaoGioHang();
        loadGioHang();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        binding.thanhToan.setOnClickListener(view1 -> datHang());
        setContentView(view);
    }
    private void khoitaoGioHang() {
        easyDB = EasyDB.init(this, "gioHang")
                .setTableName("gioHang")
                .addColumn(new Column("id_gio", "text", "unique"))
                .addColumn(new Column("ma_sp", "text", "not null"))
                .addColumn(new Column("hinh_anh", "text", "not null"))
                .addColumn(new Column("co_giam_gia", "text", "not null"))
                .addColumn(new Column("ti_le", "text", "not null"))
                .addColumn(new Column("ten_sp", "text", "not null"))
                .addColumn(new Column("gia_goc", "text", "not null"))
                .addColumn(new Column("gia_giam", "text", "not null"))
                .addColumn(new Column("soLuongSP", "text", "not null"))
                .addColumn(new Column("tongGiaTienSP", "text", "not null"))
                .addColumn(new Column("uid", "text", "not null"))
                .doneTableColumn();
    }
    private void loadGioHang() {
        gioHangArrayList.clear();
        khoitaoGioHang();
        Cursor cursor = easyDB.getAllData();
        while (cursor.moveToNext()) {
            String id_gio = cursor.getString(1);
            String ma_sp = cursor.getString(2);
            String hinh_anh = cursor.getString(3);
            String co_giam_gia = cursor.getString(4);
            String ti_le = cursor.getString(5);
            String ten_sp = cursor.getString(6);
            String gia_goc = cursor.getString(7);
            String gia_giam = cursor.getString(8);
            String soLuongSP = cursor.getString(9);
            String tongGiaTienSP = cursor.getString(10);
            uid = cursor.getString(11);
            tongHoaDon = tongHoaDon + Double.parseDouble(tongGiaTienSP);
            ModelGioHang gioHang = new
                    ModelGioHang(id_gio, ma_sp, hinh_anh, co_giam_gia, ti_le,
                    ten_sp, gia_goc, gia_giam, soLuongSP, tongGiaTienSP, uid);
            gioHangArrayList.add(gioHang);
        }
        if (gioHangArrayList.size() == 0) {
            binding.view.setVisibility(View.GONE);
            binding.empty.setVisibility(View.VISIBLE);
            binding.thanhToan.setEnabled(false);
        } else {
            adapterGioHang = new AdapterGioHang(this, gioHangArrayList);
            binding.dsGioHang.setAdapter(adapterGioHang);
        }

        binding.tongDonHang.setText(("Tổng cộng: " + String.format("%.0f", tongHoaDon) + "đ"));
    }
    private void datHang() {
        progressDialog.setMessage("Đang lập hóa đơn");
        progressDialog.show();
        final String timeStamp = "" + System.currentTimeMillis();
        // xóa chữ đ khỏi string
        String tienHoaDon = String.valueOf(tongHoaDon).replace("đ", "");
        // chuẩn bị dữ liệu
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("maHd", "" + timeStamp);
        hashMap.put("ngayDat", ""+timeStamp);
        hashMap.put("tongHd", "" + tienHoaDon);
        hashMap.put("uid_khachHang", ""+firebaseAuth.getUid());
        hashMap.put("tenKhachHang", ""+tenToi);
        hashMap.put("sdtKhachHang", ""+sdt);
        hashMap.put("uid_quanAn", "" + uid);
        hashMap.put("tenQuan", "" + tenQuan);
        hashMap.put("diaChi", "" + diaChi);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DonHang");
        reference.child(timeStamp).setValue(hashMap).addOnSuccessListener(unused -> {
            String giaSanPham;
            for (int i = 0; i < gioHangArrayList.size(); i++) {
                String maSp = gioHangArrayList.get(i).getMa_sp();
                String tongGiaTienSP = gioHangArrayList.get(i).getTongGiaTienSP();
                String tenSp = gioHangArrayList.get(i).getTen_sp();
                String soLuong = gioHangArrayList.get(i).getSoLuongSP();
                String coGiamGia = gioHangArrayList.get(i).getCo_giam_gia();
                String anhSanPham = gioHangArrayList.get(i).getHinh_anh();
                if (coGiamGia.equals("true")) {
                    giaSanPham = gioHangArrayList.get(i).getGia_giam();
                } else giaSanPham = gioHangArrayList.get(i).getGia_goc();
                HashMap<String, String> map = new HashMap<>();
                map.put("maSp", maSp);
                map.put("anhSanPham", anhSanPham);
                map.put("tenSp", tenSp);
                map.put("giaSanPham", giaSanPham);
                map.put("tongGiaTienSP", tongGiaTienSP);
                map.put("soLuong", soLuong);
                map.put("uid_khachHang", firebaseAuth.getUid());
                reference.child(timeStamp).child("MonAn").child(maSp).setValue(map)
                        .addOnSuccessListener(unused1 -> {
                            progressDialog.dismiss();

                        }).addOnFailureListener(e -> {
                            progressDialog.dismiss();
                        });
            }
            new AlertDialog.Builder(GioHang.this, R.style.CustomAlertDialog)
                    .setMessage("Đặt món ăn thành công, bạn hãy kiểm tra hóa đơn nhé")
                    // xóa hết món ăn trong giỏ sau khi đặt hàng
                    .setPositiveButton("OK", (dialogInterface, i) -> {
                        onBackPressed();
                    })
                    .show();
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            new AlertDialog.Builder(GioHang.this, R.style.CustomAlertDialog)
                    .setMessage("Lập hóa đơn thất bại, lỗi: " + e.getMessage())
                    .setPositiveButton("OK", null)
                    .show();
        });
    }
    public void xoaGioHang() {
        // Xóa hết sp khỏi giỏ
        easyDB.deleteAllDataFromTable();
    }
    // xóa sạch giỏ hàng sau khi thoát activity
    // nếu bạn không muốn thì đóng cmt method xóa giỏ hàng
    @Override
    protected void onDestroy() {
        super.onDestroy();
        xoaGioHang();

    }
}