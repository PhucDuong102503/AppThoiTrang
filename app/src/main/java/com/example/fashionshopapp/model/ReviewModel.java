package com.example.fashionshopapp.model;

import java.util.List;

public class ReviewModel {
    private boolean success;
    private String message;
    private List<Review> result;

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Review> getResult() {
        return result;
    }
}
