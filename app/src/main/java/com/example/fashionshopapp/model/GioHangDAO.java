package com.example.fashionshopapp.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

// @Dao đánh dấu đây là một lớp Data Access Object
@Dao
public interface GioHangDAO {

    // Lấy một sản phẩm trong giỏ hàng bằng ID của nó.
    // Trả về onSuccess nếu tìm thấy, onError nếu không tìm thấy.
    @Query("SELECT * FROM giohang WHERE id = :id LIMIT 1")
    Single<GioHang> getProductById(int id);

    // Chèn hoặc thay thế một sản phẩm.
    // OnConflictStrategy.REPLACE: Nếu chèn một sản phẩm có 'id' đã tồn tại, nó sẽ thay thế sản phẩm cũ.
    // Điều này giúp ta dùng chung hàm này cho cả "thêm mới" và "cập nhật".
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrReplace(GioHang gioHang);

    // Lấy toàn bộ sản phẩm trong giỏ hàng.
    @Query("SELECT * FROM giohang")
    Single<List<GioHang>> getAllCartItems();

    // Xóa một sản phẩm khỏi giỏ hàng bằng ID của nó.
    @Query("DELETE FROM giohang WHERE id = :id")
    Completable deleteById(int id);
}
