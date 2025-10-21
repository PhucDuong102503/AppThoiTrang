package com.example.fashionshopapp.model;

import android.content.Context; // << ĐẢM BẢO CÓ IMPORT NÀY
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
@Database(entities = {GioHang.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract GioHangDAO gioHangDAO();

    private static volatile AppDatabase instance; // Dùng volatile để đảm bảo an toàn trên đa luồng

    // <<< HÀM getInstance() ĐÚNG PHẢI NHẬN VÀO CONTEXT >>>
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            // Dùng context.getApplicationContext() để tránh rò rỉ bộ nhớ từ Activity
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "FashionShopDB") // Tên file database của bạn
                    .fallbackToDestructiveMigration() // Chính sách khi nâng cấp version
                    .build();
        }
        return instance;
    }
}
