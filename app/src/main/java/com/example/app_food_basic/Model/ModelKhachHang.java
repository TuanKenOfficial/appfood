package com.example.app_food_basic.Model;

public class ModelKhachHang {
    private String uid, email, hoTen, sdt, quocGia,
            tinhTP, quanHuyen, diaChi, timestamp,
            taiKhoan, online, avatar, phiGiao, tenQuan;

    public ModelKhachHang() {
    }

    public ModelKhachHang(String uid, String email, String hoTen, String sdt, String quocGia, String tinhTP, String quanHuyen, String diaChi, String timestamp, String taiKhoan, String online, String avatar, String phiGiao, String tenQuan) {
        this.uid = uid;
        this.email = email;
        this.hoTen = hoTen;
        this.sdt = sdt;
        this.quocGia = quocGia;
        this.tinhTP = tinhTP;
        this.quanHuyen = quanHuyen;
        this.diaChi = diaChi;
        this.timestamp = timestamp;
        this.taiKhoan = taiKhoan;
        this.online = online;
        this.avatar = avatar;
        this.phiGiao = phiGiao;
        this.tenQuan = tenQuan;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getQuocGia() {
        return quocGia;
    }

    public void setQuocGia(String quocGia) {
        this.quocGia = quocGia;
    }

    public String getTinhTP() {
        return tinhTP;
    }

    public void setTinhTP(String tinhTP) {
        this.tinhTP = tinhTP;
    }

    public String getQuanHuyen() {
        return quanHuyen;
    }

    public void setQuanHuyen(String quanHuyen) {
        this.quanHuyen = quanHuyen;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(String taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhiGiao() {
        return phiGiao;
    }

    public void setPhiGiao(String phiGiao) {
        this.phiGiao = phiGiao;
    }

    public String getTenQuan() {
        return tenQuan;
    }

    public void setTenQuan(String tenQuan) {
        this.tenQuan = tenQuan;
    }
}
