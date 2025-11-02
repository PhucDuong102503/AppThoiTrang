package com.example.fashionshopapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar; // ⭐ THÊM MỚI
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fashionshopapp.R;
import com.example.fashionshopapp.adapter.ReviewAdapter;
import com.example.fashionshopapp.model.AppDatabase;
import com.example.fashionshopapp.model.GioHang;
import com.example.fashionshopapp.model.Review;
import com.example.fashionshopapp.model.ReviewModel;
import com.example.fashionshopapp.model.SanPhamMoi;
import com.example.fashionshopapp.model.SanPhamSize;
import com.example.fashionshopapp.retrofit.ApiBanHang;
import com.example.fashionshopapp.retrofit.RetrofitClient;
import com.example.fashionshopapp.util.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ChiTietActivity extends AppCompatActivity {

    TextView txtTen, txtGia, txtMoTa;
    Button btnThemVaoGio;
    ImageView imgHinhAnh;
    Spinner spinnerSize, spinnerSoLuong;
    Toolbar toolbar;
    LinearLayout layoutSizeSelection;
    RecyclerView recyclerViewReviews;
    ReviewAdapter reviewAdapter;
    List<Review> reviewList;

    Button btnVietDanhGia;
    LinearLayout layoutSummaryReview;
    RatingBar ratingBarSummary;
    TextView txtRatingSummary, txtNoReviews;

    // --- Các biến logic ---
    SanPhamMoi sanPhamMoi;
    AppDatabase appDatabase;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet);

        initView();
        initData();
        ActionToolBar();

        if (sanPhamMoi != null) {
            getSanPhamSize();
            initSpinnerSoLuong();
            getReviews(); // Gọi hàm lấy đánh giá
        }

        btnThemVaoGio.setOnClickListener(v -> themVaoGio());

        // THÊM SỰ KIỆN CLICK CHO NÚT VIẾT ĐÁNH GIÁ
        btnVietDanhGia.setOnClickListener(v -> {
            // 1. Tạo một Intent để mở WriteReviewActivity
            Intent intent = new Intent(ChiTietActivity.this, WriteReviewActivity.class);
            // 2. Đính kèm đối tượng sản phẩm để màn hình mới biết đang đánh giá sản phẩm nào
            intent.putExtra("san_pham_review", sanPhamMoi);
            // 3. Khởi động Activity mới
            startActivity(intent);
        });
    }

    private void getReviews() {
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(this, reviewList);
        recyclerViewReviews.setAdapter(reviewAdapter);

        compositeDisposable.add(apiBanHang.getReviews(sanPhamMoi.getId(), 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        reviewModel -> {
                            //  NÂNG CẤP LOGIC XỬ LÝ KẾT QUẢ
                            if (reviewModel != null && reviewModel.isSuccess() && reviewModel.getResult() != null && !reviewModel.getResult().isEmpty()) {
                                // Nếu có đánh giá
                                reviewList.clear();
                                reviewList.addAll(reviewModel.getResult());
                                reviewAdapter.notifyDataSetChanged();

                                // Tính toán và hiển thị phần tóm tắt
                                calculateAndShowSummary();

                                // Ẩn thông báo "chưa có đánh giá"
                                txtNoReviews.setVisibility(View.GONE);
                                layoutSummaryReview.setVisibility(View.VISIBLE);

                            } else {
                                // Nếu không có đánh giá nào
                                txtNoReviews.setVisibility(View.VISIBLE);
                                layoutSummaryReview.setVisibility(View.GONE);
                            }
                        },
                        throwable -> {
                            // Xử lý lỗi
                            txtNoReviews.setVisibility(View.VISIBLE);
                            layoutSummaryReview.setVisibility(View.GONE);
                            Toast.makeText(this, "Lỗi tải đánh giá: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    //TÍNH TOÁN VÀ HIỂN THỊ TÓM TẮT ĐÁNH GIÁ
    private void calculateAndShowSummary() {
        if (reviewList == null || reviewList.isEmpty()) {
            return;
        }

        float totalStars = 0;
        for (Review review : reviewList) {
            totalStars += review.getSao();
        }

        int reviewCount = reviewList.size();
        float averageRating = totalStars / reviewCount;

        // Cập nhật lên giao diện
        ratingBarSummary.setRating(averageRating);
        String summaryText = String.format("%.1f/5 (%d đánh giá)", averageRating, reviewCount);
        txtRatingSummary.setText(summaryText);
    }


    private void initView() {
        txtTen = findViewById(R.id.txttenchitietsp);
        txtGia = findViewById(R.id.txtgiachitietsp);
        txtMoTa = findViewById(R.id.txtmotachitiet);
        btnThemVaoGio = findViewById(R.id.btnthemvaogiohang);
        imgHinhAnh = findViewById(R.id.imgchitiet);
        spinnerSoLuong = findViewById(R.id.spinnersl);
        spinnerSize = findViewById(R.id.spinnerslsize);
        layoutSizeSelection = findViewById(R.id.layout_size_selection);
        toolbar = findViewById(R.id.toobar);
        recyclerViewReviews = findViewById(R.id.recycleview_reviews);

        btnVietDanhGia = findViewById(R.id.btn_viet_danh_gia);
        layoutSummaryReview = findViewById(R.id.layout_summary_review);
        ratingBarSummary = findViewById(R.id.rating_bar_summary);
        txtRatingSummary = findViewById(R.id.txt_rating_summary);
        txtNoReviews = findViewById(R.id.txt_no_reviews);

        // --- Cấu hình RecyclerView ---
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewReviews.setLayoutManager(layoutManager);
        recyclerViewReviews.setNestedScrollingEnabled(false);

        // --- Khởi tạo API và DB ---
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        appDatabase = AppDatabase.getInstance(getApplicationContext());
    }

    private void initData() {
        sanPhamMoi = (SanPhamMoi) getIntent().getSerializableExtra("chitiet");
        if (sanPhamMoi == null) {
            Toast.makeText(this, "Không thể tải thông tin sản phẩm", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        txtTen.setText(sanPhamMoi.getTensanpham());
        txtMoTa.setText(sanPhamMoi.getMotasanpham());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        txtGia.setText("Giá: " + decimalFormat.format(Double.parseDouble(sanPhamMoi.getGiasanpham())) + "đ");

        String imageUrl = Utils.BASE_URL + "images/" + sanPhamMoi.getHinhanhsanpham();
        Glide.with(this).load(imageUrl).placeholder(R.drawable.ic_media_24).into(imgHinhAnh);

        // LOGIC TẠM THỜI ĐỂ HIỂN THỊ NÚT "VIẾT ĐÁNH GIÁ"
        // Trong thực tế, bạn cần gọi API để kiểm tra xem người dùng đã mua sản phẩm này chưa
        // Ví dụ, nếu người dùng đã đăng nhập thì hiển thị nút
        if (Utils.user_current != null) {
            btnVietDanhGia.setVisibility(View.VISIBLE);
        } else {
            btnVietDanhGia.setVisibility(View.GONE);
        }
        btnVietDanhGia.setVisibility(View.GONE);
    }

    // ... (CÁC HÀM CÒN LẠI: themVaoGio, themPhuKienVaoGio, themSanPhamCoSizeVaoGio, insertOrUpdateDatabase, ActionToolBar, getSanPhamSize, initSpinnerSoLuong, onDestroy GIỮ NGUYÊN KHÔNG ĐỔI)
    private void themVaoGio() {
        if (sanPhamMoi.getIdloaisanpham() == 5) {
            themPhuKienVaoGio();
        } else {
            themSanPhamCoSizeVaoGio();
        }
    }

    private void themPhuKienVaoGio() {
        final int soLuongMua = Integer.parseInt(spinnerSoLuong.getSelectedItem().toString());
        final int sanPhamIdThuc = sanPhamMoi.getId();
        final int sizeIdMacDinh = 0; // Phụ kiện luôn có sizeId = 0

        compositeDisposable.add(appDatabase.gioHangDAO().getProductByPrimaryKey(sanPhamIdThuc, sizeIdMacDinh)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        existingCartItem -> {
                            existingCartItem.setSoluong(existingCartItem.getSoluong() + soLuongMua);
                            insertOrUpdateDatabase(existingCartItem, "Đã cập nhật số lượng trong giỏ");
                        },
                        throwable -> {
                            GioHang newCartItem = new GioHang();
                            newCartItem.setIdsp(sanPhamIdThuc); // ID thật
                            newCartItem.setTensp(sanPhamMoi.getTensanpham());
                            newCartItem.setGiasp(Long.parseLong(sanPhamMoi.getGiasanpham()));
                            newCartItem.setHinhanh(sanPhamMoi.getHinhanhsanpham());
                            newCartItem.setSoluong(soLuongMua);
                            newCartItem.setSizeId(sizeIdMacDinh); // ID Size mặc định là 0
                            newCartItem.setSize("Phụ kiện");
                            insertOrUpdateDatabase(newCartItem, "Đã thêm sản phẩm vào giỏ hàng");
                        }
                ));
    }

    private void themSanPhamCoSizeVaoGio() {
        if (spinnerSize.getSelectedItem() == null) {
            Toast.makeText(this, "Vui lòng chọn size sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        SanPhamSize selectedSize = (SanPhamSize) spinnerSize.getSelectedItem();
        int soLuongMua = Integer.parseInt(spinnerSoLuong.getSelectedItem().toString());

        if (soLuongMua > selectedSize.getSoluong()) {
            Toast.makeText(this, "Số lượng trong kho không đủ!", Toast.LENGTH_SHORT).show();
            return;
        }

        final int sanPhamIdThuc = sanPhamMoi.getId(); // ID thật của sản phẩm
        final int sizeIdThuc = selectedSize.getSize_id(); // ID thật của size đã chọn

        compositeDisposable.add(appDatabase.gioHangDAO().getProductByPrimaryKey(sanPhamIdThuc, sizeIdThuc)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        existingCartItem -> {
                            existingCartItem.setSoluong(existingCartItem.getSoluong() + soLuongMua);
                            insertOrUpdateDatabase(existingCartItem, "Đã cập nhật số lượng trong giỏ");
                        },
                        throwable -> {
                            GioHang newCartItem = new GioHang();
                            newCartItem.setIdsp(sanPhamIdThuc);
                            newCartItem.setTensp(sanPhamMoi.getTensanpham());
                            newCartItem.setGiasp(Long.parseLong(sanPhamMoi.getGiasanpham()));
                            newCartItem.setHinhanh(sanPhamMoi.getHinhanhsanpham());
                            newCartItem.setSoluong(soLuongMua);
                            newCartItem.setSizeId(sizeIdThuc);
                            newCartItem.setSize(selectedSize.getTensize());
                            insertOrUpdateDatabase(newCartItem, "Đã thêm sản phẩm vào giỏ hàng");
                        }
                ));
    }

    private void insertOrUpdateDatabase(GioHang gioHang, String message) {
        compositeDisposable.add(appDatabase.gioHangDAO().insertOrReplace(gioHang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show(),
                        throwable -> Toast.makeText(this, "Lỗi database: " + throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }

    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết sản phẩm");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void getSanPhamSize() {
        if (sanPhamMoi.getIdloaisanpham() == 5) {
            layoutSizeSelection.setVisibility(View.GONE);
            return;
        }

        layoutSizeSelection.setVisibility(View.VISIBLE);

        compositeDisposable.add(apiBanHang.getSanPhamSize(sanPhamMoi.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamSizeModel -> {
                            if (sanPhamSizeModel != null && sanPhamSizeModel.isSuccess()) {
                                List<SanPhamSize> listSize = sanPhamSizeModel.getResult();
                                if (listSize == null || listSize.isEmpty()) {
                                    layoutSizeSelection.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Sản phẩm này tạm hết hàng hoặc chưa có size", Toast.LENGTH_LONG).show();
                                } else {
                                    ArrayAdapter<SanPhamSize> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listSize);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinnerSize.setAdapter(adapter);
                                }
                            } else {
                                layoutSizeSelection.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Sản phẩm này chưa có size", Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            layoutSizeSelection.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Lỗi khi tải size: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }


    private void initSpinnerSoLuong() {
        Integer[] so = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, so);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSoLuong.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
