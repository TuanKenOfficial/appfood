package com.example.app_food_basic.Fragment.QuanAn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.app_food_basic.Adapter.AdapterViewPager2;
import com.example.app_food_basic.databinding.FragmentTrungGianBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class FragmentTrungGian extends Fragment {
    // dùng để chứa 2 fragment khác
    private FragmentTrungGianBinding binding;
    private AdapterViewPager2 adapterViewPager2;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    public FragmentTrungGian() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTrungGianBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        loadFragment();
        return view;
    }

    private void loadFragment() {
        fragments.clear();
        fragments.add(new HoaDon());
        fragments.add(new DoanhThu());
        adapterViewPager2 = new AdapterViewPager2(requireActivity(), fragments);
        binding.viewPager.setAdapter(adapterViewPager2);
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Hóa đơn");
                        break;
                    case 1:
                        tab.setText("Doanh thu");
                        break;
                }
            }
        }).attach();
    }
}