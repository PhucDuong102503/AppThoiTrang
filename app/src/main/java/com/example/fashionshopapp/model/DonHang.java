package com.example.fashionshopapp.model;

import java.util.List;

public class DonHang {
    private int id;
    private int user_id;
    private String diachi;
    // Sửa từ long thành double hoặc String để khớp với DECIMAL từ PHP/MySQL
    private String tongtien; // Sử dụng String là an toàn nhất để hiển thị
    private List<GioHang> chitiet;

    // --- Getters and Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUser_id() { return user_id; }
    public void setUser_id(int user_id) { this.user_id = user_id; }
    public String getDiachi() { return diachi; }
    public void setDiachi(String diachi) { this.diachi = diachi; }

    // Sửa lại getter và setter cho tongtien
    public String getTongtien() { return tongtien; }
    public void setTongtien(String tongtien) { this.tongtien = tongtien; }

    public List<GioHang> getChitiet() { return chitiet; }
    public void setChitiet(List<GioHang> chitiet) { this.chitiet = chitiet; }
}
