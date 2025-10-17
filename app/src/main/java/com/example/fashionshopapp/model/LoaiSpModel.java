package com.example.fashionshopapp.model;

import java.util.List;

public class LoaiSpModel {
    boolean success;
    String message;
    List<Loaisp> result;

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

    public List<Loaisp> getResult() {
        return result;
    }

    public void setResult(List<Loaisp> result) {
        this.result = result;
    }
}
