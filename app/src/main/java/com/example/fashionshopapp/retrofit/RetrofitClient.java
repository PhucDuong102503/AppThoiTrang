package com.example.fashionshopapp.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.concurrent.TimeUnit; // <<< QUAN TRỌNG: Import thư viện TimeUnit
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    /**
     * Phương thức này sẽ tạo và trả về một instance Retrofit mới mỗi lần được gọi,
     * được cấu hình với Logging Interceptor và tăng thời gian chờ.
     * @param baseUrl URL gốc của API cần gọi
     * @return Một instance Retrofit đã được cấu hình đầy đủ.
     */
    public static Retrofit getInstance(String baseUrl) {

        // 1. TẠO LOGGING INTERCEPTOR (để debug)
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // 2. TẠO OKHTTP CLIENT VÀ CẤU HÌNH TIMEOUT
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // ⭐⭐⭐ PHẦN QUAN TRỌNG NHẤT ĐỂ SỬA LỖI TIMEOUT ⭐⭐⭐
                .readTimeout(30, TimeUnit.SECONDS)      // Tăng thời gian chờ đọc phản hồi lên 30 giây
                .connectTimeout(30, TimeUnit.SECONDS)   // Tăng thời gian chờ kết nối lên 30 giây
                .writeTimeout(30, TimeUnit.SECONDS)     // Tăng thời gian chờ ghi yêu cầu lên 30 giây
                .addInterceptor(loggingInterceptor)     // Gắn interceptor để ghi log
                .build();

        // 3. TẠO GSON
        Gson gson = new GsonBuilder().setLenient().create();

        // 4. TẠO RETROFIT INSTANCE
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient) // QUAN TRỌNG: Sử dụng OkHttpClient đã được cấu hình ở trên
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
    }
}
