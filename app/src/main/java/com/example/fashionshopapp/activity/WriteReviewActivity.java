package com.example.fashionshopapp.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView; // ⭐ Import ImageView
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide; // ⭐ Import Glide
import com.example.fashionshopapp.R;
import com.example.fashionshopapp.model.SanPhamMoi;
import com.example.fashionshopapp.retrofit.ApiBanHang;
import com.example.fashionshopapp.retrofit.RetrofitClient;
import com.example.fashionshopapp.util.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WriteReviewActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageView imgProduct; // ⭐ Ánh xạ ImageView mới
    TextView txtProductName;
    RatingBar ratingBar;
    EditText edtComment;
    Button btnSubmit;

    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    SanPhamMoi sanPhamReview;
    int donHangId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);

        initView();
        initControl();
        getIntentData();
    }

    private void getIntentData() {
        sanPhamReview = (SanPhamMoi) getIntent().getSerializableExtra("san_pham_review");
        donHangId = getIntent().getIntExtra("don_hang_id", 0);

        if (sanPhamReview != null) {
            txtProductName.setText(sanPhamReview.getTensanpham());

            // ⭐⭐⭐ HIỂN THỊ HÌNH ẢNH SẢN PHẨM ⭐⭐⭐
            String imageUrl = sanPhamReview.getHinhanhsanpham();
            if (imageUrl != null && !imageUrl.startsWith("http")) {
                imageUrl = Utils.BASE_URL + "images/" + imageUrl;
            }
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_media_24) // Ảnh mặc định khi đang tải
                    .into(imgProduct);
        }
    }

    private void initControl() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        btnSubmit.setOnClickListener(v -> {
            submitReview();
        });
    }

    // Trong file WriteReviewActivity.java, hàm submitReview()

    private void submitReview() {
        int sao = (int) ratingBar.getRating();
        String binhluan = edtComment.getText().toString().trim();

        // ⭐ SỬA LỖI GÕ NHẦM Ở ĐÂY
        if (Utils.user_current == null) { // Đổi currentUser thành user_current
            Toast.makeText(this, "Vui lòng đăng nhập để đánh giá", Toast.LENGTH_SHORT).show();
            return;
        }
        if (sao == 0) {
            Toast.makeText(this, "Vui lòng chọn số sao", Toast.LENGTH_SHORT).show();
            return;
        }
        if (sanPhamReview == null || donHangId == 0) {
            Toast.makeText(this, "Thiếu thông tin sản phẩm hoặc đơn hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi API gửi đánh giá (đoạn này bây giờ sẽ hết lỗi)
        compositeDisposable.add(apiBanHang.danhGiaSanPham(
                        Utils.user_current.getId(), // Đổi currentUser thành user_current
                        sanPhamReview.getId(),
                        donHangId,
                        sao,
                        binhluan)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            if (messageModel.isSuccess()) {
                                Toast.makeText(this, "Cảm ơn bạn đã đánh giá sản phẩm!", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(this, "Lỗi: " + messageModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(this, "Lỗi kết nối: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }



    private void initView() {
        toolbar = findViewById(R.id.toolbar_write_review);
        imgProduct = findViewById(R.id.image_product_review); // ⭐ Ánh xạ ImageView mới
        txtProductName = findViewById(R.id.text_product_name_review);
        ratingBar = findViewById(R.id.rating_bar_review);
        edtComment = findViewById(R.id.edit_text_comment);
        btnSubmit = findViewById(R.id.button_submit_review);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
