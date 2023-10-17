package com.example.app_food_basic.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_food_basic.Model.ModelHoaDon;
import com.example.app_food_basic.Model.ModelSpDaDat;
import com.example.app_food_basic.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterDonHang extends RecyclerView.Adapter<AdapterDonHang.HolderHoaDon>{
    private final Context context;
    private final ArrayList<ModelHoaDon> modelHoaDonArrayList;
    public AdapterDonHang(Context context, ArrayList<ModelHoaDon> modelHoaDonArrayList) {
        this.context = context;
        this.modelHoaDonArrayList = modelHoaDonArrayList;
    }


    @NonNull
    @Override
    public HolderHoaDon onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_don_hang, parent, false);
        return new HolderHoaDon(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderHoaDon holder, int position) {
        final ModelHoaDon hoaDon = modelHoaDonArrayList.get(position);
        String maHd = "Hóa đơn #" + hoaDon.getMaHd();
        String tenKh = "Tên khách đặt: " + hoaDon.getTenKhachHang();
        String sDt = "SĐT: " + hoaDon.getSdtKhachHang();
        String diaChi = "Địa chỉ giao hàng: " + hoaDon.getDiaChi();
        String tongHoaDon = "Số tiền phải thu: " + hoaDon.getTongHd() + "đ";
        String tenQuanAn = "Tên quán ăn: " + hoaDon.getTenQuan();
        String ngayDat = hoaDon.getNgayDat();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(ngayDat));
        String ngay_dat = DateFormat.format("HH:mm dd/MM/yyyy", calendar).toString();
        holder.ngayDat.setText("Ngày đặt mua: " + ngay_dat);

        holder.maHoaDon.setText(maHd);
        holder.tenKhachDat.setText(tenKh);
        holder.sdtKhach.setText(sDt);
        holder.diaChiKhach.setText(diaChi);
        holder.tongHoaDon.setText(tongHoaDon);
        holder.tenQuanAn.setText(tenQuanAn);

        holder.itemView.setOnClickListener(view -> {
            dialog(hoaDon);
        });



    }


    private void dialog(final ModelHoaDon hoaDon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
        ArrayList<ModelSpDaDat> spDaDatArrayList = new ArrayList<>();
        AlertDialog dialog = builder.create();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_mon_an_da_mua, null);
        RecyclerView rcView = view.findViewById(R.id.rcView);
        TextView test = view.findViewById(R.id.test);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DonHang");
        reference.child(hoaDon.getMaHd()).child("MonAn").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                spDaDatArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelSpDaDat spDaDat = ds.getValue(ModelSpDaDat.class);
                    spDaDatArrayList.add(spDaDat);
                }
                AdapterSpDaDat adapterSpDaDat = new AdapterSpDaDat(context, spDaDatArrayList);
                rcView.setAdapter(adapterSpDaDat);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dialog.setView(view);
        dialog.show();

    }


    @Override
    public int getItemCount() {
        return modelHoaDonArrayList.size();
    }

    protected static class HolderHoaDon extends RecyclerView.ViewHolder {
        private final TextView maHoaDon, tenKhachDat, sdtKhach, diaChiKhach, tongHoaDon, tenQuanAn, ngayDat;
        public HolderHoaDon(@NonNull View itemView) {
            super(itemView);
            maHoaDon = itemView.findViewById(R.id.maHoaDon);
            tenKhachDat = itemView.findViewById(R.id.tenKhachDat);
            sdtKhach = itemView.findViewById(R.id.sdtKhach);
            diaChiKhach = itemView.findViewById(R.id.diaChiKhach);
            tongHoaDon = itemView.findViewById(R.id.tongHoaDon);
            tenQuanAn = itemView.findViewById(R.id.tenQuanAn);
            ngayDat = itemView.findViewById(R.id.ngayDat);
        }
    }
}
