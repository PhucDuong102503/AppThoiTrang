package com.example.fashionshopapp.model;

/**
 * Model này dùng để hứng các response đơn giản từ server
 * chỉ chứa trạng thái 'success' (true/false) và một 'message' (chuỗi ký tự).
 * Ví dụ: hủy đơn hàng, cập nhật thông tin, ...
 */
public class MessageModel {
    private boolean success;
    private String message;

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
}
