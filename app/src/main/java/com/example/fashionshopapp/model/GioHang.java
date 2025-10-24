package com.example.fashionshopapp.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

// THAY ĐỔI 1: Định nghĩa lại khóa chính là sự kết hợp của idsp và sizeId
@Entity(tableName = "giohang", primaryKeys = {"idsp", "sizeId"})
public class GioHang implements Serializable {

    // THAY ĐỔI 2: Đánh dấu @NonNull để đảm bảo cột này không bao giờ null
    @NonNull
    @SerializedName("sanpham_id")
    private int idsp;

    private String tensp;

    @SerializedName("gia")
    private long giasp;

    private String hinhanh;

    @SerializedName("soluong")
    private int soluong;

    @NonNull
    @SerializedName("size_id")
    private int sizeId;

    private String size;

    // --- Toàn bộ phần Getters và Setters giữ nguyên, không cần thay đổi ---
    public int getIdsp() {
        return idsp;
    }

    public void setIdsp(int idsp) {
        this.idsp = idsp;
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

    public int getSizeId() {
        return sizeId;
    }

    public void setSizeId(int sizeId) {
        this.sizeId = sizeId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
