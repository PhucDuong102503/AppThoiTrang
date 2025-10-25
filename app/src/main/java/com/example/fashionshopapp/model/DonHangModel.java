package com.example.fashionshopapp.model;

import java.util.List;

public class DonHangModel {
    private boolean success;
    private String message;
    private List<DonHang> result;

    public DonHangModel() {
    }

    public DonHangModel(boolean success, String message, List<DonHang> result) {
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

    public List<DonHang> getResult() {
        return result;
    }

    public void setResult(List<DonHang> result) {
        this.result = result;
    }
}
    