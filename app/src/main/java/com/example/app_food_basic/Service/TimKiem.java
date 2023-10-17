package com.example.app_food_basic.Service;

import android.annotation.SuppressLint;
import android.widget.Filter;

import com.example.app_food_basic.Adapter.AdapterSanPham;
import com.example.app_food_basic.Model.ModelSanPham;

import java.util.ArrayList;

public class TimKiem extends Filter {

    private final AdapterSanPham adapter;
    private final ArrayList<ModelSanPham> sanPhams;

    public TimKiem(AdapterSanPham adapter, ArrayList<ModelSanPham> filterList) {
        this.adapter = adapter;
        this.sanPhams = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length() > 0){

            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelSanPham> result = new ArrayList<>();
            for (int i=0; i<sanPhams.size(); i++){
                if (sanPhams.get(i).getTenSp().toUpperCase().contains(constraint)){
                    if (sanPhams.get(i).getCoGiamGia().equals("true")) {

                    }
                    result.add(sanPhams.get(i));
                }
            }
            results.count = result.size();
            results.values = result;
        }
        else {
            results.count = sanPhams.size();
            results.values = sanPhams;
        }
        return results;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.sanPhams = (ArrayList<ModelSanPham>) results.values;
        adapter.notifyDataSetChanged();
    }
}
