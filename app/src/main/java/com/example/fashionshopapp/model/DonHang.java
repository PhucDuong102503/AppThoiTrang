package com.example.fashionshopapp.model;

import java.io.Serializable;
import java.util.List;

// QUAN TRỌNG: Đảm bảo import đúng lớp Item mà bạn đã tạo
import com.example.fashionshopapp.model.Item;

// Model này dùng để hứng dữ liệu của một đơn hàng từ file xemdonhang.php
public class DonHang implements Serializable {

    // Các trường này phải khớp với tên cột trong bảng `donhang` và JSON trả về
    private int id;
    private int user_id;
    private String diachi;
    private String sodienthoai;
    private String email;
    private String tongtien;
    private String trangthai;
    private String ngaydathang;

    // ⭐ TRƯỜNG NÀY PHẢI SỬ DỤNG ĐÚNG LỚP `Item` MÀ BẠN ĐÃ TẠO
    private List<Item> items;

    // --- Getters và Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
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

    public String getTongtien() {
        return tongtien;
    }

    public void setTongtien(String tongtien) {
        this.tongtien = tongtien;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }

    public String getNgaydathang() {
        return ngaydathang;
    }

    public void setNgaydathang(String ngaydathang) {
        this.ngaydathang = ngaydathang;
    }

    // ⭐ PHƯƠNG THỨC NÀY SẼ TRẢ VỀ ĐÚNG KIỂU DỮ LIỆU
    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
