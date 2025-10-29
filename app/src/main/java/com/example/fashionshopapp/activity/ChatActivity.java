package com.example.fashionshopapp.activity;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.widget.Toolbar; // Đảm bảo import đúng
import androidx.appcompat.app.AppCompatActivity;

import com.example.fashionshopapp.R;

// ... các import khác

public class ChatActivity extends AppCompatActivity {

    // Khai báo các view của bạn
    Toolbar toolbar;
    // EditText edtinputtext;
    // RecyclerView recycleview_chat;
    // ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Gọi hàm ánh xạ
        initView();

        // Gọi hàm cài đặt Toolbar
        actionToolbar();
    }

    private void actionToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng màn hình Chat và quay lại màn hình trước đó
            }
        });
    }

    private void initView() {
        // Ánh xạ các view
        toolbar = findViewById(R.id.toolbar_chat);
        // edtinputtext = findViewById(R.id.edtinputtext);
        // recycleview_chat = findViewById(R.id.recycleview_chat);
        // ...
    }
}
