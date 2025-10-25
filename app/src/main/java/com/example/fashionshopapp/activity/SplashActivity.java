package com.example.fashionshopapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fashionshopapp.R;

public class SplashActivity extends AppCompatActivity {

    Button btnGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Ánh xạ nút bấm từ file layout
        btnGetStarted = findViewById(R.id.btn_get_started);

        // Thiết lập sự kiện click cho nút
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo một Intent để chuyển từ màn hình hiện tại (SplashActivity)
                // sang màn hình Đăng nhập (LoginActivity)
                Intent intent = new Intent(SplashActivity.this, DangNhapActivity.class);
                startActivity(intent);

                // Kết thúc SplashActivity để người dùng không thể quay lại bằng nút back
                finish();
            }
        });
    }
}
