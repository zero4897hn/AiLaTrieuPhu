package com.example.zero.model;

public class NguoiChoi {
    private int thuTu;
    private String ten;
    private double diem;
    private int thuTuCauHoi;
    private String thoiGian;

    public NguoiChoi() {
    }

    public NguoiChoi(int thuTu, String ten, double diem, int thuTuCauHoi, String thoiGian) {
        this.thuTu = thuTu;
        this.ten = ten;
        this.diem = diem;
        this.thuTuCauHoi = thuTuCauHoi;
        this.thoiGian = thoiGian;
    }

    public int getThuTu() {
        return thuTu;
    }

    public void setThuTu(int thuTu) {
        this.thuTu = thuTu;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public double getDiem() {
        return diem;
    }

    public void setDiem(double diem) {
        this.diem = diem;
    }

    public int getThuTuCauHoi() {
        return thuTuCauHoi;
    }

    public void setThuTuCauHoi(int thuTuCauHoi) {
        this.thuTuCauHoi = thuTuCauHoi;
    }

    public String getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(String thoiGian) {
        this.thoiGian = thoiGian;
    }
}
