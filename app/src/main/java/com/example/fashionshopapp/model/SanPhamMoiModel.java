package com.example.fashionshopapp.model;

import java.util.List;

public class SanPhamMoiModel {
    boolean success;
    String message;
    List<SanPhamMoi> result;

    public SanPhamMoiModel(boolean success, String message, List<SanPhamMoi> result) {
        this.success = success;
        this.message = message;
        this.result = result;
    }

    public SanPhamMoiModel() {
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

    public List<SanPhamMoi> getResult() {
        return result;
    }

    public void setResult(List<SanPhamMoi> result) {
        this.result = result;
    }
}
