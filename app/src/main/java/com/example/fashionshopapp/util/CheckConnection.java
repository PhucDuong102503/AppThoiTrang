//package com.example.fashionshopapp.util;
//
//import android.content.Context;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.widget.Toast;
//
//public class CheckConnection {
//    public static boolean isConnected(Context context) { // Kiểm tra kết nối mạng
//        boolean wifi = false;
//        boolean mobile = false;
//        ConnectivityManager cm = (ConnectivityManager)
//                context.getSystemService(Context.CONNECTIVITY_SERVICE); // Lấy danh sách kết nối mạng
//        NetworkInfo[] networkInfo = cm.getAllNetworkInfo();
//        for (NetworkInfo info : networkInfo ){
//            if (info.getTypeName().equals("WIFI")){
//                if (info.isConnected())
//                    wifi = true;
//            }
//            if (info.getTypeName().equals("MOBILE")){
//                if (info.isConnected()){
//                    mobile = true;
//                }
//            }
//        }
//        return wifi|| mobile;
//    }
//    public static void ShowToastLong(Context context, String tb){
//        Toast.makeText(context, tb, Toast.LENGTH_LONG).show();
//    }
//}
