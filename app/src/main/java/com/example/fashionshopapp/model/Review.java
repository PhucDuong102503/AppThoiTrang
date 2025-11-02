package com.example.fashionshopapp.model;

public class Review {
    private int id;
    private int user_id;
    private int sanpham_id;
    private int donhang_id;
    private int sao;
    private String binhluan;
    private String hinhanh_danhgia;
    private String ngaydanhgia;

    // Thêm 2 trường này để lấy từ bảng `user`
    private String hoten;
    private String user_avatar;

    // Getters
    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getSanpham_id() {
        return sanpham_id;
    }

    public int getDonhang_id() {
        return donhang_id;
    }

    public int getSao() {
        return sao;
    }

    public String getBinhluan() {
        return binhluan;
    }

    public String getHinhanh_danhgia() {
        return hinhanh_danhgia;
    }

    public String getNgaydanhgia() {
        return ngaydanhgia;
    }

    public String getHoten() {
        return hoten;
    }

    public String getUser_avatar() {
        return user_avatar;
    }
}
