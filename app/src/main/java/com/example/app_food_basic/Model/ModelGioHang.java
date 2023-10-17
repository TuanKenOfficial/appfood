package com.example.app_food_basic.Model;

public class ModelGioHang {
    private String id_gioHang, ma_sp, hinh_anh, co_giam_gia, ti_le, ten_sp, gia_goc, gia_giam, soLuongSP, tongGiaTienSP, uid;
    public ModelGioHang() {
    }

    public ModelGioHang(String id_gioHang, String ma_sp, String hinh_anh, String co_giam_gia, String ti_le, String ten_sp, String gia_goc, String gia_giam, String soLuongSP, String tongGiaTienSP, String uid) {
        this.id_gioHang = id_gioHang;
        this.ma_sp = ma_sp;
        this.hinh_anh = hinh_anh;
        this.co_giam_gia = co_giam_gia;
        this.ti_le = ti_le;
        this.ten_sp = ten_sp;
        this.gia_goc = gia_goc;
        this.gia_giam = gia_giam;
        this.soLuongSP = soLuongSP;
        this.tongGiaTienSP = tongGiaTienSP;
        this.uid = uid;
    }

    public String getId_gioHang() {
        return id_gioHang;
    }

    public void setId_gioHang(String id_gioHang) {
        this.id_gioHang = id_gioHang;
    }

    public String getMa_sp() {
        return ma_sp;
    }

    public void setMa_sp(String ma_sp) {
        this.ma_sp = ma_sp;
    }

    public String getHinh_anh() {
        return hinh_anh;
    }

    public void setHinh_anh(String hinh_anh) {
        this.hinh_anh = hinh_anh;
    }

    public String getCo_giam_gia() {
        return co_giam_gia;
    }

    public void setCo_giam_gia(String co_giam_gia) {
        this.co_giam_gia = co_giam_gia;
    }

    public String getTi_le() {
        return ti_le;
    }

    public void setTi_le(String ti_le) {
        this.ti_le = ti_le;
    }

    public String getTen_sp() {
        return ten_sp;
    }

    public void setTen_sp(String ten_sp) {
        this.ten_sp = ten_sp;
    }

    public String getGia_goc() {
        return gia_goc;
    }

    public void setGia_goc(String gia_goc) {
        this.gia_goc = gia_goc;
    }

    public String getGia_giam() {
        return gia_giam;
    }

    public void setGia_giam(String gia_giam) {
        this.gia_giam = gia_giam;
    }

    public String getSoLuongSP() {
        return soLuongSP;
    }

    public void setSoLuongSP(String soLuongSP) {
        this.soLuongSP = soLuongSP;
    }

    public String getTongGiaTienSP() {
        return tongGiaTienSP;
    }

    public void setTongGiaTienSP(String tongGiaTienSP) {
        this.tongGiaTienSP = tongGiaTienSP;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
