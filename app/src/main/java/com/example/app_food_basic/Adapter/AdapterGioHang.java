package com.example.app_food_basic.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_food_basic.Activity.KhachHang.GioHang;
import com.example.app_food_basic.Model.ModelGioHang;
import com.example.app_food_basic.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterGioHang extends RecyclerView.Adapter<AdapterGioHang.HolderGioHang>{
    private Context context;
    private ArrayList<ModelGioHang> gioHangArrayList;

    public AdapterGioHang(Context context, ArrayList<ModelGioHang> gioHangArrayList) {
        this.context = context;
        this.gioHangArrayList = gioHangArrayList;
    }

    @NonNull
    @Override
    public HolderGioHang onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gio_hang, parent, false);
        return new HolderGioHang(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGioHang holder, int position) {
        final ModelGioHang gioHang = gioHangArrayList.get(position);
        // get
        final String hinhAnhSP = gioHang.getHinh_anh();
        final String coGiamGiaKhong = gioHang.getCo_giam_gia();
        final String tileGiamGia = gioHang.getTi_le();
        final String tenSanPham = gioHang.getTen_sp();
        final String giaGocSP = gioHang.getGia_goc();
        final String giaGiamSP = gioHang.getGia_giam();
        final String soLuongTrongGio = gioHang.getSoLuongSP();
        final String tongGiaTienSP = gioHang.getTongGiaTienSP();
        final String maGioHang = gioHang.getId_gioHang();

        // set
        Picasso.get().load(hinhAnhSP).fit().centerCrop()
                .placeholder(R.drawable.img_test)
                .error(R.drawable.img_test)
                .into(holder.hinhAnh);
        holder.tiLe.setText("Giảm " + tileGiamGia + "%");
        holder.tenSp.setText(tenSanPham);
        holder.giaGoc.setText(giaGocSP + "đ");
        holder.giaGiam.setText(giaGiamSP + "đ");
        holder.soLuong.setText(soLuongTrongGio);
        holder.tongGiaTienSp.setText(tongGiaTienSP + "đ");
        holder.xoa.setOnClickListener(view -> xoaSpKhoiGioHang(maGioHang, gioHangArrayList, holder, gioHang));

        if (coGiamGiaKhong.equals("true")) {
            holder.tiLe.setVisibility(View.VISIBLE);
            holder.giaGiam.setVisibility(View.VISIBLE);
            holder.giaGoc.setPaintFlags(holder.giaGoc.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tiLe.setVisibility(View.GONE);
            holder.giaGiam.setVisibility(View.GONE);
        }
    }

    private void xoaSpKhoiGioHang(String maGioHang, ArrayList<ModelGioHang> gioHangArrayList, HolderGioHang holer, ModelGioHang gioHang) {
        double tienHoaDon = ((GioHang)context).tongHoaDon;
        ((GioHang)context).binding.tongDonHang.setText("Tổng cộng: " + String.format("%.0f", tienHoaDon) + "đ");
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
        easyDB.deleteRow(1, maGioHang);
        new AlertDialog.Builder(context, R.style.CustomAlertDialog)
                .setMessage("Bạn có muốn xóa món ăn này khỏi giỏ hàng?")
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    // trừ tiền sau khi xóa sản phẩm khỏi giỏ
                    double tienConLai = tienHoaDon - Double.parseDouble(gioHang.getTongGiaTienSP());
                    ((GioHang)context).tongHoaDon = tienConLai;
                    double parseDouble = Double.parseDouble(String.format("%.0f", tienConLai));
                    ((GioHang)context).binding.tongDonHang.setText("Tổng cộng: " + String.format("%.0f", parseDouble) + "đ");
                    gioHangArrayList.remove(holer.getAdapterPosition());
                    notifyItemChanged(holer.getAdapterPosition());
                    notifyDataSetChanged();
                }).setCancelable(false).show();
    }

    @Override
    public int getItemCount() {
        return gioHangArrayList.size();
    }

    protected static class HolderGioHang extends RecyclerView.ViewHolder {
        private final ImageView hinhAnh;
        private final TextView soLuong, tongGiaTienSp, xoa, giaGoc, giaGiam, tiLe;
        public final TextView tenSp;
        public HolderGioHang(@NonNull View itemView) {
            super(itemView);
            hinhAnh = itemView.findViewById(R.id.hinhAnh);
            tenSp = itemView.findViewById(R.id.tenSp);
            soLuong = itemView.findViewById(R.id.soLuong);
            tiLe = itemView.findViewById(R.id.tiLe);
            tongGiaTienSp = itemView.findViewById(R.id.tongGiaTienSp);
            giaGiam = itemView.findViewById(R.id.giaGiam);
            giaGoc = itemView.findViewById(R.id.giaGoc);
            xoa = itemView.findViewById(R.id.xoa);

        }
    }
}
