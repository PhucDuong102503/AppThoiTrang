package com.example.fashionshopapp.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.fashionshopapp.R;
// ⭐ SỬA LẠI ĐƯỜNG DẪN IMPORT CHO ĐÚNG VỚI CẤU TRÚC MỚI CỦA BẠN
import com.example.fashionshopapp.model.EventBus.MessageEvent;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingSvc";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Kiểm tra xem notification có chứa payload không
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Notification Received: " + title + " - " + body);

            // Hiển thị notification lên thanh trạng thái
            showNotification(title, body);

            // ⭐ BƯỚC QUAN TRỌNG: Phát đi một sự kiện
            // Bất kỳ thành phần nào đang "lắng nghe" sự kiện này sẽ nhận được nó.
            EventBus.getDefault().post(new MessageEvent());
        }

        // Xử lý data payload nếu có (bạn có thể mở rộng sau)
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            Log.d(TAG, "Data Payload: " + data.toString());
        }
    }

    private void showNotification(String title, String body) {
        String CHANNEL_ID = "chat_messages";
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Chat Messages",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.send_1) // Thay bằng icon của bạn
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);
        // Gửi token này lên server của bạn nếu cần
    }
}
    