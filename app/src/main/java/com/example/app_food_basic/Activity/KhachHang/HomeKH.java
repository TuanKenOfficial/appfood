package com.example.app_food_basic.Activity.KhachHang;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.app_food_basic.Adapter.AdapterViewPager2;
import com.example.app_food_basic.Fragment.KhachHang.HoaDonKH;
import com.example.app_food_basic.Fragment.KhachHang.ThongTinKH;
import com.example.app_food_basic.Fragment.KhachHang.TrangChuKH;
import com.example.app_food_basic.R;
import com.example.app_food_basic.databinding.ActivityHomeKhBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import p32929.androideasysql_library.EasyDB;

public class HomeKH extends AppCompatActivity {
    private ActivityHomeKhBinding binding;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private EasyDB easyDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeKhBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        firebaseAuth = FirebaseAuth.getInstance();
        binding.container.setCurrentItem(0);
        binding.menu.setItemSelected(R.id.trangChu, true);
        // load thông tin cá nhân
        loadUser();
        // load fragment cho activity
        loadFragments();
        // set trang fragment đầu tiên để hiển thị trong activty

    }
    private void loadUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("TaiKhoan");
        reference.child(Objects.requireNonNull(firebaseAuth.getUid())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // lấy dl từ firebase để hiển thị lên view
                String hoTen = "" + snapshot.child("hoTen").getValue();
                String tinhTP = "" + snapshot.child("tinhTP").getValue();
                String quanHuyen = "" + snapshot.child("quanHuyen").getValue();
                String diaChi = "" + snapshot.child("diaChi").getValue();
                String sdt = "" + snapshot.child("sdt").getValue();
                String avatar = "" + snapshot.child("avatar").getValue();

                binding.tenKH.setText(hoTen + " | " + sdt);
                binding.diaChi.setText(tinhTP + ", " + quanHuyen + ", " + diaChi);
                try {
                    Picasso.get().load(avatar).fit().centerCrop()
                            .placeholder(R.drawable.fork)
                            .error(R.drawable.fork)
                            .into(binding.avatar);
                } catch (Exception e) {
                    binding.avatar.setImageResource(R.drawable.fork);
                }
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("diaChi", binding.diaChi.getText().toString().trim());
                editor.putString("tenToi", hoTen);
                editor.putString("sdtToi", sdt);
                editor.apply();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadFragments() {
        // nạp fragment vào viewpager tại activity hiện hành
        fragments.add(new TrangChuKH());
        fragments.add(new HoaDonKH());
        fragments.add(new ThongTinKH());
        AdapterViewPager2 adapter = new AdapterViewPager2(this, fragments);
        binding.container.setAdapter(adapter);
        // nút button navigation sẽ thay đổi theo trang
        binding.container.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        binding.menu.setItemSelected(R.id.trangChu, true);
                        break;
                    case 1:
                        binding.menu.setItemSelected(R.id.ds_hoaDon, true);
                        break;
                    case 2:
                        binding.menu.setItemSelected(R.id.ttCaNhan, true);
                        break;
                }
                super.onPageSelected(position);
            }
        });
        // nạp viewpager2 vào bottom navigation
        binding.menu.setOnItemSelectedListener(i -> {
            switch (i) {
                case R.id.trangChu:
                    binding.container.setCurrentItem(0);
                    break;
                case R.id.ds_hoaDon:
                    binding.container.setCurrentItem(1);
                    break;
                case R.id.ttCaNhan:
                    binding.container.setCurrentItem(2);
                    break;
            }
        });
    }
}