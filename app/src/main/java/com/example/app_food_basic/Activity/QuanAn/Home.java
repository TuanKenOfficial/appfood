package com.example.app_food_basic.Activity.QuanAn;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.app_food_basic.Adapter.AdapterViewPager2;
import com.example.app_food_basic.Fragment.QuanAn.FragmentTrungGian;
import com.example.app_food_basic.Fragment.QuanAn.ThongTinQuan;
import com.example.app_food_basic.Fragment.QuanAn.TrangChu;
import com.example.app_food_basic.R;
import com.example.app_food_basic.databinding.ActivityHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class Home extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Fragment> fragments = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        firebaseAuth = FirebaseAuth.getInstance();
        // load thông tin cá nhân
        loadUser();
        // load fragment cho activity
        loadFragments();
        // set trang fragment đầu tiên để hiển thị trong activty
        binding.container.setCurrentItem(0);
        binding.menu.setItemSelected(R.id.trangChu, true);

    }

    private void loadFragments() {
        // nạp fragment vào viewpager tại activity hiện hành
        fragments.add(new TrangChu());
        fragments.add(new FragmentTrungGian());
        fragments.add(new ThongTinQuan());
        AdapterViewPager2 adapterViewPager2 = new AdapterViewPager2(this, fragments);
        binding.container.setAdapter(adapterViewPager2);
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
    private void loadUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("TaiKhoan");
        reference.child(Objects.requireNonNull(firebaseAuth.getUid()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // lấy dl từ firebase để hiển thị lên view
                        String tenQuan = "" + snapshot.child("tenQuan").getValue();
                        String sdt = "" + snapshot.child("sdt").getValue();
                        String tinhTP = "" + snapshot.child("tinhTP").getValue();
                        String quanHuyen = "" + snapshot.child("quanHuyen").getValue();
                        String diaChi = "" + snapshot.child("diaChi").getValue();
                        String avatar = "" + snapshot.child("avatar").getValue();
                        String diaChiTongHop = tinhTP + ", " + quanHuyen + ", " + diaChi;
                        binding.tenQuan.setText(tenQuan + " | " + sdt);
                        binding.diaChi.setText(diaChiTongHop);
                        try {
                            Picasso.get().load(avatar).fit().centerCrop()
                                    .placeholder(R.drawable.shopivhd)
                                    .error(R.drawable.shopivhd)
                                    .into(binding.avatar);
                        } catch (Exception e) {
                            binding.avatar.setImageResource(R.drawable.shopivhd);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}