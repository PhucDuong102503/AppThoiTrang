package com.example.fashionshopapp.retrofit;

import com.example.fashionshopapp.model.DonHangModel;
import com.example.fashionshopapp.model.LoaiSpModel;
import com.example.fashionshopapp.model.MessageModel;
import com.example.fashionshopapp.model.SanPhamMoiModel;
import com.example.fashionshopapp.model.SanPhamSizeModel;
import com.example.fashionshopapp.model.UserModel;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

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

    @POST("timkiem.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> search(
            @Field("search") String search
    );

    @POST("getSanPhamSize.php")
    @FormUrlEncoded
    Observable<SanPhamSizeModel> getSanPhamSize(@Field("sanpham_id") int sanpham_id);

    // --- API ĐĂNG NHẬP ---
    @POST("dangnhap.php")
    @FormUrlEncoded
    Observable<UserModel> dangNhap(
            @Field("tendangnhap") String tendangnhap,
            @Field("matkhau") String matkhau
    );

    // --- API LUỒNG QUÊN MẬT KHẨU (DÙNG JSON) ---
    @Headers("Content-Type: application/json")
    @POST("forgot_password.php")
    Observable<UserModel> sendResetPasswordOtp(@Body RequestBody body); // <<< HÀM ĐÚNG LÀ HÀM NÀY

    @Headers("Content-Type: application/json")
    @POST("reset_password.php")
    Observable<UserModel> verifyAndResetPassword(@Body RequestBody body);

    // --- API LUỒNG ĐĂNG KÝ BẰNG OTP (DÙNG FORMenCODED) ---
    @POST("send_register_otp.php")
    @FormUrlEncoded
    Observable<UserModel> sendRegisterOtp(
            @Field("email") String email,
            @Field("tendangnhap") String username
    );

    @POST("dangki_final.php")
    @FormUrlEncoded
    Observable<UserModel> dangKiFinal(
            @Field("hoten") String hoten,
            @Field("tendangnhap") String username,
            @Field("email") String email,
            @Field("sodienthoai") String sdt,
            @Field("diachi") String diachi,
            @Field("matkhau") String password,
            @Field("otp") String otp
    );

//    @POST("donhang.php")
//    @Headers("Content-Type: application/json")
//    Observable<UserModel> datHang(@Body RequestBody body);
// HÀM MỚI - KHẮC PHỤC LỖI
@POST("donhang.php")
@FormUrlEncoded
Observable<MessageModel> datHang(
        @Field("user_id") int user_id,
        @Field("diachi") String diachi,
        @Field("sodienthoai") String sodienthoai,
        @Field("email") String email,
        @Field("soluong") int soluong,
        @Field("tongtien") String tongtien,
        @Field("chitiet") String chitiet
);


    @POST("get_order_history.php")
    @FormUrlEncoded
    Observable<DonHangModel> xemDonHang(
            @Field("user_id") int user_id
    );

    @Multipart
    @POST("update_profile.php")
    Observable<UserModel> updateProfile(
            @Part("id") RequestBody id,
            @Part("hoten") RequestBody hoten,
            @Part("sodienthoai") RequestBody sodienthoai,
            @Part("diachi") RequestBody diachi,
            @Part MultipartBody.Part file
    );

    // Trong file ApiBanHang.java

    @POST("xemdonhang.php")
    @FormUrlEncoded
    Observable<DonHangModel> xemDonHang(
            @Field("user_id") int user_id,
            @Field("trangthai") String trangthai
    );

    @POST("huydonhang.php")
    @FormUrlEncoded
    Observable<MessageModel> huyDonHang(
            @Field("donhang_id") int donhang_id
    );

}
