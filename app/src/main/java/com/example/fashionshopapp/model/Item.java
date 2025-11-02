package com.example.fashionshopapp.model;

import java.io.Serializable;

// Model này chỉ dùng để chứa thông tin của một sản phẩm trong chi tiết đơn hàng
public class Item implements Serializable {
    // Các trường này phải khớp với JSON trả về từ file xemdonhang.php

    // ⭐⭐⭐ THÊM TRƯỜNG NÀY VÀO ⭐⭐⭐
    private int sanpham_id;

    private String tensanpham;
    private String hinhanhsanpham;
    private int soluong;
    private double gia;
    private String tensize;


    // --- Getters và Setters ---

    // ⭐⭐⭐ THÊM CÁC HÀM GETTER/SETTER NÀY VÀO ⭐⭐⭐
    public int getSanpham_id() {
        return sanpham_id;
    }

    public void setSanpham_id(int sanpham_id) {
        this.sanpham_id = sanpham_id;
    }


    // --- Các hàm cũ giữ nguyên ---
    public String getTensanpham() {
        return tensanpham;
    }

    public void setTensanpham(String tensanpham) {
        this.tensanpham = tensanpham;
    }

    public String getHinhanhsanpham() {
        return hinhanhsanpham;
    }

    public void setHinhanhsanpham(String hinhanhsanpham) {
        this.hinhanhsanpham = hinhanhsanpham;
    }

    public int getSoluong() {
        return soluong;
    }

    public void setSoluong(int soluong) {
        this.soluong = soluong;
    }

    public double getGia() {
        return gia;
    }

    public void setGia(double gia) {
        this.gia = gia;
    }

    public String getTensize() {
        return tensize;
    }

    public void setTensize(String tensize) {
        this.tensize = tensize;
    }
}
