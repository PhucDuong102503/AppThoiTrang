package com.example.fashionshopapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar; // Thêm ProgressBar
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fashionshopapp.R;

public class VNPAYActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vnpay); // Giả sử layout của bạn có ProgressBar với id là progress_bar

        WebView webView = findViewById(R.id.webview_vnpay);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true); // Hỗ trợ lưu trữ DOM

        String url = getIntent().getStringExtra("url");

        webView.setWebViewClient(new WebViewClient() {

            // Hiển thị ProgressBar khi trang bắt đầu tải
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
                Log.d("VNPAY_WEBVIEW", "Bắt đầu tải URL: " + url);

                // Lắng nghe khi VNPAY chuyển hướng về URL trả về của bạn
                if (url.contains("vnpay_return.php")) {
                    Uri uri = Uri.parse(url);
                    String responseCode = uri.getQueryParameter("vnp_ResponseCode");

                    Intent intent = new Intent();
                    if ("00".equals(responseCode)) {
                        intent.putExtra("status", "success");
                    } else {
                        intent.putExtra("status", "failure");
                    }
                    setResult(RESULT_OK, intent);
                    finish(); // Đóng màn hình WebView và trả kết quả
                }
            }

            // Ẩn ProgressBar khi trang tải xong
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // if (progressBar != null) progressBar.setVisibility(View.GONE);
                Log.d("VNPAY_WEBVIEW", "Tải xong URL: " + url);
            }

            // ⭐ HÀM QUAN TRỌNG: BẮT LỖI KHI TẢI TRANG
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                // if (progressBar != null) progressBar.setVisibility(View.GONE);

                // Chỉ xử lý lỗi cho trang chính
                if (request.isForMainFrame()) {
                    String errorMessage = "Lỗi khi tải trang: " + error.getDescription() + " (Mã lỗi: " + error.getErrorCode() + ")";
                    Log.e("VNPAY_ERROR", errorMessage);
                    Toast.makeText(VNPAYActivity.this, "Không thể kết nối tới cổng thanh toán. Vui lòng kiểm tra lại kết nối mạng.", Toast.LENGTH_LONG).show();
                    // Đóng activity sau khi thông báo lỗi
                    new android.os.Handler().postDelayed(
                            () -> finish(),
                            3000
                    );
                }
            }
        });

        // Tải URL thanh toán
        if (url != null && !url.isEmpty()) {
            webView.loadUrl(url);
        } else {
            Log.e("VNPAY_ERROR", "URL is null or empty. Closing activity.");
            Toast.makeText(this, "Lỗi: Không có đường dẫn thanh toán.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
