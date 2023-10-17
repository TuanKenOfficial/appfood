package com.example.app_food_basic.Model;

public class ModelSpDaDat {
    String anhSanPham, giaSanPham, maSp, soLuong, tenSp, tongGiaTienSP, uid_khachHang;

    public ModelSpDaDat(String anhSanPham, String giaSanPham, String maSp, String soLuong, String tenSp, String tongGiaTienSP, String uid_khachHang) {
        this.anhSanPham = anhSanPham;
        this.giaSanPham = giaSanPham;
        this.maSp = maSp;
        this.soLuong = soLuong;
        this.tenSp = tenSp;
        this.tongGiaTienSP = tongGiaTienSP;
        this.uid_khachHang = uid_khachHang;
    }

    public ModelSpDaDat() {
    }

    public String getAnhSanPham() {
        return anhSanPham;
    }

    public void setAnhSanPham(String anhSanPham) {
        this.anhSanPham = anhSanPham;
    }

    public String getGiaSanPham() {
        return giaSanPham;
    }

    public void setGiaSanPham(String giaSanPham) {
        this.giaSanPham = giaSanPham;
    }

    public String getMaSp() {
        return maSp;
    }

    public void setMaSp(String maSp) {
        this.maSp = maSp;
    }

    public String getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(String soLuong) {
        this.soLuong = soLuong;
    }

    public String getTenSp() {
        return tenSp;
    }

    public void setTenSp(String tenSp) {
        this.tenSp = tenSp;
    }

    public String getTongGiaTienSP() {
        return tongGiaTienSP;
    }

    public void setTongGiaTienSP(String tongGiaTienSP) {
        this.tongGiaTienSP = tongGiaTienSP;
    }

    public String getUid_khachHang() {
        return uid_khachHang;
    }

    public void setUid_khachHang(String uid_khachHang) {
        this.uid_khachHang = uid_khachHang;
    }
}
