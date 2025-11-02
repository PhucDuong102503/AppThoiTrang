package com.example.fashionshopapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopapp.R;
import com.example.fashionshopapp.adapter.DonHangAdapter;
import com.example.fashionshopapp.model.DonHang;
import com.example.fashionshopapp.retrofit.ApiBanHang;
import com.example.fashionshopapp.retrofit.RetrofitClient;
import com.example.fashionshopapp.util.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class XemDonHangActivity extends AppCompatActivity implements DonHangAdapter.OnHuyDonClickListener {
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    RecyclerView recyclerDonHang;
    Toolbar toolbar;

    // Biến để lưu status_id
    private int statusId = 0; // Mặc định là 0 (Chờ giao hàng)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xem_don_hang);
        initView();
        initToolbar();
        // Lấy status_id từ Intent
        statusId = getIntent().getIntExtra("status_id", 0);
        getOrderHistory();
    }

    private void getOrderHistory() {
        if (Utils.user_current == null) return;

        // ⭐⭐⭐ SỬA LỖI TẠI ĐÂY ⭐⭐⭐
        // Gọi API với biến statusId (kiểu int) thay vì biến trangThai (kiểu String)
        compositeDisposable.add(apiBanHang.xemDonHang(Utils.user_current.getId(), statusId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        donHangModel -> {
                            if (donHangModel.isSuccess() && donHangModel.getResult() != null) {
                                DonHangAdapter adapter = new DonHangAdapter(this, donHangModel.getResult(), this);
                                recyclerDonHang.setAdapter(adapter);
                            } else {
                                // Xử lý trường hợp không có đơn hàng
                                Toast.makeText(this, "Không có đơn hàng nào.", Toast.LENGTH_SHORT).show();
                                // Có thể cần xóa dữ liệu cũ trong adapter nếu có
                                recyclerDonHang.setAdapter(null);
                            }
                        },
                        throwable -> {
                            Toast.makeText(this, "Lỗi: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("XemDonHang", "Lỗi getOrderHistory: " + throwable.getMessage());
                        }
                ));
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // Sửa tiêu đề cho phù hợp
            String title = "Đơn hàng của tôi";
            switch (statusId) {
                case 0:
                    title = "Đơn hàng chờ giao";
                    break;
                case 1:
                    title = "Đơn hàng đã giao";
                    break;
                case 2:
                    title = "Đơn hàng đã hủy";
                    break;
            }
            getSupportActionBar().setTitle(title);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        recyclerDonHang = findViewById(R.id.recycleview_donhang);
        toolbar = findViewById(R.id.toolbar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerDonHang.setLayoutManager(layoutManager);
    }

    @Override
    public void onHuyDonClick(DonHang donHang) {
        huyDonHangApi(donHang.getId());
    }

    private void huyDonHangApi(int donhang_id) {
        compositeDisposable.add(apiBanHang.huyDonHang(donhang_id) // Giả sử bạn có API huyDonHang
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            if (messageModel.isSuccess()) {
                                Toast.makeText(this, "Hủy đơn hàng thành công", Toast.LENGTH_SHORT).show();
                                getOrderHistory(); // Tải lại danh sách
                            } else {
                                Toast.makeText(this, "Hủy thất bại: " + messageModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(this, "Lỗi: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("XemDonHang", "Lỗi huyDonHangApi: " + throwable.getMessage());
                        }
                ));
    }


    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
