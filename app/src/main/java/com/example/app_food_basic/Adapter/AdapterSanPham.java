package com.example.app_food_basic.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_food_basic.Activity.QuanAn.CapNhatSP;
import com.example.app_food_basic.Fragment.KhachHang.TrangChuKH;
import com.example.app_food_basic.Model.ModelSanPham;
import com.example.app_food_basic.R;
import com.example.app_food_basic.Service.TimKiem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterSanPham extends RecyclerView.Adapter<AdapterSanPham.HolderSanPham> implements Filterable {
    private Context context;
    public ArrayList<ModelSanPham> sanPhams, kqTimKiem;
    private TimKiem timKiem;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private AlertDialog dialog;
    private TrangChuKH trangChuKH;
    public String phiGiaoHang;


    public AdapterSanPham(Context context, ArrayList<ModelSanPham> sanPhams) {
        this.context = context;
        this.sanPhams = sanPhams;
        this.kqTimKiem = sanPhams;
    }

    @NonNull
    @Override
    public HolderSanPham onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mon_an, parent, false);
        return new HolderSanPham(view);
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull HolderSanPham holder, int position) {
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(context);
        progressDialog.setCanceledOnTouchOutside(false);
        final ModelSanPham modelSanPham = sanPhams.get(position);
        // get
        String ten = modelSanPham.getTenSp();
        String anh = modelSanPham.getHinhAnh();
        String gia_goc = modelSanPham.getGiaGoc();
        String gia_giam = modelSanPham.getGiaGiam();
        String tile = modelSanPham.getTiLeGiam();
        String co_giam_gia = modelSanPham.getCoGiamGia();
        String id = modelSanPham.getMaSp();
        // set
        holder.tenSp.setText(ten);
        Picasso.get().load(anh).fit().centerCrop()
                .placeholder(R.drawable.img_test)
                .error(R.drawable.img_test)
                .into(holder.hinhAnh);
        holder.tiLe.setText("Giảm " + tile + "%");
        holder.giaGoc.setText(gia_goc + "đ");
        holder.giamCon.setText(gia_giam + "đ");
        // nếu có giảm giá, gạch bỏ giá gốc, hiển thị giá giảm
        if (co_giam_gia.equals("true")) {
            holder.tiLe.setVisibility(View.VISIBLE);
            holder.giamCon.setVisibility(View.VISIBLE);
            holder.giaGoc.setPaintFlags(holder.giaGoc.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tiLe.setVisibility(View.GONE);
            holder.giamCon.setVisibility(View.GONE);
            holder.giaGoc.setPaintFlags(0);
        }
        holder.itemView.setOnClickListener(view -> {
            dialogMonAn(id, modelSanPham);
        });

    }
    double giaTien = 0, tongGiaTienSanPham = 0;
    String giaTienString = "";
    int soluong = 0;
    // hiển thị thông tin món ăn lên dialog
    private void dialogMonAn (String id, ModelSanPham sanPham) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
        // dialog sản phẩm
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_tt_mon_an, null);
        ImageView hinhAnh = view.findViewById(R.id.hinhAnh);
        TextView tenSp = view.findViewById(R.id.tenSp);
        TextView tiLe = view.findViewById(R.id.tiLe);
        TextView giaGoc = view.findViewById(R.id.giaGoc);
        TextView giaGiam = view.findViewById(R.id.giaGiam);
        TextView moTa = view.findViewById(R.id.moTa);
        TextView ten_quan = view.findViewById(R.id.tenQuan);
        TextView soLuong = view.findViewById(R.id.soLuong);
        TextView tongGiaTienSp = view.findViewById(R.id.tongGiaTienSp);
        ImageButton capNhat = view.findViewById(R.id.capNhat);
        ImageButton xoa = view.findViewById(R.id.xoa);
        ImageButton giamSL = view.findViewById(R.id.giamSL);
        ImageButton tangSL = view.findViewById(R.id.tangSL);
        Button themVaoGio = view.findViewById(R.id.themVaoGio);
        LinearLayout linear02 = view.findViewById(R.id.linear02);
        builder.setView(view);
        // Không cho phép khách hàng được thao tác lên món ăn
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("TaiKhoan");
        ref.child(Objects.requireNonNull(firebaseAuth.getUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String taiKhoan = "" + snapshot.child("taiKhoan").getValue();
                        if (taiKhoan.equals("KhachHang")) {
                            xoa.setVisibility(View.GONE);
                            capNhat.setVisibility(View.GONE);
                        } else {
                            // xóa sản phẩm theo id
                            xoa.setVisibility(View.VISIBLE);
                            xoa.setOnClickListener(view1 -> xoa(id));
                            // cập nhật sản phẩm, chuyển sang activity khác, tôi không thể để adapter này chứa quá nhiều code được :D
                            capNhat.setVisibility(View.VISIBLE);
                            capNhat.setOnClickListener(view1 -> {
                                Intent intent = new Intent(context, CapNhatSP.class);
                                intent.putExtra("maSp", id);
                                context.startActivity(intent);
                                dialog.dismiss();
                            });
                            themVaoGio.setVisibility(View.GONE);
                            linear02.setVisibility(View.GONE);

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        // set hình ảnh quán ăn
        // 1. truy vấn id của món ăn (mỗi id món ăn là 1 bản ghi (hàng) trong csdl).
        // 2. lấy uid từ bảng món ăn và mang đi truy vấn đến bảng users
        // 3. hiển thị dữ liệu (chỉ khi uid quán ăn của bảng sản phẩm giống với uid của bảng users)
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SanPham");
        reference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // get từ csdl
                String uid = "" + snapshot.child("uid").getValue();
                //get từ model
                String uid02 = sanPham.getUid();
                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("TaiKhoan");
                reference1.child(uid02).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ImageView avatar = view.findViewById(R.id.avatar);
                        // lấy dl từ bảng users
                        String tenQuan = "" + snapshot.child("tenQuan").getValue();
                        String anhDaiDien = "" + snapshot.child("avatar").getValue();
                        phiGiaoHang = "" + snapshot.child("phiGiao").getValue();
                        // chia sẻ dữ liệu cho toàn bộ activity và fragment
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("tenQuan", tenQuan);
                        editor.putString("phiGiao", phiGiaoHang);
                        editor.apply();
                        // set data
                        ten_quan.setText(tenQuan);
                        try {
                            Picasso.get().load(anhDaiDien).fit().centerCrop()
                                    .placeholder(R.drawable.shopivhd)
                                    .error(R.drawable.shopivhd)
                                    .into(avatar);
                        } catch (Exception e) {
                            avatar.setImageResource(R.drawable.shopivhd);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        String maSP = sanPham.getMaSp();
        String anhSp = sanPham.getHinhAnh();
        String coGiamGia = sanPham.getCoGiamGia();
        String tiLePhanTram = sanPham.getTiLeGiam();
        String tenSP = sanPham.getTenSp();
        String giaGocSp = sanPham.getGiaGoc();
        String giaGiamSp = sanPham.getGiaGiam();
        String moTaSp = sanPham.getMoTa();

        Picasso.get().load(anhSp).fit().centerCrop()
                .placeholder(R.drawable.img_test)
                .error(R.drawable.img_test)
                .into(hinhAnh);
        tiLe.setText("Giảm " + tiLePhanTram + "%");
        tenSp.setText(tenSP);
        giaGoc.setText(giaGocSp + "đ");
        giaGiam.setText(giaGiamSp + "đ ");
        moTa.setText(moTaSp);
        if (coGiamGia.equals("true")) {
            tiLe.setVisibility(View.VISIBLE);
            giaGiam.setVisibility(View.VISIBLE);
            giaGoc.setPaintFlags(giaGoc.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            giaTienString = sanPham.getGiaGiam();
            tongGiaTienSp.setText(sanPham.getGiaGiam() + "đ");
        } else {
            tiLe.setVisibility(View.GONE);
            giaGiam.setVisibility(View.GONE);
            giaTienString = sanPham.getGiaGoc();
            tongGiaTienSp.setText(sanPham.getGiaGoc() + "đ");
        }
        giaTien = Double.parseDouble(giaTienString.replace("đ", ""));
        tongGiaTienSanPham = Double.parseDouble(giaTienString.replace("đ", ""));
        soluong = 1;
        tangSL.setOnClickListener(view1 -> {
            soluong++;
            tongGiaTienSanPham = tongGiaTienSanPham + giaTien;
            soLuong.setText("" + soluong);
            tongGiaTienSp.setText(String.valueOf(tongGiaTienSanPham + "đ"));
        });
        giamSL.setOnClickListener(view1 -> {
            if (soluong > 1) {
                soluong--;
                tongGiaTienSanPham = tongGiaTienSanPham - giaTien;
                soLuong.setText("" + soluong);
                tongGiaTienSp.setText(String.valueOf(tongGiaTienSanPham + "đ"));
            }
        });
        themVaoGio.setOnClickListener(view1 -> {
            String tenSanPham = tenSp.getText().toString().trim();
            String tongGiaTienSP = tongGiaTienSp.getText().toString().trim().replace("đ", "");
            String soLuongSanPham = soLuong.getText().toString().trim();
            String uid = sanPham.getUid();

            themVaoGio(maSP, anhSp, coGiamGia, tiLePhanTram, tenSanPham, giaGocSp, giaGiamSp, soLuongSanPham, tongGiaTienSP, uid);
        });
        dialog = builder.create();
        dialog.show();
    }
    private int itemId = 1;
    private void themVaoGio(String ma_sp, String hinh_anh, String co_giam_gia, String ti_le,
                            String ten_sp, String gia_goc, String gia_giam,
                            String soLuongSP, String tongGiaTienSP, String uid) {
        itemId++;
        EasyDB easyDB = EasyDB.init(context, "gioHang")
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
        Cursor cursor = easyDB.getAllData();
        boolean trungNhau = false;
        while (cursor.moveToNext()) {
            // kiểm tra các trường hợp
            String id = cursor.getString(2);
            String id_quan = cursor.getString(11);
            // món ăn đã có sẵn trong giỏ hàng
            if (id.equals(String.valueOf(ma_sp))) {
                new AlertDialog.Builder(context, R.style.CustomAlertDialog)
                        .setMessage("Thêm thất bại, sản phẩm đã có trong giỏ")
                        .setPositiveButton("OK", null)
                        .setCancelable(false)
                        .show();
                trungNhau = true;
                break;
            }
            // giỏ hàng chứa món ăn có quán khác nhau
            if (!id_quan.equals(uid)) {
                new AlertDialog.Builder(context, R.style.CustomAlertDialog)
                        .setTitle("Lỗi")
                        .setMessage("Bạn không thể thêm món ăn của quán khác trong cùng một giỏ được")
                        .setPositiveButton("OK", null)
                        .setCancelable(false)
                        .show();
                trungNhau = true;
                break;
            }
        }
        if (!trungNhau){
            new AlertDialog.Builder(context, R.style.CustomAlertDialog)
                    .setMessage("Bạn muốn thêm món ăn này giỏ hàng?")
                    .setPositiveButton("OK", (dialogInterface, i) -> {
                        Boolean b = easyDB
                                .addData("id_gio", itemId)
                                .addData("ma_sp", ma_sp)
                                .addData("hinh_anh", hinh_anh)
                                .addData("co_giam_gia", co_giam_gia)
                                .addData("ti_le", ti_le)
                                .addData("ten_sp", ten_sp)
                                .addData("gia_goc", gia_goc)
                                .addData("gia_giam", gia_giam)
                                .addData("soLuongSP", soLuongSP)
                                .addData("tongGiaTienSP", tongGiaTienSP)
                                .addData("uid", uid)
                                .doneDataAdding();
                        dialog.dismiss();
                    })
                    .setNegativeButton("Không", null)
                    .setCancelable(false)
                    .show();
        }
    }
    // xóa món ăn theo id
    private void xoa(String id) {
        // hiển thị dialog xác nhận xóa đã
        new AlertDialog.Builder(context, R.style.CustomAlertDialog)
                .setTitle("Xóa món ăn")
                .setMessage("Bạn có muốn xóa món ăn này vĩnh viễn?")
                .setPositiveButton("Xóa", (dialogInterface, i) -> {
                    progressDialog.setMessage("Đang xóa...");
                    progressDialog.show();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SanPham");
                    reference.child(id).removeValue().addOnSuccessListener(unused -> {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(context, R.style.CustomAlertDialog)
                                .setMessage("Xóa thành công")
                                .setPositiveButton("OK", (dialogInterface1, i1) -> dialog.dismiss()).show();
                    }).addOnFailureListener(e -> {
                        new AlertDialog.Builder(context, R.style.CustomAlertDialog)
                                .setMessage("Xóa thất bại, lỗi: " + e.getMessage())
                                .setPositiveButton("OK", null).show();
                    });
                    // không xóa
                }).setNegativeButton("Không", null).show();

    }
    @Override
    public int getItemCount() {
        return sanPhams.size();
    }
    @Override
    public Filter getFilter() {
        if (timKiem == null) {
            timKiem = new TimKiem(AdapterSanPham.this, kqTimKiem);
        }
        return timKiem;
    }
    protected static class HolderSanPham extends RecyclerView.ViewHolder {
        private final TextView tiLe, tenSp, giaGoc, giamCon;

        private final ImageView hinhAnh;
        public HolderSanPham(@NonNull View itemView) {
            super(itemView);
            tiLe = itemView.findViewById(R.id.tiLe);
            tenSp = itemView.findViewById(R.id.tenSp);
            giaGoc = itemView.findViewById(R.id.giaGoc);
            giamCon = itemView.findViewById(R.id.giamCon);
            hinhAnh = itemView.findViewById(R.id.hinhAnh);
        }
    }

}
