package com.example.app_food_basic.Model;

import androidx.annotation.NonNull;

public class ModelSanPham {
    private String maSp;
    private String tenSp, moTa, hinhAnh, giaGoc, giaGiam, tiLeGiam, coGiamGia, timestamp, uid, sl;


    public ModelSanPham() {

    }


    public ModelSanPham(@NonNull String maSp, String tenSp, String hinhAnh, String giaGoc, String giaGiam, String coGiamGia, String uid) {
        this.maSp = maSp;
        this.tenSp = tenSp;
        this.hinhAnh = hinhAnh;
        this.giaGoc = giaGoc;
        this.giaGiam = giaGiam;
        this.coGiamGia = coGiamGia;
        this.uid = uid;
    }

    public String getMaSp() {
        return maSp;
    }

    public void setMaSp(String maSp) {
        this.maSp = maSp;
    }

    public String getTenSp() {
        return tenSp;
    }

    public void setTenSp(String tenSp) {
        this.tenSp = tenSp;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public String getGiaGoc() {
        return giaGoc;
    }

    public void setGiaGoc(String giaGoc) {
        this.giaGoc = giaGoc;
    }

    public String getGiaGiam() {
        return giaGiam;
    }

    public void setGiaGiam(String giaGiam) {
        this.giaGiam = giaGiam;
    }

    public String getTiLeGiam() {
        return tiLeGiam;
    }

    public void setTiLeGiam(String tiLeGiam) {
        this.tiLeGiam = tiLeGiam;
    }

    public String getCoGiamGia() {
        return coGiamGia;
    }

    public void setCoGiamGia(String coGiamGia) {
        this.coGiamGia = coGiamGia;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSl() {
        return sl;
    }

    public void setSl(String sl) {
        this.sl = sl;
    }
}
