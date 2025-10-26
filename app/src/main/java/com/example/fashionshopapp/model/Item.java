package com.example.fashionshopapp.model;

import java.io.Serializable;

// Model này chỉ dùng để chứa thông tin của một sản phẩm trong chi tiết đơn hàng
public class Item implements Serializable {
    // Các trường này phải khớp với JSON trả về từ file xemdonhang.php
    private String tensanpham;
    private String hinhanhsanpham;
    private int soluong;
    private double gia;
    private String tensize;

    // --- Getters và Setters ---

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
