package com.example.app_food_basic.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_food_basic.Model.ModelSpDaDat;
import com.example.app_food_basic.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterSpDaDat extends RecyclerView.Adapter<AdapterSpDaDat.HolderSpDaDat>{
    private final Context context;
    private final ArrayList<ModelSpDaDat> spDaDats;


    public AdapterSpDaDat(Context context, ArrayList<ModelSpDaDat> spDaDats) {
        this.context = context;
        this.spDaDats = spDaDats;
    }

    @NonNull
    @Override
    public HolderSpDaDat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mon_an_da_dat, parent, false);
        return new HolderSpDaDat(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderSpDaDat holder, int position) {
        final String anhSanPham, giaSanPham, maSp, soLuong, tenSp, tongGiaTienSP, uid_khachHang;
        final ModelSpDaDat spDaDat = spDaDats.get(position);
        anhSanPham = spDaDat.getAnhSanPham();
        giaSanPham = spDaDat.getGiaSanPham();
        maSp = spDaDat.getMaSp();
        soLuong = spDaDat.getSoLuong();
        tenSp = spDaDat.getTenSp();
        tongGiaTienSP = spDaDat.getTongGiaTienSP();
        uid_khachHang = spDaDat.getUid_khachHang();

        Picasso.get().load(anhSanPham).fit().centerCrop()
                .placeholder(R.drawable.img_test)
                .error(R.drawable.img_test)
                .into(holder.anhSp);
        holder.giaMua.setText(giaSanPham + "đ");
        holder.soLuong.setText(soLuong);
        holder.tenSp.setText(tenSp);
        holder.tongGiaTienSp.setText(tongGiaTienSP + "đ");

    }

    @Override
    public int getItemCount() {
        return spDaDats.size();
    }

    protected static class HolderSpDaDat extends RecyclerView.ViewHolder {
        private final TextView tenSp, giaMua, soLuong, tongGiaTienSp;
        private final ImageView anhSp;
        public HolderSpDaDat(@NonNull View itemView) {
            super(itemView);
            tenSp = itemView.findViewById(R.id.tenSp);
            giaMua = itemView.findViewById(R.id.giaMua);
            soLuong = itemView.findViewById(R.id.soLuong);
            tongGiaTienSp = itemView.findViewById(R.id.tongGiaTienSp);
            anhSp = itemView.findViewById(R.id.hinhAnh);

        }
    }
}
