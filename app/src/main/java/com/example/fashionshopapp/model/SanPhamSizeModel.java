package com.example.fashionshopapp.model;

import java.util.List;

public class SanPhamSizeModel {
    boolean success;
    String message;
    List<SanPhamSize> result;

    public SanPhamSizeModel() {
    }

    public SanPhamSizeModel(boolean success, String message, List<SanPhamSize> result) {
        this.success = success;
        this.message = message;
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SanPhamSize> getResult() {
        return result;
    }

    public void setResult(List<SanPhamSize> result) {
        this.result = result;
    }
}
