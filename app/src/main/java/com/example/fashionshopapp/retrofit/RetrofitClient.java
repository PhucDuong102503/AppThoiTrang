package com.example.fashionshopapp.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.concurrent.TimeUnit;
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor; // Quan trọng: Import thư viện mới
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit instance;

    public static Retrofit getInstance(String baseUrl) {
        // Luôn tạo một instance mới để đảm bảo OkHttpClient được áp dụng.
        // Đây là cách tốt nhất để debug, sau khi xong có thể tối ưu lại với singleton pattern nếu cần.

        // 1. TẠO LOGGING INTERCEPTOR
        // Interceptor này sẽ in ra chi tiết request và response trong Logcat.
        // Rất quan trọng để debug.
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // 2. TẠO OKHTTP CLIENT VÀ GẮN INTERCEPTOR
        // Tăng thời gian timeout để tránh lỗi Connection Timed Out trên các mạng chậm.
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor) // Gắn interceptor vào client để ghi log
                .build();

        // 3. TẠO GSON CẤU HÌNH "LENIENT"
        // Giúp xử lý các trường hợp JSON không hoàn toàn chuẩn.
        Gson gson = new GsonBuilder().setLenient().create();

        // 4. TẠO RETROFIT INSTANCE VỚI OKHTTPCLIENT TÙY CHỈNH
        instance = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient) // QUAN TRỌNG: Sử dụng OkHttpClient đã được cấu hình
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();

        return instance;
    }
}
