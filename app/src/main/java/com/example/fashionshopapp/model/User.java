package com.example.fashionshopapp.model;

public class User {
    int id;
    String hoten;
    String tendangnhap;
    String matkhau;
    String sodienthoai;
    String email;
    String diachi;

    public User() {
    }

    public User(int id, String hoten, String tendangnhap, String matkhau, String sodienthoai, String email, String diachi) {
        this.id = id;
        this.hoten = hoten;
        this.tendangnhap = tendangnhap;
        this.matkhau = matkhau;
        this.sodienthoai = sodienthoai;
        this.email = email;
        this.diachi = diachi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public String getTendangnhap() {
        return tendangnhap;
    }

    public void setTendangnhap(String tendangnhap) {
        this.tendangnhap = tendangnhap;
    }

    public String getMatkhau() {
        return matkhau;
    }

    public void setMatkhau(String matkhau) {
        this.matkhau = matkhau;
    }

    public String getSodienthoai() {
        return sodienthoai;
    }

    public void setSodienthoai(String sodienthoai) {
        this.sodienthoai = sodienthoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }
}
