package com.example.fashionshopapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopapp.Interface.GioHangItemClickListener;
import com.example.fashionshopapp.R;
import com.example.fashionshopapp.adapter.GioHangAdapter;
import com.example.fashionshopapp.model.AppDatabase;
import com.example.fashionshopapp.model.GioHang;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GioHangActivity extends AppCompatActivity implements GioHangItemClickListener {

    Toolbar toolbar;
    RecyclerView recyclerView;
    TextView txtGioHangTrong, txtTongTien;
    Button btnMuaHang;
    AppDatabase appDatabase;
    GioHangAdapter gioHangAdapter;
    List<GioHang> gioHangList = new ArrayList<>();
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gio_hang);

        initView();
        initControl();
        // Tải dữ liệu lần đầu khi vào màn hình
        loadDataFromDatabase();
    }

    // HÀM TẢI DỮ LIỆU TỪ DATABASE
    private void loadDataFromDatabase() {
        compositeDisposable.add(appDatabase.gioHangDAO().getAllCartItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::updateCartView, // Sử dụng method reference cho gọn
                        throwable -> Toast.makeText(getApplicationContext(), "Lỗi khi tải giỏ hàng: " + throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }

    // HÀM DUY NHẤT CHỊU TRÁCH NHIỆM CẬP NHẬT TOÀN BỘ GIAO DIỆN GIỎ HÀNG
    private void updateCartView(List<GioHang> listFromDb) {
        if (listFromDb == null || listFromDb.isEmpty()) {
            // Trường hợp giỏ hàng trống
            txtGioHangTrong.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            this.gioHangList.clear(); // Xóa sạch dữ liệu trong Adapter
        } else {
            // Trường hợp có sản phẩm
            txtGioHangTrong.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            this.gioHangList.clear(); // Xóa dữ liệu cũ
            this.gioHangList.addAll(listFromDb); // Nạp dữ liệu mới
        }
        // Luôn luôn gọi notifyDataSetChanged() để vẽ lại toàn bộ
        gioHangAdapter.notifyDataSetChanged();
        // Luôn tính lại tổng tiền
        calculateTotalPrice();
    }

    // Tính tổng tiền
    private void calculateTotalPrice() {
        long tongTien = 0;
        for (GioHang item : gioHangList) {
            tongTien += (item.getGiasp() * item.getSoluong());
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        txtTongTien.setText(decimalFormat.format(tongTien) + "đ");
    }

    // Cài đặt các điều khiển
    private void initControl() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(gioHangAdapter);
    }

    // Ánh xạ View
    private void initView() {
        toolbar = findViewById(R.id.toolbar_giohang);
        recyclerView = findViewById(R.id.recyclerview_giohang);
        txtGioHangTrong = findViewById(R.id.txt_giohangtrong);
        txtTongTien = findViewById(R.id.txt_tongtien);
        btnMuaHang = findViewById(R.id.btn_muahang);
        appDatabase = AppDatabase.getInstance(this);
        gioHangAdapter = new GioHangAdapter(this, gioHangList, this);
    }

    // HÀM XỬ LÝ SỰ KIỆN XÓA (PHIÊN BẢN TÁI CẤU TRÚC)
    @Override
    public void onItemClick(View view, int pos, int typeClick) {
        if (typeClick == 3) {
            if (pos >= 0 && pos < gioHangList.size()) {
                GioHang gioHangCanXoa = gioHangList.get(pos);

                compositeDisposable.add(appDatabase.gioHangDAO().deleteById(gioHangCanXoa.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    // << THAY ĐỔI CỐT LÕI >>
                                    // Xóa thành công trong DB, không tự sửa UI nữa.
                                    // Thay vào đó, gọi lại hàm tải dữ liệu để đồng bộ lại từ đầu.
                                    loadDataFromDatabase();
                                    Toast.makeText(this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                                },
                                throwable -> Toast.makeText(this, "Lỗi khi xóa: " + throwable.getMessage(), Toast.LENGTH_SHORT).show()
                        ));
            }
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
