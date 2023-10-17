package com.example.app_food_basic.Model;

public class ModelHoaDon {
    private String maHd, ngayDat, tongHd, uid_khachHang, tenKhachHang, sdtKhachHang, uid_quanAn, tenQuan, diaChi;

    public ModelHoaDon() {
    }

    public ModelHoaDon(String maHd, String ngayDat, String tongHd, String uid_khachHang, String tenKhachHang, String sdtKhachHang, String uid_quanAn, String tenQuan, String diaChi) {
        this.maHd = maHd;
        this.ngayDat = ngayDat;
        this.tongHd = tongHd;
        this.uid_khachHang = uid_khachHang;
        this.tenKhachHang = tenKhachHang;
        this.sdtKhachHang = sdtKhachHang;
        this.uid_quanAn = uid_quanAn;
        this.tenQuan = tenQuan;
        this.diaChi = diaChi;
    }

    public String getMaHd() {
        return maHd;
    }

    public void setMaHd(String maHd) {
        this.maHd = maHd;
    }

    public String getNgayDat() {
        return ngayDat;
    }

    public void setNgayDat(String ngayDat) {
        this.ngayDat = ngayDat;
    }

    public String getTongHd() {
        return tongHd;
    }

    public void setTongHd(String tongHd) {
        this.tongHd = tongHd;
    }

    public String getUid_khachHang() {
        return uid_khachHang;
    }

    public void setUid_khachHang(String uid_khachHang) {
        this.uid_khachHang = uid_khachHang;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public String getSdtKhachHang() {
        return sdtKhachHang;
    }

    public void setSdtKhachHang(String sdtKhachHang) {
        this.sdtKhachHang = sdtKhachHang;
    }

    public String getUid_quanAn() {
        return uid_quanAn;
    }

    public void setUid_quanAn(String uid_quanAn) {
        this.uid_quanAn = uid_quanAn;
    }

    public String getTenQuan() {
        return tenQuan;
    }

    public void setTenQuan(String tenQuan) {
        this.tenQuan = tenQuan;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }
}
