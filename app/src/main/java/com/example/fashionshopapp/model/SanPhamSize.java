package com.example.fashionshopapp.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class SanPhamSize implements Serializable {
    int size_id;
    String tensize;
    int soluong;

    public SanPhamSize() {
    }

    public SanPhamSize(int size_id, String tensize, int soluong) {
        this.size_id = size_id;
        this.tensize = tensize;
        this.soluong = soluong;
    }

    public int getSize_id() {
        return size_id;
    }

    public void setSize_id(int size_id) {
        this.size_id = size_id;
    }

    public String getTensize() {
        return tensize;
    }

    public void setTensize(String tensize) {
        this.tensize = tensize;
    }

    public int getSoluong() {
        return soluong;
    }

    public void setSoluong(int soluong) {
        this.soluong = soluong;
    }

    // <<< BƯỚC QUAN TRỌNG NHẤT NẰM Ở ĐÂY >>>
    // Ghi đè phương thức toString() để Spinner biết phải hiển thị chuỗi nào.
    @NonNull
    @Override
    public String toString() {
        // Bạn có thể tùy chỉnh chuỗi hiển thị ở đây
        // Ví dụ 1: "Size: M"
        // return "Size: " + tensize;

        // Ví dụ 2: "M"
        // return tensize;

        // Ví dụ 3 (đầy đủ thông tin): "Size: M - Còn: 20"
        return "Size: " + tensize + " - (Còn " + soluong + ")";
    }
}
