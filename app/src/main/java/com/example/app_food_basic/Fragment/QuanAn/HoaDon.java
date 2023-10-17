package com.example.app_food_basic.Fragment.QuanAn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.app_food_basic.Adapter.AdapterDonHang;
import com.example.app_food_basic.Model.ModelHoaDon;
import com.example.app_food_basic.databinding.FragmentHoaDonBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HoaDon extends Fragment {
    private FragmentHoaDonBinding binding;
    private FirebaseAuth firebaseAuth;
    private AdapterDonHang adapterDonHang;
    private ArrayList<ModelHoaDon> modelHoaDonArrayList = new ArrayList<>();
    public HoaDon() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHoaDonBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        firebaseAuth = FirebaseAuth.getInstance();
        loadHoaDon();
        return view;
    }

    private void loadHoaDon() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DonHang");
        reference.orderByChild("uid_quanAn").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelHoaDon modelHoaDon = ds.getValue(ModelHoaDon.class);
                    modelHoaDonArrayList.add(modelHoaDon);
                }
                if (modelHoaDonArrayList.size() == 0) {
                    binding.relativeLayout.setVisibility(View.GONE);
                    binding.linearLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.relativeLayout.setVisibility(View.VISIBLE);
                    binding.linearLayout.setVisibility(View.GONE);
                    adapterDonHang = new AdapterDonHang(getActivity(), modelHoaDonArrayList);
                    binding.hoaDonRv.setAdapter(adapterDonHang);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}