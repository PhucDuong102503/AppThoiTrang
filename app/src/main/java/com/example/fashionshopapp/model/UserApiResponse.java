package com.example.fashionshopapp.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UserApiResponse {
    private boolean success;
    private String message;

    @SerializedName("admins") // Khớp với key 'admins' trong JSON
    private List<User> userList;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<User> getUserList() { return userList; }
}
