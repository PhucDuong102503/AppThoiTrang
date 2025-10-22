package com.example.fashionshopapp.retrofit;

import com.example.fashionshopapp.model.LoaiSpModel;
import com.example.fashionshopapp.model.SanPhamMoiModel;
import com.example.fashionshopapp.model.SanPhamSizeModel;
import com.example.fashionshopapp.model.UserModel;


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
        @Field("idloaisanpham") int idloaisanpham);

    @FormUrlEncoded
    @POST("getSanPhamSize.php")
    Observable<SanPhamSizeModel> getSanPhamSize(
            @Field("sanpham_id") int sanpham_id
    );

    @POST("dangki.php")
    @FormUrlEncoded
    Observable<UserModel> dangki(
            @Field("tendangnhap") String tendangnhap,
            @Field("hoten") String hoten,
            @Field("matkhau") String matkhau,
            @Field("sodienthoai") String sodienthoai,
            @Field("email") String email,
            @Field("diachi") String diachi);

    @POST("dangnhap.php")
    @FormUrlEncoded
    Observable<UserModel> dangNhap(
            @Field("tendangnhap") String tendangnhap,
            @Field("matkhau") String matkhau);
}

