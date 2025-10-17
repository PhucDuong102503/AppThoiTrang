package com.example.fashionshopapp.retrofit;

import com.example.fashionshopapp.model.LoaiSpModel;
import com.example.fashionshopapp.model.SanPhamMoiModel;


import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiBanHang {
    @GET("getloaisp.php")
    Observable<LoaiSpModel> getLoaiSp();

    @GET("getsanphammoinhat.php")
    Observable<SanPhamMoiModel> getSpMoi();

    @POST("getsanpham.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> getSanPham(
        @Field("page") int page,
        @Field("idloaisanpham") int idloaisanpham
    );
}

