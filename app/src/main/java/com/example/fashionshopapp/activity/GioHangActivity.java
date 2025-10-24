package com.example.fashionshopapp.activity;

import android.content.Intent;
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
import com.example.fashionshopapp.model.EventBus.TinhTongEvent; // ⭐ 1. IMPORT
import com.example.fashionshopapp.model.GioHang;
import com.example.fashionshopapp.util.Utils;

import org.greenrobot.eventbus.EventBus; // ⭐ 1. IMPORT
import org.greenrobot.eventbus.Subscribe; // ⭐ 1. IMPORT
import org.greenrobot.eventbus.ThreadMode; // ⭐ 1. IMPORT

import java.io.Serializable; // ⭐ 1. IMPORT
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
    long tongTien;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gio_hang);

        initView();
        initControl();
        loadDataFromDatabase();
    }

    // ⭐ 2. HÀM TÍNH TỔNG TIỀN ĐƯỢC GIỮ NGUYÊN
    private void calculateTotalPrice() {
        long tongtiensp = 0;
        for (int i = 0; i < Utils.mangmuahang.size(); i++) {
            tongtiensp += Utils.mangmuahang.get(i).getGiasp() * Utils.mangmuahang.get(i).getSoluong();
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        // Cập nhật biến tổng tiền toàn cục để gửi đi
        this.tongTien = tongtiensp;
        txtTongTien.setText(decimalFormat.format(tongtiensp) + "đ");
    }


    // ⭐ 3. SỬA LẠI NÚT MUA HÀNG
    private void initControl() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(gioHangAdapter);

        btnMuaHang.setOnClickListener(v -> {
            // Logic mới: Kiểm tra Utils.mangmuahang thay vì gioHangList
            if (Utils.mangmuahang.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Vui lòng chọn sản phẩm để mua", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(getApplicationContext(), ThanhToanActivity.class);
                intent.putExtra("tongtien", tongTien);
                // Gửi danh sách sản phẩm được chọn đi
                intent.putExtra("danhsachmua", (Serializable) Utils.mangmuahang);
                startActivity(intent);
            }
        });
    }

    // ⭐ 4. ĐĂNG KÝ, LẮNG NGHE VÀ HỦY EVENTBUS
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onTinhTongEvent(TinhTongEvent event) {
        if (event != null) {
            calculateTotalPrice();
        }
    }

    // Các hàm còn lại (initView, loadData, updateCartView, onItemClick,...) giữ nguyên
    // Vì chúng vẫn cần thiết để quản lý việc tăng/giảm/xóa sản phẩm khỏi giỏ hàng tổng.

    @Override
    protected void onResume() {
        super.onResume();
        loadDataFromDatabase();
        // Xóa các sản phẩm đã chọn khi quay lại để bắt đầu lại
        Utils.mangmuahang.clear();
        // Cập nhật tổng tiền (sẽ về 0)
        calculateTotalPrice();
    }

    // ... (Các hàm còn lại không thay đổi)
    private void loadDataFromDatabase() {
        compositeDisposable.add(appDatabase.gioHangDAO().getAllCartItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::updateCartView,
                        throwable -> Toast.makeText(getApplicationContext(), "Lỗi khi tải giỏ hàng: " + throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }

    private void updateCartView(List<GioHang> listFromDb) {
        if (listFromDb == null || listFromDb.isEmpty()) {
            txtGioHangTrong.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            this.gioHangList.clear();
        } else {
            txtGioHangTrong.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            this.gioHangList.clear();
            this.gioHangList.addAll(listFromDb);
        }
        gioHangAdapter.notifyDataSetChanged();
        calculateTotalPrice();
    }
    private void initView() {
        toolbar = findViewById(R.id.toolbar_giohang);
        recyclerView = findViewById(R.id.recyclerview_giohang);
        txtGioHangTrong = findViewById(R.id.txt_giohangtrong);
        txtTongTien = findViewById(R.id.txt_tongtien);
        btnMuaHang = findViewById(R.id.btn_muahang);
        appDatabase = AppDatabase.getInstance(this);
        gioHangAdapter = new GioHangAdapter(this, gioHangList, this);
    }
    @Override
    public void onItemClick(View view, int pos, int typeClick) {
        if (pos < 0 || pos >= gioHangList.size()) return;
        final GioHang gioHang = gioHangList.get(pos);
        switch (typeClick) {
            case 1:
                if (gioHang.getSoluong() > 1) {
                    gioHang.setSoluong(gioHang.getSoluong() - 1);
                    updateItemInDatabase(gioHang);
                } else {
                    deleteItemFromDatabase(gioHang);
                }
                break;
            case 2:
                gioHang.setSoluong(gioHang.getSoluong() + 1);
                updateItemInDatabase(gioHang);
                break;
            case 3:
                deleteItemFromDatabase(gioHang);
                break;
        }
    }
    private void updateItemInDatabase(GioHang gioHang) {
        compositeDisposable.add(appDatabase.gioHangDAO().insertOrReplace(gioHang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            // Không cần làm gì ở đây vì EventBus sẽ lo việc tính tổng
                            // Chỉ cần báo cho adapter vẽ lại
                            gioHangAdapter.notifyDataSetChanged();
                            EventBus.getDefault().postSticky(new TinhTongEvent());
                        },
                        throwable -> Toast.makeText(this, "Lỗi khi cập nhật: " + throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }
    private void deleteItemFromDatabase(GioHang gioHang) {
        compositeDisposable.add(appDatabase.gioHangDAO().deleteByPrimaryKey(gioHang.getIdsp(), gioHang.getSizeId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            loadDataFromDatabase();
                            Toast.makeText(this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                        },
                        throwable -> Toast.makeText(this, "Lỗi khi xóa: " + throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }
    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
