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
    long tongTien; // Biến để lưu tổng tiền cho Intent

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
        // Biến tongTien đã được khai báo ở trên cùng
        tongTien = 0;
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
        btnMuaHang.setOnClickListener(v -> {
            // gioHangList là danh sách sản phẩm hiện tại trong giỏ
            if (gioHangList.isEmpty()) {
                // Nếu không có sản phẩm, thông báo cho người dùng
                Toast.makeText(getApplicationContext(), "Giỏ hàng của bạn đang trống, không thể thanh toán", Toast.LENGTH_SHORT).show();
            } else {
                // Nếu có sản phẩm, chuyển sang màn hình ThanhToanActivity
                Intent intent = new Intent(getApplicationContext(), ThanhToanActivity.class);
                // Truyền tổng số tiền sang màn hình thanh toán để hiển thị
                intent.putExtra("tongtien", tongTien);
                startActivity(intent);
            }
        });
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


    // HÀM XỬ LÝ SỰ KIỆN TĂNG/GIẢM/XÓA
    @Override
    public void onItemClick(View view, int pos, int typeClick) {
        if (pos < 0 || pos >= gioHangList.size()) return;

        final GioHang gioHang = gioHangList.get(pos);

        switch (typeClick) {
            case 1: // ➖ Giảm
                if (gioHang.getSoluong() > 1) {
                    gioHang.setSoluong(gioHang.getSoluong() - 1);
                    // Cập nhật lại sản phẩm đã thay đổi trong DB
                    updateItemInDatabase(gioHang);
                } else {
                    // Nếu số lượng là 1, giảm nữa sẽ là xóa
                    deleteItemFromDatabase(gioHang);
                }
                break;

            case 2: // ➕ Tăng
                gioHang.setSoluong(gioHang.getSoluong() + 1);
                // Cập nhật lại sản phẩm đã thay đổi trong DB
                updateItemInDatabase(gioHang);
                break;

            case 3: // ❌ Xóa
                // Gọi hàm xóa trực tiếp
                deleteItemFromDatabase(gioHang);
                break;
        }
    }

    // HÀM MỚI: Cập nhật một item trong DB
    private void updateItemInDatabase(GioHang gioHang) {
        compositeDisposable.add(appDatabase.gioHangDAO().insertOrReplace(gioHang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            // Sau khi cập nhật DB thành công...
                            // 1. Tính lại tổng tiền
                            calculateTotalPrice();
                            // 2. THÊM DÒNG NÀY: Bắt buộc thông báo cho Adapter vẽ lại giao diện
                            gioHangAdapter.notifyDataSetChanged();
                        },
                        throwable -> Toast.makeText(this, "Lỗi khi cập nhật: " + throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }

    // HÀM MỚI: Xóa một item khỏi DB
    private void deleteItemFromDatabase(GioHang gioHang) {
        // THAY ĐỔI: Gọi hàm DAO mới deleteByPrimaryKey và truyền vào cả idsp và sizeId
        compositeDisposable.add(appDatabase.gioHangDAO().deleteByPrimaryKey(gioHang.getIdsp(), gioHang.getSizeId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            // Sau khi xóa thành công trong DB...
                            // 1. Tải lại toàn bộ dữ liệu từ DB để đảm bảo tính nhất quán
                            loadDataFromDatabase();
                            // 2. Thông báo cho người dùng
                            Toast.makeText(this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                            // Hàm loadDataFromDatabase() đã bao gồm cả việc tính lại tổng tiền và notifyDataSetChanged() nên không cần gọi lại ở đây.
                        },
                        throwable -> Toast.makeText(this, "Lỗi khi xóa: " + throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Luôn tải lại dữ liệu mỗi khi quay lại màn hình này
        // để đảm bảo dữ liệu luôn mới nhất sau khi xóa/cập nhật.
        loadDataFromDatabase();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
