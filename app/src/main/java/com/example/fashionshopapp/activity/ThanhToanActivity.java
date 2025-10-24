package com.example.fashionshopapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopapp.R;
import com.example.fashionshopapp.adapter.ThanhToanAdapter;
import com.example.fashionshopapp.model.AppDatabase;
import com.example.fashionshopapp.model.GioHang;
import com.example.fashionshopapp.retrofit.ApiBanHang;
import com.example.fashionshopapp.retrofit.RetrofitClient;
import com.example.fashionshopapp.util.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList; // ⭐ 1. IMPORT THÊM
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class ThanhToanActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView txtTongTien, txtSoDienThoai, txtEmail;
    TextInputEditText edtDiaChi;
    Button btnDatHang;
    RecyclerView recyclerView;
    long tongtien;
    List<GioHang> danhSachDaChon; // ⭐ 2. BIẾN MỚI ĐỂ LƯU DANH SÁCH ĐƯỢC CHỌN

    ApiBanHang apiBanHang;
    AppDatabase appDatabase;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);
        initView();
        initControl();
        getIntentData();
        // ⭐ Thay vì load từ DB, giờ ta hiển thị danh sách đã được chọn gửi qua
        displaySelectedProducts();
    }

    private void getIntentData() {
        tongtien = getIntent().getLongExtra("tongtien", 0);
        // ⭐ Nhận danh sách sản phẩm đã được check từ GioHangActivity
        danhSachDaChon = (List<GioHang>) getIntent().getSerializableExtra("danhsachmua");
        if (danhSachDaChon == null) {
            danhSachDaChon = new ArrayList<>(); // Tránh lỗi NullPointerException
        }

        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        txtTongTien.setText("Tổng tiền: " + decimalFormat.format(tongtien) + "đ");
    }

    // ⭐ Hàm mới để hiển thị danh sách đã chọn lên RecyclerView
    private void displaySelectedProducts() {
        ThanhToanAdapter adapter = new ThanhToanAdapter(this, danhSachDaChon);
        recyclerView.setAdapter(adapter);
    }

    // Hàm loadProductListFromDb() không còn cần thiết, bạn có thể xóa nó
    // private void loadProductListFromDb() { ... }


    private void initControl() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        if (Utils.user_current != null) {
            txtEmail.setText(Utils.user_current.getEmail());
            txtSoDienThoai.setText(Utils.user_current.getSodienthoai());
            if (Utils.user_current.getDiachi() != null && !Utils.user_current.getDiachi().isEmpty()) {
                edtDiaChi.setText(Utils.user_current.getDiachi());
            }
        }

        btnDatHang.setOnClickListener(v -> {
            String diaChiGiaoHang = edtDiaChi.getText().toString().trim();
            if (TextUtils.isEmpty(diaChiGiaoHang)) {
                Toast.makeText(getApplicationContext(), "Vui lòng nhập địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Utils.user_current == null) {
                Toast.makeText(getApplicationContext(), "Lỗi: Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
                return;
            }

            // ⭐ Thay vì gọi processCheckout, ta kiểm tra và gọi thẳng postOrderToServer
            if (danhSachDaChon == null || danhSachDaChon.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Không có sản phẩm nào được chọn để đặt hàng.", Toast.LENGTH_SHORT).show();
                return;
            }

            btnDatHang.setEnabled(false);
            postOrderToServer(diaChiGiaoHang, danhSachDaChon);
        });
    }

    // Hàm processCheckout() không còn cần thiết vì ta đã có danh sách
    // private void processCheckout(String diaChiGiaoHang) { ... }

    private void postOrderToServer(String diaChiGiaoHang, List<GioHang> gioHangList) {
        // ⭐ Hàm này không thay đổi, nó đã đúng khi nhận vào một List<GioHang>
        String chitietJson = new Gson().toJson(gioHangList);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                "{" +
                        "\"user_id\":" + Utils.user_current.getId() + "," +
                        "\"diachi\":\"" + diaChiGiaoHang + "\"," +
                        "\"tongtien\":" + tongtien + "," +
                        "\"chitiet\":" + chitietJson +
                        "}"
        );

        compositeDisposable.add(apiBanHang.datHang(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel.isSuccess()) {
                                // ⭐ Sửa hàm xóa: Chỉ xóa những sản phẩm đã mua
                                clearPurchasedItemsInDatabase(gioHangList);
                            } else {
                                Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_LONG).show();
                                btnDatHang.setEnabled(true);
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Lỗi kết nối: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            btnDatHang.setEnabled(true);
                        }
                ));
    }

    // ⭐ Thay thế hàm clearCartInDatabase() bằng hàm này
    private void clearPurchasedItemsInDatabase(List<GioHang> purchasedItems) {
        // Xóa từng sản phẩm đã mua khỏi RoomDB
        for (GioHang item : purchasedItems) {
            appDatabase.gioHangDAO().deleteByPrimaryKey(item.getIdsp(), item.getSizeId())
                    .subscribeOn(Schedulers.io())
                    .subscribe(); // Chạy lệnh xóa
        }

        // Thông báo và quay về màn hình chính
        Toast.makeText(getApplicationContext(), "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
        // Dọn dẹp mảng mua hàng để chuẩn bị cho lần sau
        Utils.mangmuahang.clear();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    private void initView() {
        toolbar = findViewById(R.id.toolbarthanhtoan);
        txtTongTien = findViewById(R.id.txtTongTienThanhToan);
        txtSoDienThoai = findViewById(R.id.txtSdtThanhToan);
        txtEmail = findViewById(R.id.txtEmailThanhToan);
        edtDiaChi = findViewById(R.id.edtDiaChiThanhToan);
        btnDatHang = findViewById(R.id.btnDatHang);
        recyclerView = findViewById(R.id.recycleview_sanpham_thanhtoan);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        appDatabase = AppDatabase.getInstance(this);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
