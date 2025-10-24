package com.example.fashionshopapp.model;

import androidx.room.Dao;
import androidx.room.Insert;import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface GioHangDAO {

    // THAY ĐỔI 1: Tìm sản phẩm bằng cả idsp và sizeId
    @Query("SELECT * FROM giohang WHERE idsp = :idsp AND sizeId = :sizeId LIMIT 1")
    Single<GioHang> getProductByPrimaryKey(int idsp, int sizeId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrReplace(GioHang gioHang);

    @Query("SELECT * FROM giohang")
    Single<List<GioHang>> getAllCartItems();

    // THAY ĐỔI 2: Xóa sản phẩm bằng cả idsp và sizeId
    @Query("DELETE FROM giohang WHERE idsp = :idsp AND sizeId = :sizeId")
    Completable deleteByPrimaryKey(int idsp, int sizeId);

    @Query("DELETE FROM giohang")
    Completable deleteAllItems();
}
