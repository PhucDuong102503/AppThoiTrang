package com.example.fashionshopapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopapp.R;
import com.example.fashionshopapp.adapter.ChatAdapter;
import com.example.fashionshopapp.model.ChatMessage;
import com.example.fashionshopapp.util.Utils;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    private RecyclerView recyclerViewChat;
    private EditText edtInputText;
    private ImageButton imageSend;
    private Toolbar toolbar;

    private FirebaseFirestore db;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessageList;

    private String currentUserId;
    private String targetAdminId;
    private String targetAdminName;
    private String conversationKey;
    private boolean isFirstLoad = true; // Biến cờ để chỉ chạy markAsRead một lần

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        targetAdminId = intent.getStringExtra("admin_id");
        targetAdminName = intent.getStringExtra("admin_name");

        if (Utils.user_current == null || Utils.user_current.getId() == 0) {
            Toast.makeText(this, "Vui lòng đăng nhập để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (targetAdminId == null || targetAdminId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không xác định được người nhận.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentUserId = String.valueOf(Utils.user_current.getId());

        initView();
        setupToolbar();
        initControl();
        generateConversationKey();
        listenMessages();
    }
    private void markMessagesAsRead() {
        db.collection("messages")
                // Chỉ lấy các tin nhắn trong cuộc hội thoại này
                .whereEqualTo("conversationKey", this.conversationKey)
                // Chỉ lấy các tin nhắn mà MÌNH LÀ NGƯỜI NHẬN
                .whereEqualTo("receiver_id", this.currentUserId)
                // Chỉ lấy các tin nhắn CHƯA ĐỌC
                .whereEqualTo("read", false)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Dùng WriteBatch để cập nhật nhiều document cùng lúc cho hiệu quả
                    WriteBatch batch = db.batch();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        batch.update(document.getReference(), "read", true);
                    }
                    // Commit tất cả thay đổi lên server
                    batch.commit().addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Đã đánh dấu " + queryDocumentSnapshots.size() + " tin nhắn là đã đọc.");
                    }).addOnFailureListener(e -> {
                        Log.w(TAG, "Lỗi khi đánh dấu đã đọc.", e);
                    });
                });
    }

    private void listenMessages() {
        db.collection("messages")
                .whereEqualTo("conversationKey", this.conversationKey)
                .orderBy("created_at", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Listen failed.", error);
                        return;
                    }

                    if (value != null) {
                        int oldSize = chatMessageList.size(); // Kích thước cũ của list
                        for (DocumentChange docChange : value.getDocumentChanges()) {
                            if (docChange.getType() == DocumentChange.Type.ADDED) {
                                // Tránh thêm trùng lặp
                                ChatMessage chatMessage = docChange.getDocument().toObject(ChatMessage.class);
                                if (!isMessageInList(chatMessage.sender_id, chatMessage.created_at)) {
                                    chatMessageList.add(chatMessage);
                                }
                            }
                        }
                        chatMessageList.sort((o1, o2) -> o1.getCreated_at().compareTo(o2.getCreated_at()));

                        if (chatMessageList.size() > oldSize || isFirstLoad) {
                            chatAdapter.notifyDataSetChanged();
                            recyclerViewChat.scrollToPosition(chatMessageList.size() - 1);
                        }

                        if (isFirstLoad) {
                            markMessagesAsRead();
                            isFirstLoad = false;
                        }
                    }
                });
    }

    private boolean isMessageInList(String senderId, Date createdAt) {
        for (ChatMessage msg : chatMessageList) {
            if (msg.getSender_id().equals(senderId) && msg.getCreated_at().equals(createdAt)) {
                return true;
            }
        }
        return false;
    }

    // ---------- CÁC HÀM CŨ (KHÔNG THAY ĐỔI) ----------

    private void initView() {
        recyclerViewChat = findViewById(R.id.recycleview_chat);
        edtInputText = findViewById(R.id.edtinputtext);
        imageSend = findViewById(R.id.imagechat);
        toolbar = findViewById(R.id.toolbar_chat);

        db = FirebaseFirestore.getInstance();
        chatMessageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatMessageList, currentUserId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewChat.setLayoutManager(layoutManager);
        recyclerViewChat.setAdapter(chatAdapter);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            String title = (targetAdminName != null && !targetAdminName.isEmpty()) ? targetAdminName : "Hỗ trợ";
            getSupportActionBar().setTitle("Chat với " + title);
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    private void initControl() {
        imageSend.setOnClickListener(v -> {
            String messageText = edtInputText.getText().toString().trim();
            if (!TextUtils.isEmpty(messageText)) {
                sendMessage(messageText);
            }
        });
    }

    private void generateConversationKey() {
        if (Integer.parseInt(currentUserId) < Integer.parseInt(targetAdminId)) {
            conversationKey = currentUserId + "_" + targetAdminId;
        } else {
            conversationKey = targetAdminId + "_" + currentUserId;
        }
        Log.d(TAG, "Generated ConversationKey: " + conversationKey);
    }

    private void sendMessage(String messageText) {
        imageSend.setEnabled(false);
        Map<String, Object> message = new HashMap<>();
        message.put("sender_id", currentUserId);
        message.put("receiver_id", targetAdminId);
        message.put("content", messageText);
        message.put("created_at", new Date());
        message.put("conversationKey", conversationKey);
        message.put("read", false);

        db.collection("messages").add(message)
                .addOnSuccessListener(documentReference -> {
                    edtInputText.setText("");
                    imageSend.setEnabled(true);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ChatActivity.this, "Gửi tin nhắn thất bại", Toast.LENGTH_SHORT).show();
                    imageSend.setEnabled(true);
                    Log.e(TAG, "Lỗi khi gửi tin nhắn", e);
                });
    }
}
