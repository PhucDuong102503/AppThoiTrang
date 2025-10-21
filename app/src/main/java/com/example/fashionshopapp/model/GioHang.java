package com.example.fashionshopapp.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// @Entity đánh dấu đây là một bảng trong database.
// tableName là tên của bảng.
@Entity(tableName = "giohang")
public class GioHang {

    // @PrimaryKey đánh dấu đây là cột khóa chính.
    @PrimaryKey
    private int id; // ID này sẽ là sự kết hợp của id sản phẩm và id size.

    private String tensp;
    private long giasp;
    private String hinhanh;
    private int soluong; // Số lượng người dùng muốn mua
    private String size;   // Size người dùng đã chọn (ví dụ: "M")

    // --- BẮT BUỘC PHẢI CÓ GETTER VÀ SETTER CHO TẤT CẢ CÁC TRƯỜNG ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTensp() {
        return tensp;
    }

    public void setTensp(String tensp) {
        this.tensp = tensp;
    }

    public long getGiasp() {
        return giasp;
    }

    public void setGiasp(long giasp) {
        this.giasp = giasp;
    }

    public String getHinhanh() {
        return hinhanh;
    }

    public void setHinhanh(String hinhanh) {
        this.hinhanh = hinhanh;
    }

    public int getSoluong() {
        return soluong;
    }

    public void setSoluong(int soluong) {
        this.soluong = soluong;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
