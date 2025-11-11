package com.example.fashionshopapp.util;

import com.example.fashionshopapp.model.GioHang;
import com.example.fashionshopapp.model.User;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    //public static final String BASE_URL = "http://192.168.0.101/FashionShop/";
    //public static final String BASE_URL = "http://10.33.50.185/FashionShop/";
    //public static final String BASE_URL = "http://192.168.1.12/FashionShop/";
    //public static final String BASE_URL = "http://10.78.88.240/FashionShop/";
    //public static final String BASE_URL = "http://172.20.10.2/FashionShop/";
    //public static final String BASE_URL = "http://192.168.1.9/FashionShop/";
    public static final String BASE_URL = "http://10.0.2.2/FashionShop/";
    //public static final String BASE_URL = "http://192.168.1.9/FashionShop/";
    //public static final String BASE_URL = "http://phucdq.id.vn/api/";


    public static List<GioHang> manggiohang;
    public static List<GioHang> mangmuahang = new ArrayList<>();
    public static User user_current = new User();

}