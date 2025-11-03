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
                        // Mỗi khi có thay đổi, xóa list cũ và thêm lại từ đầu.
                        // Đây là cách làm an toàn và đảm bảo dữ liệu luôn đồng bộ.
                        chatMessageList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            ChatMessage chatMessage = doc.toObject(ChatMessage.class);
                            chatMessageList.add(chatMessage);
                        }

                        // Thông báo cho adapter rằng toàn bộ dữ liệu đã thay đổi
                        chatAdapter.notifyDataSetChanged();

                        // Cuộn xuống tin nhắn cuối cùng
                        if (!chatMessageList.isEmpty()) {
                            recyclerViewChat.smoothScrollToPosition(chatMessageList.size() - 1);
                        }

                        // Sau khi tải và hiển thị tin nhắn, đánh dấu chúng là đã đọc
                        markMessagesAsRead();
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                });
    }

    private void generateConversationKey() {
        try {
            // Chuyển đổi ID sang kiểu số (long) để so sánh, tránh lỗi so sánh chuỗi "10" < "9"
            long currentIdNum = Long.parseLong(currentUserId);
            long targetIdNum = Long.parseLong(targetAdminId);

            // Luôn đặt ID nhỏ hơn ở trước để đảm bảo key là duy nhất cho 2 người
            if (currentIdNum < targetIdNum) {
                conversationKey = currentIdNum + "_" + targetIdNum;
            } else {
                conversationKey = targetIdNum + "_" + currentIdNum;
            }
            Log.d(TAG, "Generated ConversationKey: " + conversationKey);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Lỗi khi chuyển đổi ID sang số", e);
            Toast.makeText(this, "Lỗi ID người dùng không hợp lệ.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    // ====================  //

    private void markMessagesAsRead() {
        db.collection("messages")
                .whereEqualTo("conversationKey", this.conversationKey)
                .whereEqualTo("receiver_id", this.currentUserId) // Chỉ đánh dấu tin nhắn mà MÌNH nhận
                .whereEqualTo("read", false) // và tin nhắn đó chưa được đọc
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        return; // Không có tin nhắn nào cần đánh dấu
                    }
                    WriteBatch batch = db.batch();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        batch.update(document.getReference(), "read", true);
                    }
                    batch.commit().addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Đã đánh dấu " + queryDocumentSnapshots.size() + " tin nhắn là đã đọc.");
                    }).addOnFailureListener(e -> {
                        Log.w(TAG, "Lỗi khi đánh dấu đã đọc.", e);
                    });
                });
    }

    private void initView() {
        recyclerViewChat = findViewById(R.id.recycleview_chat);
        edtInputText = findViewById(R.id.edtinputtext);
        imageSend = findViewById(R.id.imagechat);
        toolbar = findViewById(R.id.toolbar_chat);

        db = FirebaseFirestore.getInstance();
        chatMessageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatMessageList, currentUserId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Giúp RecyclerView bắt đầu từ dưới lên
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
