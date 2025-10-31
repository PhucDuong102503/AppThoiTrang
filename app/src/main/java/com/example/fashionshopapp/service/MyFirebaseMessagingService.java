// Đường dẫn: com/example/fashionshopapp/service/MyFirebaseMessagingService.java
package com.example.fashionshopapp.service;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Hàm này được gọi khi một token mới được tạo ra hoặc khi token cũ được làm mới.
     * Đây là nơi quan trọng nhất để bạn lấy token và gửi nó lên server.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);

        // GỌI HÀM GỬI TOKEN LÊN SERVER CỦA BẠN TẠI ĐÂY
        // sendTokenToServer(token);
    }

    /**
     * Hàm này được gọi khi ứng dụng nhận được một thông báo đẩy lúc đang ở foreground.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Xử lý hiển thị thông báo khi app đang chạy
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            // Bạn có thể tự tạo một thông báo tùy chỉnh ở đây
        }
    }
}
