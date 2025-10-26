package com.example.fashionshopapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

// ⭐ LƯU Ý: Activity này chưa có giao diện Tab, nó chỉ hiển thị 1 danh sách duy nhất.
public class XemDonHangActivity extends AppCompatActivity implements DonHangAdapter.OnHuyDonClickListener { // ⭐ BƯỚC 1: Implement interface
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    RecyclerView recyclerDonHang;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xem_don_hang);
        initView();
        initToolbar();
        getOrderHistory();
    }

    private void getOrderHistory() {
        // Lấy trạng thái từ Intent, nếu không có thì mặc định là "Chờ giao hàng"
        String trangThai = getIntent().getStringExtra("trangthai_donhang");
        if (trangThai == null) {
            trangThai = "Chờ giao hàng";
        }

        compositeDisposable.add(apiBanHang.xemDonHang(Utils.user_current.getId(), trangThai) // Gọi API với trạng thái
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        donHangModel -> {
                            if (donHangModel.isSuccess()) {
                                // ⭐ BƯỚC 2: Sửa lại cách tạo Adapter, truyền 'this' làm listener
                                DonHangAdapter adapter = new DonHangAdapter(this, donHangModel.getResult(), this);
                                recyclerDonHang.setAdapter(adapter);
                            } else {
                                Toast.makeText(this, donHangModel.getMessage(), Toast.LENGTH_SHORT).show();
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
            getSupportActionBar().setTitle("Đơn hàng của tôi");
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

    // ⭐ BƯỚC 3: Override lại phương thức của interface để xử lý sự kiện hủy đơn
    @Override
    public void onHuyDonClick(DonHang donHang) {
        // Tại đây, bạn sẽ gọi API để hủy đơn hàng
        huyDonHangApi(donHang.getId());
    }

    private void huyDonHangApi(int donhang_id) {
        compositeDisposable.add(apiBanHang.huyDonHang(donhang_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            if (messageModel.isSuccess()) {
                                Toast.makeText(this, "Hủy đơn hàng thành công", Toast.LENGTH_SHORT).show();
                                // Sau khi hủy thành công, gọi lại API để làm mới danh sách
                                getOrderHistory();
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
