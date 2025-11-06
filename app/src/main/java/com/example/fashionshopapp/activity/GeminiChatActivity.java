package com.example.fashionshopapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopapp.R;
import com.example.fashionshopapp.adapter.AiChatAdapter;
import com.example.fashionshopapp.model.AiChatMessage;
import com.example.fashionshopapp.model.GeminiRequest;
import com.example.fashionshopapp.model.GeminiResponse;
import com.example.fashionshopapp.retrofit.ApiBanHang;
import com.example.fashionshopapp.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GeminiChatActivity extends AppCompatActivity {

    // URL của server Next.js (web admin)
    // Dùng 10.0.2.2 cho máy ảo Android, nếu dùng máy thật thì thay bằng IP LAN của máy tính
    private static final String NEXTJS_API_BASE_URL = "http://10.0.2.2:3000/";

    private RecyclerView recyclerView;
    private EditText editTextMessage;
    private ImageButton buttonSend;
    private ProgressBar progressBar;
    private AiChatAdapter chatAdapter;
    private final List<AiChatMessage> messageList = new ArrayList<>();

    private ApiBanHang geminiApi;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gemini_chat);

        // Khởi tạo một instance Retrofit MỚI dành riêng cho API của Next.js
        geminiApi = RetrofitClient.getInstance(NEXTJS_API_BASE_URL).create(ApiBanHang.class);

        initView();
        setupRecyclerView();
        initControl();
        addInitialMessage();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar_gemini_chat);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        recyclerView = findViewById(R.id.recyclerview_gemini_chat);
        editTextMessage = findViewById(R.id.edittext_gemini_message);
        buttonSend = findViewById(R.id.button_gemini_send);
        progressBar = findViewById(R.id.progressbar_gemini_loading);
    }

    private void setupRecyclerView() {
        chatAdapter = new AiChatAdapter(messageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Giúp RecyclerView bắt đầu từ dưới lên
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);
    }

    private void addInitialMessage() {
        messageList.add(new AiChatMessage("Chào bạn, tôi là Trợ lý AI. Tôi có thể giúp gì cho bạn về các sản phẩm thời trang?", AiChatMessage.TYPE_BOT));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
    }

    private void initControl() {
        buttonSend.setOnClickListener(v -> {
            String query = editTextMessage.getText().toString().trim();
            if (!query.isEmpty()) {
                sendMessageToBot(query);
            }
        });
    }

    private void sendMessageToBot(String query) {
        // 1. Thêm tin nhắn của người dùng vào giao diện
        messageList.add(new AiChatMessage(query, AiChatMessage.TYPE_USER));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);
        editTextMessage.setText("");

        // 2. Hiển thị trạng thái đang xử lý và gọi API
        setLoadingState(true);

        compositeDisposable.add(geminiApi.sendQueryToGemini(new GeminiRequest(query))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::handleApiResponseSuccess,
                        this::handleApiResponseError
                ));
    }

    private void handleApiResponseSuccess(GeminiResponse response) {
        setLoadingState(false);
        String reply = (response != null && response.getReply() != null) ? response.getReply() : "Xin lỗi, tôi chưa thể phản hồi lúc này.";
        messageList.add(new AiChatMessage(reply, AiChatMessage.TYPE_BOT));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);
    }

    private void handleApiResponseError(Throwable e) {
        setLoadingState(false);
        Toast.makeText(this, "Lỗi kết nối: " + e.getMessage(), Toast.LENGTH_LONG).show();
        messageList.add(new AiChatMessage("Đã có lỗi xảy ra. Vui lòng kiểm tra kết nối và thử lại.", AiChatMessage.TYPE_BOT));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);
    }

    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            buttonSend.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            buttonSend.setEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
